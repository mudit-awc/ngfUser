/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.SupplyPoInvoices.Initiator;
import static com.newgen.Webservice.CallCLMSService.DO_NOT_VERIFY;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class ServiceConnection {

    public static String callBearerAuthWebService(String AccessToken, String serviceURL, String inputJSON) {
        String outputJSON = "";

        System.out.println("Access Token...." + AccessToken);
        System.out.println("Service URL...." + serviceURL);
        System.out.println("Input JSON...." + inputJSON);
        try {
            URL url = new URL(serviceURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + AccessToken);

            OutputStream os = conn.getOutputStream();
            os.write(inputJSON.getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            outputJSON = br.readLine();
            System.out.println("Output JSON.... " + outputJSON);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputJSON;
    }

    public static String callBasiAuthWebService(String AccessToken, String serviceURL, String inputJSON) {
        String outputJSON = "";
        System.out.println("Access Token...." + AccessToken);
        System.out.println("Service URL...." + serviceURL);
        System.out.println("Input JSON...." + inputJSON);
       try {

            System.out.println("inside callBasiAuthWebService ");
            URL url = new URL(serviceURL);
            HttpURLConnection http = null;

            if (url.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", AccessToken);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            OutputStream out = http.getOutputStream();
            out.write(inputJSON.getBytes());
            out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            System.out.println("br :" + br);
            outputJSON = br.readLine();
            System.out.println("Output JSON.... " + outputJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputJSON;
    }
     final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}

