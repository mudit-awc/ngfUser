/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author AWCLappy170
 */
public class GetSetGateEntryData implements Serializable {

    FormReference formObject;

    public String parseGateEntryOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String GateLineContractXML = "";
        JSONObject objJSONObject = new JSONObject(content);

        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            //Set Header Details
            formObject.setNGValue("gateentryid", objJSONObject.optString("GateEntryId"));
            formObject.setNGValue("lrno", objJSONObject.optString("LRNumber"));
            formObject.setNGValue("lrdate", objJSONObject.optString("LRDate"));
            formObject.setNGValue("transportercode", objJSONObject.optString("TransporterCode"));
            formObject.setNGValue("transportername", objJSONObject.optString("TransporterName"));
            formObject.setNGValue("vehicleno", objJSONObject.optString("VehicleNumber"));

            //Set Line Details
            JSONArray objJSONArray_GateLineContract = objJSONObject.getJSONArray("GateLineContract");
            for (int i = 0; i < objJSONArray_GateLineContract.length(); i++) {
                GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("ItemId")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("ItemName")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("VendorChallanQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("GRNQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("FirstWeight")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("SecondWeight")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("NetWeight")).
                        append("</SubItem></ListItem>").toString();
            }
            //System.out.println((new StringBuilder()).append("PO Line XML :").append(POLineContractXML).toString());
            formObject.clear("q_gateentrylines");
            formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }
}
