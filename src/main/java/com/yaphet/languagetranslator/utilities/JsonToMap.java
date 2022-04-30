package com.yaphet.languagetranslator.utilities;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@RequiredArgsConstructor
public class JsonToMap {
    private  final ClassPathResource languageResource;


    public Map<String,String> getMap() throws IOException {
        BufferedReader reader=new BufferedReader(new InputStreamReader(languageResource.getInputStream()));
        StringBuilder builder=new StringBuilder();
        String line;
        while((line= reader.readLine())!=null){
            builder.append(line);
        }
        reader.close();
        Gson gson=new Gson();
        return gson.fromJson(builder.toString(),Map.class);
    }
}
