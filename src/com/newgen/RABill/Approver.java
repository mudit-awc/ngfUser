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
        String levelflag_ = formObject.getNGValue("levelflag");
        System.out.println("Level Flag : " + levelflag_);
        int levelflag = Integer.parseInt(levelflag_) + 1;
        System.out.println("level flag : " + levelflag);
        String state = formObject.getNGValue("state");
        System.out.println("State : " + state);

        if (filestatus.equalsIgnoreCase("Approved")) {
            try {
                System.out.println("inside Approver");
                Query = "select TOP 1 ApproverCode from RABillApproverMaster where Head = 'RABill' "
                        + "and ApproverLevel='" + levelflag + "'and State ='" + state + "'"
                        + "and approvercode is not null "
                        + "and approvercode <> ''";
                System.out.println("Query:" + Query);
                result = formObject.getDataFromDataSource(Query);
                System.out.println("result" + result);
                if (result.size() > 0) {
                    System.out.println("assignto " + result.get(0).get(0));
                    System.out.println("levelflag " + levelflag);
                    formObject.setNGValue("assignto", result.get(0).get(0));
                    formObject.setNGValue("levelflag", levelflag);
                    formObject.setNGValue("nextactivity", "Approver");
                } else {
                    System.out.println("inside else of approver");

                    Query = "select ApproverLevel, ApproverCode from RABillApproverMaster where  "
                            + "head = 'RABill' "
                            + "and state = '" + formObject.getNGValue("state") + "' "
                            + "and approverlevel in ('Maker', 'Checker')";
                    System.out.println("query  for level is " + Query);
                    result = formObject.getDataFromDataSource(Query);
                    if (result.size() > 0) {
                        if (result.get(0).get(0).equalsIgnoreCase("Maker")) {
                            formObject.setNGValue("levelflag", "Maker");
                        } else if (result.get(levelflag).get(0).equalsIgnoreCase("Checker")) {
                            formObject.setNGValue("levelflag", "Checker");
                        }
                        formObject.setNGValue("assignto", result.get(0).get(1));
                        formObject.setNGValue("nextactivity", "Accounts");
                    } else {
                        formObject.setNGValue("nextactivity", "SchedulerAccount");
                        //formObject.setNGValue("assignto", "NA");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Query Raised")) {
            formObject.setNGValue("nextactivity", "Indexer");
            formObject.setNGValue("assignto", formObject.getNGValue("CreatedByName"));
        }
        objAccountsGeneral.getsetRABILLSummary(processInstanceId);
        formObject.setNGValue("previousactivity", activityName);
        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text69"), "q_transactionhistory");

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
