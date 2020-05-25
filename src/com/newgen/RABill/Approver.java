/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.RABill;

import com.newgen.common.AccountsGeneral;
import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Admin
 */
public class Approver implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    List<List<String>> result;
    General objGeneral = null;
    AccountsGeneral objAccountsGeneral = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;

    PickList objPicklist;

    @Override
    public void formLoaded(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {
            engineName = formConfig.getConfigElement("EngineName");
            sessionId = formConfig.getConfigElement("DMSSessionId");
            folderId = formConfig.getConfigElement("FolderId");
            serverUrl = formConfig.getConfigElement("ServletPath");
            activityName = formObject.getWFActivityName();
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
    public void formPopulated(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        formObject.addComboItem("filestatus", "Discard", "Discard");
        try {
            Query = "select StateName from StateMaster order by StateCode asc";
            System.out.println("Query is " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("resut is " + result);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("state", result.get(i).get(0), result.get(i).get(0));
            }
            //for Location
            Query = "select WMSLocationId from WarehouseLocationMaster where WarehouseCode='" + formObject.getNGValue("warehouse") + "'";
            System.out.println("query" + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result" + result);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("location", result.get(i).get(0).toString(), result.get(i).get(0).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
    }

    @Override
    public void saveFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
    }

    @Override
    public void submitFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();

        String filestatus = formObject.getNGValue("filestatus");
        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag"));
        if (filestatus.equalsIgnoreCase("Approved")) {
            levelflag = levelflag + 1;
            objGeneral.checkRABillDoAUser(String.valueOf(levelflag), "Approver");
        } else if (filestatus.equalsIgnoreCase("Reject")) {
            levelflag = levelflag - 1;
            formObject.setNGValue("FilterDoA_ApproverLevel", levelflag);
            formObject.setNGValue("levelflag", levelflag);
        }
        formObject.setNGValue("previousactivity", activityName);
        objAccountsGeneral.getsetRABILLSummary(processInstanceId);
        objGeneral.maintainHistory(
                userName,
                activityName,
                filestatus,
                "",
                formObject.getNGValue("Text69"),
                "q_transactionhistory"
        );
    }

    @Override
    public void submitFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objAccountsGeneral = new AccountsGeneral();
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                }

                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }

                break;
        }
    }

    @Override
    public void continueExecution(String string, HashMap<String, String> hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
