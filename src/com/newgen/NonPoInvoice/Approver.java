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
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        // formObject.addComboItem("filestatus", "Discarded", "Discarded");

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

//        formObject.addComboItem("department", "All", "All");
//        Query = "select description from department order by description asc";
//        System.out.println("Query is " + Query);
//        result = formObject.getDataFromDataSource(Query);
//        System.out.println("result is "+result);
//        for (int i = 0; i < result.size(); i++) {
//            formObject.addComboItem("department", result.get(i).get(0), result.get(i).get(0));
//        }
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

//        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag")) + 1;
//        String filestatus = formObject.getNGValue("filestatus");
//        if (filestatus.equalsIgnoreCase("Approved")) {
//            Query = "select ApproverCode from ServiceNonPOApproverMaster where Head ='" + formObject.getNGValue("proctype") + "' "
//                    + "and ApproverLevel='" + levelflag + "' "
//                    + "and State ='" + formObject.getNGValue("state") + "'";
//            System.out.println("level flag value at approver" + levelflag);
//
//            System.out.println("---" + formObject.getNGValue("levelflag"));
//            System.out.println("Query:" + Query);
//            result = formObject.getDataFromDataSource(Query);
//            System.out.println("inside approver ");
//            System.out.println("result" + result);
//            if (result.size() > 0) {
//                formObject.setNGValue("nextactivity", "Approver");
//                formObject.setNGValue("assignto", result.get(0).get(0));
//                formObject.setNGValue("levelflag", levelflag);
//            } else {
//                System.out.println("inside else of approver");
//                //Query = "select ApproverLevel, ApproverCode from ServiceNonPOApproverMaster where head = '" + formObject.getNGValue("proctype") + "'  and state = '" + formObject.getNGValue("state") + "'  ";
//                Query = "select ApproverLevel, ApproverCode from ServiceNonPOApproverMaster where  "
//                        + "head = '" + formObject.getNGValue("proctype") + "' "
//                        + "and state = '" + formObject.getNGValue("state") + "' "
//                        + "and approverlevel in ('Maker', 'Checker')";
//                System.out.println("query  for level is " + Query);
//                result = formObject.getDataFromDataSource(Query);
//                if (result.size() > 0) {
//                    if (result.get(0).get(0).equalsIgnoreCase("Maker")) {
//                        formObject.setNGValue("levelflag", "Maker");
//                    } else if (result.get(levelflag).get(0).equalsIgnoreCase("Checker")) {
//                        formObject.setNGValue("levelflag", "Checker");
//                    }
//                    formObject.setNGValue("assignto", result.get(0).get(1));
//                    formObject.setNGValue("nextactivity", "Accounts");
//                } else {
//                    formObject.setNGValue("nextactivity", "SchedularAccount");
//                    //formObject.setNGValue("assignto", "NA");
//                }
//            }
//        } else if (filestatus.equalsIgnoreCase("Query Raised")) {
//            formObject.setNGValue("nextactivity", "Initiator");
//            formObject.setNGValue("assignto", formObject.getNGValue("CreatedByName"));
//        }
        String sQuery = "", nextactivity = "", strLevelFlag = "";
        String filestatus = formObject.getNGValue("filestatus");
        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag")) + 1;
        if (filestatus.equalsIgnoreCase("Approved")) {
            Query = "select count(*) from ServiceNonPOApproverMaster "
                    + "where head = '" + formObject.getNGValue("proctype") + "' "
                    + "and site = '" + formObject.getNGValue("site") + "' "
                    + "and state = '" + formObject.getNGValue("state") + "' "
                    + "and department = '" + formObject.getNGValue("department") + "' ";
            sQuery = Query + "and ApproverLevel = '" + levelflag + "' ";
            System.out.println("Query: " + sQuery);
            result = formObject.getDataFromDataSource(sQuery);
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                sQuery = "";
                sQuery = Query + "and ApproverLevel = 'Maker'";
                System.out.println("Query: " + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    sQuery = "";
                    sQuery = Query + "and ApproverLevel = 'Checker'";
                    System.out.println("Query: " + sQuery);
                    result = formObject.getDataFromDataSource(sQuery);
                    if (result.get(0).get(0).equalsIgnoreCase("0")) {
                        throw new ValidatorException(new FacesMessage("No Approver and Account Maker/Checker defined in the DoA."));
                    } else {
                        strLevelFlag = "Checker";
                        nextactivity = "Accounts";
                    }
                } else {
                    strLevelFlag = "Maker";
                    nextactivity = "Accounts";
                }
            } else {
                strLevelFlag = String.valueOf(levelflag);
                nextactivity = "Approver";
            }
        } else if (filestatus.equalsIgnoreCase("Query Raised")) {
            nextactivity = "Initiator";
        }
        formObject.setNGValue("FilterDoA_ApproverLevel", strLevelFlag);
        formObject.setNGValue("levelflag", strLevelFlag);
        formObject.setNGValue("nextactivity", nextactivity);
        formObject.setNGValue("previousactivity", activityName);
        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text51"), "q_transactionhistory");
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
