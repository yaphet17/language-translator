package com.yaphet.languagetranslator;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LanguageTranslatorApplication {

    public static void main(String[] args) {
       Application.launch(LanguageTranslatorApplicationUI.class,args);
    }

}
