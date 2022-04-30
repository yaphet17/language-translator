package com.yaphet.languagetranslator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LanguageTranslatorApplicationUI extends Application {

    private ConfigurableApplicationContext applicationContext;
    @Override
    public void init(){
        applicationContext=new SpringApplicationBuilder(LanguageTranslatorApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }
    @Override
    public void stop(){
        applicationContext.close();
        Platform.exit();
    }
    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));

    }
    class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }
        public Stage getStage(){
            return ((Stage)getSource());
        }
    }
}
