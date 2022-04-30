package com.yaphet.languagetranslator.exceptions;

public class InvalidLanguageCodeException extends Exception{
    public InvalidLanguageCodeException(String language){
        super(String.format("Language %s is either invalid or not supported by google translation api.",language));
    }
}
