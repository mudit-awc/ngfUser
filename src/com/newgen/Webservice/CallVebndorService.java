/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author V_AWC
 */
public class CallVebndorService {

    FormReference formObject;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    ServiceConnection objServiceConnection = null;
    String webserviceStatus;

    public void GetSetPrePaymentLines(String AccessToken, String VendorCode) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objServiceConnection = new ServiceConnection();
        objReadProperty = new ReadProperty();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("_vendAccount", VendorCode);

            String outputJSON = objServiceConnection.callBearerAuthWebService(
                    AccessToken,
                    objReadProperty.getValue("getVendorData"),
                    request_json.toString().trim()
            );
            System.out.println("outputJSON : " + outputJSON);
            webserviceStatus = parseVendorOutputJSON(outputJSON);
        } catch (JSONException ex) {
            Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("IsStatus return :call purchase order : " + webserviceStatus);
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    public String parseVendorOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        JSONObject objJSONObject = new JSONObject(content);
        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :call purchase order :" + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {

            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }
}
