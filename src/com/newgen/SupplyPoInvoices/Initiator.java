/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.GetSetGateEntryData;
import com.newgen.Webservice.GetSetPurchaseOrderData;
import com.newgen.Webservice.PostGRN;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;

import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.json.JSONObject;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.IRepeater;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.json.JSONException;

public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    GetSetPurchaseOrderData objGetSetPurchaseOrderData = null;
    GetSetGateEntryData objGetSetGateEntryData = null;
    PostGRN objPostGRN = null;

    String activityName = null;
    String engineName = null;
    String sessionId = null;
    String folderId = null;
    String FILE = null;
    String serverUrl = null;
    String processInstanceId = null;
    String workItemId = null;
    String userName = null;
    String processDefId = null;
    String Query = null;
    List<List<String>> result;
    private String webserviceStatus;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        objGeneral = new General();
        objGetSetPurchaseOrderData = new GetSetPurchaseOrderData();
        objGetSetGateEntryData = new GetSetGateEntryData();
        objReadProperty = new ReadProperty();
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {

                    case "purchaseorderno":

                        String purchaseorderno = formObject.getNGValue("purchaseorderno");
                        if (!purchaseorderno.equalsIgnoreCase("")) {
                            populatePurchaseOrder(purchaseorderno);
                        }
                        break;

                    case "invoiceno":

                        String invoiceno = formObject.getNGValue("invoiceno");
                        String purchaseorderno_ge = formObject.getNGValue("purchaseorderno");
                        if (!invoiceno.equalsIgnoreCase("")
                                && !purchaseorderno_ge.equalsIgnoreCase("")) {
                            populateGateEntry(purchaseorderno_ge, invoiceno);
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }

                break;
            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }

                break;
        }
        /* if (pEvent.getType().name().equalsIgnoreCase("VALUE_CHANGED")) {
         if (activityName.equalsIgnoreCase("Initiator")
         && pEvent.getSource().getName().equalsIgnoreCase("purchaseorderno")) {
         boolean IsStatus = false;
         try {
         JSONObject request_json = new JSONObject();
         request_json.put("PONumber", formObject.getNGValue("purchaseorderno"));
         String outputJSON = objGeneral.callWebService(
         objReadProperty.getValue("getPOData"),
         request_json.toString()
         );
         objGetSetPurchaseOrderData.parsePoJSON(outputJSON);
         } catch (JSONException ex) {
         Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
         }
         if (IsStatus == false) {
         System.out.println("Inside IsStatus False");
         throw new ValidatorException(new FacesMessage("Error", ""));
         }
         }
         if (activityName.equalsIgnoreCase("Initiator")
         && pEvent.getSource().getName().equalsIgnoreCase("invoiceno")) {
         try {
         JSONObject request_json = new JSONObject();
         request_json.put("PONumber", formObject.getNGValue("purchaseorderno"));
         request_json.put("ChallanNumber", formObject.getNGValue("invoiceno"));
         String outputJSON = objGeneral.callWebService(
         objReadProperty.getValue("getGateEntryData"),
         request_json.toString()
         );
         objGetSetGateEntryData.parseGateEntryJSON(outputJSON);
         } catch (JSONException ex) {
         Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
         }

         }
         }
         if (pEvent.getType().name().equalsIgnoreCase("TAB_CLICKED")) {
         System.out.println("------------Inside Tab------------------");

         if (pEvent.getSource().getName().equalsIgnoreCase("Tab1")) {
         }
         }

         if (pEvent.getType().name().equalsIgnoreCase("MOUSE_CLICKED")) {
         System.out.print("------------Inside Mouse Click------------------");

         if (pEvent.getSource().getName().equalsIgnoreCase("Button1")) {
         //  throw new ValidatorException(new CustomExceptionHandler("Mail_Hrms", email_combo, "", new HashMap()));
         }

         } */
    }

    @Override
    public void formLoaded(FormEvent arg0) {
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {

            activityName = formObject.getWFActivityName();
            engineName = formConfig.getConfigElement("EngineName");
            sessionId = formConfig.getConfigElement("DMSSessionId");
            folderId = formConfig.getConfigElement("FolderId");
            serverUrl = formConfig.getConfigElement("ServletPath");
            processInstanceId = formConfig.getConfigElement("ProcessInstanceId");
            workItemId = formConfig.getConfigElement("WorkitemId");
            userName = formConfig.getConfigElement("UserName");
            processDefId = formConfig.getConfigElement("ProcessDefId");

            System.out.println("ProcessInstanceId===== " + processInstanceId);
            System.out.println("Activityname=====" + activityName);
            System.out.println("CabinetName====" + engineName);
            System.out.println("sessionId====" + sessionId);
            System.out.println("Username====" + userName);
            System.out.println("workItemId====" + workItemId);
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");

        String purchaseorderno = formObject.getNGValue("purchaseorderno");
        if (!purchaseorderno.equalsIgnoreCase("")) {
            populatePurchaseOrder(purchaseorderno);
            String invoiceno = formObject.getNGValue("invoiceno");
            if (!invoiceno.equalsIgnoreCase("")) {
                populateGateEntry(purchaseorderno, invoiceno);
            }
        }
    }

    @Override
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormCompleted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("******activityName****" + activityName);
        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
        int RowCount_gateentrylines = ListViewq_gateentrylines.getRowCount();
        if (RowCount_gateentrylines == 0) {
            throw new ValidatorException(new FacesMessage("Kindly fetch the gate entry details", ""));
        }
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void populatePurchaseOrder(String purchaseorderno) {
        System.out.println("Inside populatePurchaseOrder");
        try {
            JSONObject request_json = new JSONObject();
            request_json.put("PONumber", purchaseorderno);
            String outputJSON = objGeneral.callWebService(
                    objReadProperty.getValue("getPOData"),
                    request_json.toString()
            );
            webserviceStatus = objGetSetPurchaseOrderData.parsePoOutputJSON(outputJSON);
            System.out.println("IsStatus return : " + webserviceStatus);
        } catch (Exception ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    void populateGateEntry(String purchaseorderno, String invoiceno) {
        System.out.println("Inside populateGateEntry");
        try {
            JSONObject request_json = new JSONObject();
            request_json.put("PONumber", purchaseorderno);
            request_json.put("ChallanNumber", invoiceno);
            String outputJSON = objGeneral.callWebService(
                    objReadProperty.getValue("getGateEntryData"),
                    request_json.toString()
            );
            webserviceStatus = objGetSetGateEntryData.parseGateEntryOutputJSON(outputJSON);
        } catch (JSONException ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }
}
