/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Admin
 */
public class CallCLMSService {

    FormReference formObject;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    String webserviceStatus;

    public void GetSetCLMSS(String InvoiceNO, String PONumber) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("inside GetSetCLMSS function");
        try {
            System.out.println("inside clms class ");
            System.out.println("clms InvoiceNO : " + InvoiceNO);
            System.out.println("clms PONumber : " + PONumber);
            objReadProperty = new ReadProperty();
            JSONObject request_json = new JSONObject();
            try {
                System.out.println("inside try");
                request_json.put("InvoiceNo", InvoiceNO);
                request_json.put("PurchaseOrderNo", PONumber);
            } catch (JSONException ex) {
                Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
            }

            String outputJSON = callClmsWebService(
                    objReadProperty.getValue("getClmsData") + "InvoiceNo=" + InvoiceNO + "&PurchaseOrderNo=" + PONumber,
                    request_json.toString().trim()
            );
            System.out.println("outputJSON farman: " + outputJSON);

            webserviceStatus = parseClmsOutputJSON(outputJSON);
            //  addToSypplyInvoice();

            System.out.println("IsStatus return :call purchase order : " + webserviceStatus);
        } catch (JSONException ex) {
            Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    private String parseClmsOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("inside parseClmsOutputJSON @");
        String ClmsContractXML = "", invoicedetailsLineNo = "", invoicedetailsItemNo = "", invoicedetailsQuantity = "";
        Calculations objCalculations = new Calculations();
        String getentry_lineno = "", getentry_itemno = "", invoice_lineno = "", invoice_itemno = "";
        //  Boolean invoicelinechecker = true;
        String unitprice_poline = "", taxgroup_poline = "";

        ListView ListViewq_polinedetails = (ListView) formObject.getComponent("q_polinedetails");
        int RowCountq_polinedetails = ListViewq_polinedetails.getRowCount();
        System.out.println("RowCountq_polinedetails : " + RowCountq_polinedetails);

        JSONObject objJSONObject = new JSONObject(content);

        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("Status");
        String ErrorMessage = objJSONObject.optString("Message");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :call purchase order :" + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            System.out.println("inside trueeeeeeeee");
            JSONArray objJSONArray_poLineList = objJSONObject.getJSONArray("_365InvoicCreationLineItems");
            System.out.println("objJSONArray_poLineList.length() " + objJSONArray_poLineList.length());
            for (int j = 0; j < objJSONArray_poLineList.length(); j++) {
                invoicedetailsLineNo = objJSONArray_poLineList.getJSONObject(j).optString("LineNo");
                invoicedetailsItemNo = objJSONArray_poLineList.getJSONObject(j).optString("ItemNo");
                invoicedetailsQuantity = objJSONArray_poLineList.getJSONObject(j).optString("Quantity");

                for (int t = 0; t < RowCountq_polinedetails; t++) {
                    if (invoicedetailsLineNo.equalsIgnoreCase(formObject.getNGValue("q_polinedetails", t, 0)) && invoicedetailsItemNo.equalsIgnoreCase(formObject.getNGValue("q_polinedetails", t, 1))) {
                        System.out.println("both are same");
                        unitprice_poline = formObject.getNGValue("q_polinedetails", t, 2);
                        taxgroup_poline = formObject.getNGValue("q_polinedetails", t, 16);
                        String calculatedvalues[] = objCalculations.calculateLineTotalWithTax(invoicedetailsQuantity, unitprice_poline, taxgroup_poline).split("/");
                        ClmsContractXML = (new StringBuilder()).append(ClmsContractXML).
                                append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(j).optString("LineNo")).
                                append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(j).optString("ItemNo")).
                                append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(j).optString("")).
                                append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(j).optString("Quantity")).
                                append("</SubItem><SubItem>").append(unitprice_poline).
                                append("</SubItem><SubItem>").append(calculatedvalues[1]).
                                append("</SubItem><SubItem>").append("").
                                append("</SubItem><SubItem>").append("").
                                append("</SubItem><SubItem>").append(calculatedvalues[3]).
                                append("</SubItem><SubItem>").append(calculatedvalues[2]).
                                append("</SubItem><SubItem>").append(calculatedvalues[0]).
                                append("</SubItem><SubItem>").append("").
                                append("</SubItem><SubItem>").append(formObject.getNGValue("ponumber")).
                                append("</SubItem></ListItem>").toString();
                    }
                }
            }
            try {
                formObject.NGAddListItem("q_invoicedetails", ClmsContractXML);

            } catch (Exception e) {
                System.out.println("Exception in Clms line item :" + e);
            }
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }

    private String callClmsWebService(String serviceURL, String inputJSON) {
        String outputJSON = "";
        try {

            System.out.println("inside my service clms");
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
