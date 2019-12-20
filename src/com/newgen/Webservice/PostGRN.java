/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import org.json.JSONException;
import org.json.JSONObject;

public class PostGRN {

    FormReference formObject;

    public String parseGRNOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        JSONObject objJSONObject = new JSONObject(content);
        String IsSuccess = objJSONObject.optString("IsSuccess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");

        if (IsSuccess.equalsIgnoreCase("true")) //Set Header Details
        {
            formObject.setNGValue("grnnumber", objJSONObject.optString("Voucher"));
            formObject.setNGValue("storestatus", "Accepted");
            formObject.setEnabled("storestatus", false);
            formObject.setVisible("Btn_GenerateGRN", false);
            formObject.setVisible("Btn_CancelGRN", true);
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }

    public String parseGRNCancellationOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        JSONObject objJSONObject = new JSONObject(content);
        String IsSuccess = objJSONObject.optString("IsSuccess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");

        if (IsSuccess.equalsIgnoreCase("true")) //Set Header Details
        {
            formObject.setNGValue("grnnumber", "");
            formObject.setNGValue("storestatus", "");
            formObject.setEnabled("storestatus", true);
            formObject.setVisible("Btn_GenerateGRN", true);
            formObject.setVisible("Btn_CancelGRN", false);
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }
}
