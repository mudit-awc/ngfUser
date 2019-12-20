package com.newgen.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class ReadProperty implements Serializable {

    Properties prop = new Properties();
    InputStream input = null;

    public String getValue(String parameter) {
        try {
            input = new FileInputStream(System.getProperty("user.dir") + File.separator + "WonderCement" + File.separator + "conf" + File.separator + "conf.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop.getProperty(parameter);
    }
}
