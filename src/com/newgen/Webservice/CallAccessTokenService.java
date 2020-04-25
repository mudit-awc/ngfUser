/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.ReadProperty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author V_AWC
 */
public class CallAccessTokenService {

    ReadProperty objReadProperty = null;

    public String getAccessToken() {
        objReadProperty = new ReadProperty();
        String token = "";
        try {
            System.out.println("inside token class");
            String serviceURL = objReadProperty.getValue("getToken");

            StringBuilder queryParam = new StringBuilder();
            queryParam.append("grant_type=");
            queryParam.append(objReadProperty.getValue("grant_type"));
            queryParam.append("&");

            queryParam.append("client_id=");
            queryParam.append(objReadProperty.getValue("client_id"));
            queryParam.append("&");

            queryParam.append("client_secret=");
            queryParam.append(objReadProperty.getValue("client_secret"));
            queryParam.append("&");

            queryParam.append("resource=");
            queryParam.append(objReadProperty.getValue("resource"));

            URL url = new URL(serviceURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(queryParam.toString().getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String outputJSON = br.readLine();
           // System.out.println("Output JSON.... " + outputJSON);
            JSONObject objJSONObject = new JSONObject(outputJSON);
            token = objJSONObject.optString("access_token");
            return token;
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException : " + ex);
        } catch (ProtocolException ex) {
            System.out.println("ProtocolException : " + ex);
        } catch (IOException ex) {
            System.out.println("IOException : " + ex);
        } catch (JSONException ex) {
            System.out.println("JSONException : " + ex);
        }
        return token;
    }

}
