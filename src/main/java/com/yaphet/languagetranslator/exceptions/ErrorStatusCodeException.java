package com.yaphet.languagetranslator.exceptions;

public class ErrorStatusCodeException extends Exception {

    public ErrorStatusCodeException(int statusCode){
        super(String.format("Translation call failed. Call returns %d error code",statusCode));
    }
}
