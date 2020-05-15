/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.NonPoInvoice;

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
 * @author Richa Maheshwari
 */
public class Approver implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    List<List<String>> result1;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        // TODO Auto-generated method stub
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        //objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "":

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
    }

    @Override
    public void formLoaded(FormEvent arg0) {
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
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
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated........---------------------------");

        formObject.setSheetVisible("Tab1", 1, false);
        formObject.setSheetVisible("Tab1", 2, false);
        formObject.setSheetVisible("Tab1", 3, false);
        formObject.clear("filestatus");
        formObject.setNGValue("filestatus", "");
        formObject.addComboItem("filestatus", "Approve", "Approve");
        if (!formObject.getNGValue("levelflag").equals("1")) {
            formObject.addComboItem("filestatus", "Reject", "Reject");
        }
        formObject.addComboItem("filestatus", "Query Raise", "Query Raise");
        formObject.addComboItem("filestatus", "Discard", "Discard");

        Query = "select HeadName from ServiceNonPoHeadMaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result is" + result);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }

        Query = "select sitecode from sitemaster order by sitecode asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result is" + result);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("site", result.get(i).get(0), result.get(i).get(0));
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
        objGeneral = new General();
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        String filestatus = formObject.getNGValue("filestatus");
        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag"));
        if (filestatus.equalsIgnoreCase("Approve")) {
            levelflag = levelflag + 1;
            objGeneral.checkServiceNonPoDoAUser(String.valueOf(levelflag), "Approver");
        } else if (filestatus.equalsIgnoreCase("Reject")) {
            levelflag = levelflag - 1;
            formObject.setNGValue("FilterDoA_ApproverLevel", levelflag);
            formObject.setNGValue("levelflag", levelflag);
        }
//        formObject.setNGValue("nextactivity", nextactivity);
        formObject.setNGValue("previousactivity", activityName);
        objGeneral.maintainHistory(
                userName,
                activityName,
                filestatus,
                "",
                formObject.getNGValue("Text51"),
                "q_transactionhistory"
        );
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
