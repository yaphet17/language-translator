package com.yaphet.languagetranslator.controllers;

import com.yaphet.languagetranslator.exceptions.PropertiesFileNotFoundException;
import com.yaphet.languagetranslator.translator.Translator;
import com.yaphet.languagetranslator.utilities.JsonToMap;
import com.yaphet.languagetranslator.utilities.PropertiesLoader;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class LanguageTranslatorController {

    private  Stage chooseFile;
    private ClassPathResource languageResource = null;
    private final String resourceName = "application.properties";
    private Service<String> service;
    private final int MAXIMUM_LENGTH = 5000;


    private  String defaultSourceLang;

    private final Logger logger = LogManager.getLogger();
    @FXML
    public TextField browseField;
    @FXML
    public Button browseBtn;
    @FXML
    public Label statusLabel;
    @FXML
    public ProgressIndicator indicator;
    @FXML
    public ComboBox<String> sourceCombo;
    @FXML
    public ComboBox<String> targetCombo;
    @FXML
    public TextArea sourceBox;
    @FXML
    public TextArea translationBox;
    @FXML
    public Label characterLabel;
    @FXML
    public Button translateBtn;
    @FXML
    public Button exportBtn;

    public LanguageTranslatorController() {
        try {
            Properties properties = PropertiesLoader.loadProperties(resourceName);
            languageResource = new ClassPathResource(properties.getProperty("translator.api.languages.json"));
            defaultSourceLang=properties.getProperty("translator.api.default.source-lang");
        } catch (IOException e) {
            logger.error("failed to load properties file");
        }
    }

    @FXML
    public void initialize() {
        chooseFile=new Stage();

        //populate ComboBoxes
        List<String> languageList = getLanguageList();
        if (languageList != null) {
            sourceCombo.getItems().addAll(languageList);
            targetCombo.getItems().addAll(languageList);
        }

        //Make combo boxes searchable
        new AutoCompleteBox<>(sourceCombo);
        new AutoCompleteBox<>(targetCombo);

        //select default source language
        if(defaultSourceLang!=null){
            sourceCombo.getSelectionModel().select(defaultSourceLang);
        }


    }

    @FXML
    public void browse() {

        try {
            File file;
            //check if path is specified in browse field
            if(!browseField.getText().equals("")){
                file=new File(browseField.getText());
                if(!file.exists()){
                    showErrorMsg("File not found on specified path");
                    return;
                }
            }else{
                file=chooseFile();
            }
            if(file==null){
                showErrorMsg("File not selected");
                return;
            }
            FileInputStream fis=new FileInputStream(file);
            BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
            String path=file.getAbsolutePath();
            //show file path in browse text field
            browseField.setText(path);
            StringBuilder builder=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
            reader.close();
            fis.close();
            //show file content in source textarea
            sourceBox.setText(builder.toString());
            showSuccessMsg(String.format("File imported from %s",path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void translate() {
        if(!isReadyToTranslate()){
            return;
        }
        service = new Service<>() {
            @Override
            protected Task<String> createTask() {
                Translator translator;
                try {
                    translator = new Translator(sourceCombo.getSelectionModel().getSelectedItem(),
                            targetCombo.getSelectionModel().getSelectedItem(),
                            sourceBox.getText());
                } catch (PropertiesFileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return translator;

            }
        };
        service.start();
        if(service.isRunning()){
            serviceRunning();
        }
        service.setOnFailed(e -> serviceFailed(service));
        service.setOnSucceeded(e -> Platform.runLater(() -> serviceSucceeded()));
    }

    @FXML
    public void export() {
        try {
            File directory=chooseSaveFile();
            if(directory==null){
                showErrorMsg("Directory not selected");
                return;
            }
            String path=directory.getAbsolutePath();
            FileWriter fileWriter=new FileWriter(path);
            BufferedWriter writer=new BufferedWriter(fileWriter);
            writer.write(translationBox.getText());
            writer.close();
            fileWriter.close();
            showSuccessMsg(String.format("File exported to %s",path));
        } catch (IOException e) {
            showErrorMsg("some error occurred while exporting file");
            logger.error(e.getMessage());
        }

    }
    @FXML
    public void countCharacters(KeyEvent e) {
        //add (Ctrl+Enter)shortcut key to fire translateBtn
        if(e.isControlDown()&&e.getCode()== KeyCode.ENTER){
            translateBtn.fire();
            return;
        }
        //show number of characters in the source textarea
        int len = sourceBox.getText().length();
        if (len > MAXIMUM_LENGTH) {
            showSuccessMsg("Maximum character reached");
        }
        characterLabel.setText(String.format("%d characters",len));
    }
    @FXML
    public void copyToClipboard(){
        Clipboard clipboard=Clipboard.getSystemClipboard();
        Map<DataFormat,Object> map=new HashMap<>();
        map.put(DataFormat.PLAIN_TEXT,translationBox.getText());
        clipboard.setContent(map);
        showSuccessMsg("copied to clipboard");
    }
    @FXML
    public void clearFields(){
        sourceBox.clear();
        translationBox.clear();
    }
    private List<String> getLanguageList() {

        try {
            Map<String, String> map = new JsonToMap(languageResource).getMap();
            return new ArrayList<>(map.keySet());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    private File chooseFile(){
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text files","*.txt"));
        fileChooser.setTitle("Choose file");
        return fileChooser.showOpenDialog(chooseFile);
    }
    private File chooseSaveFile(){
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text files","*.txt"));
        fileChooser.setTitle("Save");
        return fileChooser.showSaveDialog(chooseFile);
    }
    private boolean isReadyToTranslate() {
        if(isSourceTextEmpty()){
            return false;
        }
        if(!isTargetLangSelected()){
            return false;
        }
        //clear status label if there are no errors
        statusLabel.setText("");
        return true;
    }
    private boolean isSourceTextEmpty() {
        if(sourceBox.getText().isBlank()){
            showErrorMsg("Enter source text");
            return true;
        }
        return false;
    }
    private boolean isTargetLangSelected() {
        //source language are selected by default so no need to check it
        if(targetCombo.getSelectionModel().isEmpty()){
            showErrorMsg("Select target language");
            return false;
        }
        return true;
    }
    private void serviceRunning(){
        indicator.setVisible(true);
        showSuccessMsg("translating text...");
        //prevent double-clicking
        translateBtn.setDisable(true);
    }
    private void serviceFailed(Service<String> service){
        //automatic retry for 3 times
        AtomicInteger fails = new AtomicInteger();
        if (fails.get() <= 3) {
            fails.getAndIncrement();
            service.reset();
            service.start();
        } else {
            showErrorMsg("Couldn't translate source text");
        }
        indicator.setVisible(false);
        translateBtn.setDisable(false);

    }
    private void serviceSucceeded() {
        translationBox.setText(service.getValue());
        statusLabel.setText("");
        indicator.setVisible(false);
        translateBtn.setDisable(false);
    }
    private void showErrorMsg(String msg){
        statusLabel.setStyle("-fx-text-fill: #FB1705");
        statusLabel.setText(msg);
    }
    private void showSuccessMsg(String msg){
        statusLabel.setStyle("-fx-text-fill: #0BC902");
        statusLabel.setText(msg);
    }

}