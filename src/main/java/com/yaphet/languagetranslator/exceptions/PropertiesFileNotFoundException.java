package com.yaphet.languagetranslator.exceptions;

public class PropertiesFileNotFoundException extends Exception{

    public PropertiesFileNotFoundException(String resourceName){
        super(String.format("Properties file %s not found in resource folder",resourceName));
    }
}
