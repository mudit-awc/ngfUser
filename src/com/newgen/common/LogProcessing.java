package com.newgen.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;

public final class LogProcessing {

    public static Logger summaryLogs = null;
    public static Logger errorLogs = null;
    public static Logger jsonLogs = null;
    public static Logger serverLogs = null;

    public static void settingLogFiles() {
        System.out.println("Inside Setting logs...");
        InputStream is = null;
        try {
            String filePath = System.getProperty("user.dir") + File.separatorChar + "WonderCement" + File.separator + "conf" + File.separatorChar + "WCLLog4j.properties";
            System.out.println(filePath + "File path");
            is = new BufferedInputStream(new FileInputStream(filePath));
            Properties ps = new Properties();
            ps.load(is);
            is.close();
            org.apache.log4j.LogManager.shutdown();
            PropertyConfigurator.configure(ps);

            summaryLogs = Logger.getLogger("summaryLogs");
            errorLogs = Logger.getLogger("errorLogs");
            jsonLogs = Logger.getLogger("jsonLogs");
            serverLogs = Logger.getLogger("serverLogs");
            dumpInitialLogs();

        } catch (Exception e) {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException te) {
                errorLogs.info("Error in setting Logger : " + te);
            }
            e.printStackTrace();
        }
    }

    public static void dumpInitialLogs() {
        errorLogs.info("==============================================");
        errorLogs.info("Error Log Initialized");
        errorLogs.info("==============================================");
        summaryLogs.info("=============================================");
        summaryLogs.info("Summary Log Initialized");
        summaryLogs.info("==============================================");
        jsonLogs.info("=============================================");
        jsonLogs.info("Json Log Initialized");
        jsonLogs.info("==============================================");
        serverLogs.info("=============================================");
        serverLogs.info("Server Log Initialized");
        serverLogs.info("==============================================");
    }

    public static void dumpFinalLogs() {
        errorLogs.info("==============================================");
        errorLogs.info("Error Log Ends");
        errorLogs.info("==============================================");
        summaryLogs.info("=============================================");
        summaryLogs.info("Summary Log Ends");
        summaryLogs.info("==============================================");
        jsonLogs.info("=============================================");
        jsonLogs.info("Json Log Ends");
        jsonLogs.info("==============================================");
        serverLogs.info("=============================================");
        serverLogs.info("Server Log Ends");
        serverLogs.info("==============================================");
    }
}
