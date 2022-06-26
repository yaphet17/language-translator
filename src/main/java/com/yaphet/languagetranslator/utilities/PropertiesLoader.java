package com.yaphet.languagetranslator.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadProperties(String resourceFileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(resourceFileName);

        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
}
