/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.ServicePoInvoice;

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
 * @author Richa Maheshwari
 */
public class Approver implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
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
    PickList objPicklist;
    General objGeneral = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        // TODO Auto-generated method stub
//        System.out.println("Value Change Event :" + pEvent);
//        System.out.println("pEvent.getType() :" + pEvent.getType());
//        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();

        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Add_Maintaincharges":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_othercharges");
                        break;

                    case "Btn_Modify_Maintaincharges":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_othercharges");
                        break;

                    case "Btn_Delete_Maintaincharges":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_othercharges");
                        break;

                    case "Btn_Add_Retention":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_retention");
                        break;

                    case "Btn_Modify_Retention":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_retention");
                        break;

                    case "Btn_Delete_Retention":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_retention");
                        break;

                    case "Btn_Add_Prepayment":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_prepayment");
                        break;

                    case "Btn_Modify_Prepayment":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_prepayment");
                        break;

                    case "Btn_Delete_Prepayment":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_prepayment");
                        break;

                    case "Btn_Delete_Invoice":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_invoicedetails");
                        formObject.RaiseEvent("WFSave");
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
        // TODO Auto-generated method stub
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        // TODO Auto-generated method stub
        System.out.println("form Loaded called : 20/05/2019");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {
            // objGeneral = new General();
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

//  ************************************************************************************
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated........---------------------------");

        formObject.setSheetVisible("Tab1", 2, false);
        formObject.setSheetVisible("Tab1", 3, false);
        formObject.setSheetVisible("Tab1", 4, false);
        formObject.setSheetVisible("Tab1", 5, false);
        formObject.setSheetVisible("Tab1", 6, false);
        formObject.setSheetVisible("Tab1", 7, false);
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");

        try {
            Query = "select StateCode from StateMaster order by StateCode asc";
            System.out.println("Query is " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("resut is " + result);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("state", result.get(i).get(0), result.get(i).get(0));
            }
            
            Query = "select HeadName from ServicePoHeadMaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result is"+result);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        String levelflag_ = formObject.getNGValue("levelflag");
        int levelflag = Integer.parseInt(levelflag_) + 1;
        try {
            if (formObject.getNGValue("filestatus").equalsIgnoreCase("Approved")) {
                System.out.println("inside approved");

                System.out.println("State is" + formObject.getNGValue("state"));
                System.out.println("level flag is " + levelflag);
                Query = "select ApproverCode from ServicePOApproverMaster where Head = '" + formObject.getNGValue("proctype").replace(",", "%") + "' "
                        + "and ApproverLevel='" + levelflag + "'"
                        + "and State ='" + formObject.getNGValue("state") + "' "
                        + "and approvercode is not null "
                        + "and approvercode <> ''";
                System.out.println("Query:" + Query);
                result = formObject.getDataFromDataSource(Query);
                System.out.println("result" + result);
                if (result.size() > 0) {
                    formObject.setNGValue("nextactivity", "Approver");
                    formObject.setNGValue("assignto", result.get(0).get(0));
                    formObject.setNGValue("levelflag", levelflag);
                } else {
                    System.out.println("inside else of approver");
                    //Query = "select ApproverLevel, ApproverCode from ServiceNonPOApproverMaster where head = '" + formObject.getNGValue("proctype") + "'  and state = '" + formObject.getNGValue("state") + "'  ";
                    Query = "select ApproverLevel, ApproverCode from ServicePOApproverMaster where  "
                            + "head = '" + formObject.getNGValue("proctype").replace(",", "%") + "' "
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
            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Query Raised")) {
                formObject.setNGValue("nextactivity", "Initiator");
                formObject.setNGValue("assignto", formObject.getNGValue("CreatedByName"));
            }
            formObject.setNGValue("previousactivity", activityName);
            objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text15"), "q_transactionhistory");
            System.out.println("history added");
        } catch (Exception e) {
            e.printStackTrace();
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