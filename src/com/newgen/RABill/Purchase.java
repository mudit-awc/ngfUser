/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.RABill;

import com.newgen.Webservice.CallCLMSService;
import com.newgen.common.AccountsGeneral;
import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Purchase implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    AccountsGeneral objAccountsGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    CallCLMSService objGetSetCLMSS = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objAccountsGeneral = new AccountsGeneral();
        objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Export_AbstractSheet":
                        objGeneral.openbamreport("AbstractSheet");
                        break;

                    case "Btn_Export_ItemJournal":
                        objGeneral.openbamreport("ItemJournal");
                        break;
                }
                break;
        }
    }

    @Override
    public void formLoaded(FormEvent arg0) {
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        System.out.println("form Loaded called : 20/05/2019");
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
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Hold", "Hold");
        formObject.addComboItem("filestatus", "Query Cleared", "Query Cleared");
        System.out.println("set the values in the combobox");

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
    }

    @Override
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormCompleted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormStarted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        if ("Query Cleared".equalsIgnoreCase(formObject.getNGValue("filestatus"))) {
            ListView listview = (ListView) formObject.getComponent("q_wfexception");
            int rowcount = listview.getRowCount();
            for (int i = 0; i <= rowcount; i++) {
                System.out.println("inside scond for Loop");
                if ("No".equalsIgnoreCase(formObject.getNGValue("q_wfexception", i, 4))) {
                    String exceptionlistview = "<ListItem>"
                            + "<SubItem></SubItem>"
                            + "<SubItem></SubItem>"
                            + "<SubItem></SubItem>"
                            + "<SubItem></SubItem>"
                            + "<SubItem>" + "Yes" + "</SubItem>"
                            + "<SubItem>" + objGeneral.getCurrentDate() + "</SubItem>"
                            + "<SubItem>" + userName + "</SubItem>"
                            + "<SubItem>" + formObject.getNGValue("text69") + "</SubItem>"
                            + "</ListItem>";
                    System.out.println("Exception list" + exceptionlistview);
                    formObject.NGModifyListViewAt("q_wfexception", i, exceptionlistview);
                }
            }
            objAccountsGeneral.getsetRABILLSummary(processInstanceId);
            objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text69"), "q_transactionhistory");
        } else {
            throw new ValidatorException(new FacesMessage("Kindly clear the exceptions to proceed further"));
        }
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
