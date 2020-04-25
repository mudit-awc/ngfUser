/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author AWCLappy170
 */
public class CallGateentryService implements Serializable {

    FormReference formObject;
    ReadProperty objReadProperty = null;
    ServiceConnection objServiceConnection = null;
    String webserviceStatus;

    public void GetSetGateEntry(String PONumber, String ChallanNumber) {
        objServiceConnection = new ServiceConnection();
        try {
            objReadProperty = new ReadProperty();
            JSONObject request_json = new JSONObject();
            try {
                request_json.put("PONumber", PONumber);
                request_json.put("ChallanNumber", ChallanNumber);
            } catch (JSONException ex) {
                Logger.getLogger(CallGateentryService.class.getName()).log(Level.SEVERE, null, ex);
            }
            String outputJSON = objServiceConnection.callBasiAuthWebService(
                    "U1AuUkVRVUVTVEVSOkFCQzEyMw==",
                    objReadProperty.getValue("getGateEntryData"),
                    request_json.toString().trim()
            );
            System.out.println("outputJSON : " + outputJSON);
            webserviceStatus = parseGateEntryOutputJSON(outputJSON);
            System.out.println("IsStatus return : call getentry :" + webserviceStatus);
        } catch (JSONException ex) {
            Logger.getLogger(CallGateentryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    public String parseGateEntryOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String GateLineContractXML = "";
        JSONObject obj = new JSONObject(content);
        JSONObject objJSONObject = obj.getJSONObject("d");

        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            //Set Header Details
            formObject.setNGValue("gateentryid", objJSONObject.optString("GateEntryCode"));
            formObject.setNGValue("lrno", objJSONObject.optString("LRNumber"));
            formObject.setNGValue("lrdate", objJSONObject.optString("LRDate"));
            formObject.setNGValue("transportercode", objJSONObject.optString("TransporterCode"));
            formObject.setNGValue("transportername", objJSONObject.optString("TransporterName"));
            formObject.setNGValue("vehicleno", objJSONObject.optString("VehicleNumber"));

            String freightServicePoNo = objJSONObject.optString("FreightServicePoNo");
            String freightRate = objJSONObject.optString("FreightRate");
            System.out.println("FreightServicePoNo " + freightServicePoNo);
            System.out.println("FreightRate " + freightRate);

            //Set Line Details
            JSONArray objJSONArray_GateLineContract = objJSONObject.getJSONArray("GateLineContract");
            for (int i = 0; i < objJSONArray_GateLineContract.length(); i++) {
                GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("LineNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("ItemId")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("ItemName")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("VendorChallanQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("GRNQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("FirstWeight")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("SecondWeight")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("NetWeight")).
                        append("</SubItem><SubItem>").append(freightServicePoNo).
                        append("</SubItem><SubItem>").append(freightRate).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(objJSONArray_GateLineContract.getJSONObject(i).optString("")).
                        append("</SubItem></ListItem>").toString();
            }
            System.out.println("outside line details GateEntry +++");
            //System.out.println((new StringBuilder()).append("PO Line XML :").append(POLineContractXML).toString());
            formObject.clear("q_gateentrylines");
            formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }
}
