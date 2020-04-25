/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.SupplyPoInvoices.Initiator;
import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author V_AWC
 */
public class CallGetStockDetailsService {

    FormReference formObject;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    String webserviceStatus;
    String[] quantity;
    String quan_flag = "";
    String pid;

    public void GetSetStockDetails(String AccessToken, String processInstanceId) {
        pid = processInstanceId;
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("inside GetSetStockDetails function");
        try {
            objReadProperty = new ReadProperty();
            JSONObject request_json = new JSONObject();

            String Query = "select itemnumber,configuration,location,sitecode,warehousecode,quantity "
                    + "from cmplx_raitemjournal where pinstanceid = '" + processInstanceId + "'";
            List<List<String>> result = formObject.getDataFromDataSource(Query);
            System.out.println("result : " + result);
            System.out.println("result ka size : " + result.size());
//            int sizee = result.size();
//            System.out.println("sizee : " + sizee);
            quantity = new String[result.size()];
            System.out.println("quantity :" + quantity);
            if (result.size() > 0) {
                System.out.println("inside if of result input json");
                quantity[0] = result.get(0).get(5);
                System.out.println("quantity at zero index :" + quantity[0]);
                JSONObject json1 = new JSONObject();
                JSONObject json2 = new JSONObject();
                JSONArray arr = new JSONArray();
                JSONObject json3 = new JSONObject();
                json3.put("_itemNumber", result.get(0).get(0)); //ITEM NUMBER
                json3.put("_inventSiteId", result.get(0).get(3)); //site code
                json3.put("_inventLocationId", result.get(0).get(4)); //warehouse code
                json3.put("_wmsLocationId", result.get(0).get(2)); //LOCATION
                json3.put("_inventBatchId", "");
                json3.put("_inventSerialId", "");
                json3.put("_configurationName", result.get(0).get(1)); //CONFIGURATION
                arr.put(json3);
                for (int i = 1; i < result.size(); i++) {
                    quantity[i] = result.get(i).get(5);
                    if (result.get(0).get(0).equalsIgnoreCase(result.get(i).get(0))) {
                        System.out.println("itemnumber same h");
                        if (result.get(0).get(1).equalsIgnoreCase(result.get(i).get(1))) {
                            System.out.println("configuration same h");
                            if (result.get(0).get(2).equalsIgnoreCase(result.get(i).get(2))) {
                                System.out.println("Location same h");
                                quan_flag = "same";
                                System.out.println("quan_flag same :" + quan_flag);
                            } else {
                                System.out.println("Location same nhi h");
                                JSONObject json6 = new JSONObject();
                                json6.put("_itemNumber", result.get(0).get(0)); //ITEM NUMBER
                                json6.put("_inventSiteId", result.get(i).get(3)); //site code
                                json6.put("_inventLocationId", result.get(i).get(4)); //warehouse code
                                json6.put("_wmsLocationId", result.get(i).get(2)); //LOCATION
                                json6.put("_inventBatchId", "");
                                json6.put("_inventSerialId", "");
                                json6.put("_configurationName", result.get(i).get(1)); //CONFIGURATION
                                arr.put(json6);
                                quan_flag = "diff";
                            }
                        } else {
                            System.out.println("configuration same nhi h");
                            JSONObject json5 = new JSONObject();
                            json5.put("_itemNumber", result.get(0).get(0)); //ITEM NUMBER
                            json5.put("_inventSiteId", result.get(i).get(3)); //site code
                            json5.put("_inventLocationId", result.get(i).get(4)); //warehouse code
                            json5.put("_wmsLocationId", result.get(i).get(2)); //LOCATION
                            json5.put("_inventBatchId", "");
                            json5.put("_inventSerialId", "");
                            json5.put("_configurationName", result.get(i).get(1)); //CONFIGURATION
                            arr.put(json5);
                            quan_flag = "diff";
                        }

                    } else {
                        System.out.println("itemnumber same nhi h");
                        JSONObject json4 = new JSONObject();
                        json4.put("_itemNumber", result.get(i).get(0)); //ITEM NUMBER
                        json4.put("_inventSiteId", result.get(i).get(3)); //site code
                        json4.put("_inventLocationId", result.get(i).get(4)); //warehouse code
                        json4.put("_wmsLocationId", result.get(i).get(2)); //LOCATION
                        json4.put("_inventBatchId", "");
                        json4.put("_inventSerialId", "");
                        json4.put("_configurationName", result.get(i).get(1)); //CONFIGURATION
                        arr.put(json4);
                        quan_flag = "diff";
                    }

                }
                json2.put("ItemList", arr);
                json1.put("_itemList", json2);
                System.out.println("input json1 :" + json1);

                String outputJSON = callStockDetailsWebService(
                        AccessToken,
                        objReadProperty.getValue("getStockDetailsData"),
                        //request_json.toString().trim()
                        json1.toString().trim()
                );
                System.out.println("outputJSON Get Stock Details: " + outputJSON);

                webserviceStatus = parseStockDetailsOutputJSON(outputJSON);
                //  addToSypplyInvoice();
                System.out.println("IsStatus return : get stock ::  " + webserviceStatus);
                ////}
            }
        } catch (JSONException ex) {
            Logger.getLogger(CallGetStockDetailsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    private String parseStockDetailsOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("inside parse parseStockDetailsOutputJSON OutputJSON @");
        JSONObject objJSONObject = new JSONObject(content);
        //Check webservice IsSuccess status
        String itemNumber = "", sitecode = "", configuration = "", location = "", availableQty = "", status = "";
        Float availavle_qty = null, input_qty = null;
        String IsSuccess = objJSONObject.optString("isSuccess");
        String ErrorMessage = objJSONObject.optString("errorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :call purchase order :" + ErrorMessage);
        String Query = "select itemnumber,configuration,sitecode,location from cmplx_raitemjournal where pinstanceid = '" + pid + "'";
        List<List<String>> result = formObject.getDataFromDataSource(Query);
        System.out.println("result : " + result);
        if (result.size() > 0) {
            if (IsSuccess.equalsIgnoreCase("true")) {
                JSONArray objJSONArray_ItemList = objJSONObject.getJSONArray("ItemList");
                for (int i = 0; i < objJSONArray_ItemList.length(); i++) {
                    System.out.println("i ki value: " + i);
                    itemNumber = objJSONArray_ItemList.getJSONObject(i).optString("_itemNumber");
                    configuration = objJSONArray_ItemList.getJSONObject(i).optString("_configurationName");

                    JSONObject objjson = objJSONArray_ItemList.optJSONObject(i);
                    JSONArray objJSONArray_InventList = objjson.getJSONArray("InventList");
                    System.out.println("objJSONArray_InventList: " + objJSONArray_InventList);
                    System.out.println("Len :" + objJSONArray_InventList.length());
                    if (objJSONArray_InventList.length() == 0) {
                        throw new ValidatorException(new FacesMessage("Quantity Out of Stock at Item Number " + itemNumber + " and configuration " + configuration + "", ""));
                    } else {
                        for (int j = 0; j < objJSONArray_InventList.length(); j++) {
                            System.out.println("Now Checking for Quantity ");
                            System.out.println("J ki value: " + j);
                            availableQty = objJSONArray_InventList.getJSONObject(j).optString("availableQty");
                            availavle_qty = Float.parseFloat(availableQty);
                            System.out.println("availavle_qty :" + availavle_qty);
                        }
                    }
                    String Query1 = "select itemnumber,configuration,quantity "
                            + "from cmplx_raitemjournal where pinstanceid = '" + pid + "'and itemnumber='" + itemNumber + "' and configuration ='" + configuration + "' ";
                    List<List<String>> result1 = formObject.getDataFromDataSource(Query1);
                    System.out.println("Query1 : " + Query1);
                    System.out.println("result1 : " + result1);
                    System.out.println("result1 : " + result1.get(0).get(2));

                    System.out.println("itemNumber :" + itemNumber);
                    System.out.println("configuration :" + configuration);
                    input_qty = Float.parseFloat(result1.get(0).get(2));
                    System.out.println("input_qty : " + input_qty);
                    System.out.println("availavle_qty: " + availavle_qty);
                    if (availavle_qty == null) {
                        System.out.println("zero set karadi ");
                        // availavle_qty = 0f;
                        throw new ValidatorException(new FacesMessage("Quantity Out of Stock at Item Number " + itemNumber + " and configuration " + configuration + "", ""));
                    }
                    System.out.println("availavle_qty -- " + availavle_qty);
                    if ((Float.compare(availavle_qty, input_qty) == 1) || (Float.compare(availavle_qty, input_qty) == 0)) {
                        System.out.println("Quantity Badi h sahi h");
                    } else {
                        System.out.println("error in Quantity chhoti h");
                        throw new ValidatorException(new FacesMessage("Quantity is Exceeding at Item Number " + itemNumber + " and configuration " + configuration + ""
                                + "\n" + " Available Quantity in Stock is " + availavle_qty + "", ""));
                    }
                }
                status = IsSuccess;
                throw new ValidatorException(new FacesMessage("Validation Successfull" + "", ""));
            } else {
                status = ErrorMessage;
            }
        }
        return status;
    }

    private String callStockDetailsWebService(String AccessToken, String serviceURL, String inputJSON) {
        String outputJSON = "";
        try {
            System.out.println("inside my service getstock details");
            System.out.println("AccessToken : " + AccessToken);
            System.out.println("URL : " + serviceURL);
            System.out.println("inputjson : " + inputJSON);

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
//    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
//        public boolean verify(String hostname, SSLSession session) {
//            return true;
//        }
//    };
}
