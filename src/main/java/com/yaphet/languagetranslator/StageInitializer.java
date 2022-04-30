package com.yaphet.languagetranslator;

import com.yaphet.languagetranslator.LanguageTranslatorApplicationUI.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("classpath:/ui/languagetranslator.fxml")
    private Resource resource;
    private final String stageTitle;
    private  final ApplicationContext applicationContext;

    private final Logger logger= LogManager.getLogger("com.yaphet.languagetranslator");

    public StageInitializer(@Value("${spring.application.ui.title}") String stageTitle, ApplicationContext applicationContext) {
        this.stageTitle=stageTitle;
        this.applicationContext=applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader=new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(e->applicationContext.getBean(e));
            Parent parent=fxmlLoader.load();
            Stage stage=event.getStage();
            Scene scene=new Scene(parent,840,620);
            stage.setScene(scene);
            stage.setTitle(stageTitle);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
