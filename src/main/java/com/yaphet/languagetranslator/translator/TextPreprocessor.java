package com.yaphet.languagetranslator.translator;

public class TextPreprocessor {
    private final int MAXIMUM_LENGTH=5000;

    public String process(String targetText){
        /* replace 2 or more space and newline with a single space
         * add whitespace between a comma and a whitespace
         * add whitespace between  a dot and a whitespace
        * */

        String processedText=targetText.trim().replaceAll(" \\s+","")
                .replaceAll("\\n"," ")
                .replaceAll(",\b(?! )",", ").replaceAll(".\b(?! )",". ");
        if(processedText.length()>MAXIMUM_LENGTH){
            //take the first 5000 characters if length of the source text is greater than number of characters allowed
            processedText=processedText.substring(0,MAXIMUM_LENGTH);
        }
        return processedText;
    }
}
