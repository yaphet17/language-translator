package com.yaphet.languagetranslator.translator;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.yaphet.languagetranslator.exceptions.ErrorStatusCodeException;
import com.yaphet.languagetranslator.exceptions.InvalidLanguageCodeException;
import com.yaphet.languagetranslator.exceptions.PropertiesFileNotFoundException;
import com.yaphet.languagetranslator.utilities.JsonToMap;
import com.yaphet.languagetranslator.utilities.PropertiesLoader;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class Translator extends Task<Optional<String>> {


    private final ClassPathResource languageResource;

    private  final TextPreprocessor textPreprocessor;
    private final Logger logger = LogManager.getLogger("com.yaphet.languagetranslator");
    private  final String URL;
    private final String from;
    private final String to;
    private final String text;

    public Translator(String from, String to, String text) throws PropertiesFileNotFoundException {
        //prevent the class from being instantiated if properties file is not found
        String propertiesFile = "application.properties";
        try {
            Properties properties= PropertiesLoader.loadProperties(propertiesFile);
            String resourceName= properties.getProperty("translator.api.languages.json");
            languageResource=new ClassPathResource(resourceName);
            URL=properties.getProperty("translator.api.wep-app.url");
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new PropertiesFileNotFoundException(propertiesFile);
        }
        this.from = from;
        this.to = to;
        this.text = text;

        textPreprocessor=new TextPreprocessor();
    }


    @Override
    protected Optional<String> call() throws InvalidLanguageCodeException, ErrorStatusCodeException {
        Optional<String> returnValue = Optional.empty();

        try {
            updateMessage("processing text");
            String processedText=  textPreprocessor.process(text);
            String sourceLang = getLanguageCode(from);
            String targetLang = getLanguageCode(to);
            if(sourceLang == null || targetLang == null){
                logger.error("Error occurred while retrieving language code");
                return returnValue;
            }
            updateMessage("translating");
            HttpResponse<String> response = Unirest.get(URL)
                    .header("content-type","application/x-www-form-urlencoded")
                    .header("Accept-Encoding","application/json")
                    .queryString("q", URLEncoder.encode(processedText, StandardCharsets.UTF_8))
                    .queryString("target", targetLang)
                    .queryString("source", sourceLang).asString();
            int statusCode = response.getStatus();
            if(statusCode != 200){
                logger.error(String.format("Translation call returns error code=%d", statusCode));
                throw new ErrorStatusCodeException(statusCode);
            }
            //decode and extract url
           returnValue = Optional.of(extractText(response.getBody()));
        } catch (UnirestException e) {
            logger.error(e.getMessage());
        }
        return returnValue;
    }
    private String extractText(String json){
        updateMessage("extracting response");
        Gson gson = new Gson();
        Map<String,String> map = gson.fromJson(json,Map.class);
        updateMessage("");
        return  URLDecoder.decode(map.get("translatedText"),StandardCharsets.UTF_8);
    }
    private String getLanguageCode(String language) throws InvalidLanguageCodeException {
        try {
            Map<String,String> languageMap = new JsonToMap(languageResource).getMap();
            //display error if language provided is not supported or invalid
            if(!languageMap.containsKey(language)){
                logger.error(String.format("Language %s is not found in languages.json",language));
                throw new InvalidLanguageCodeException(language);
            }
            return languageMap.get(language);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
