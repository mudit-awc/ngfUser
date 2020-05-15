package com.newgen.OutwardFreight;

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
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
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
        formObject.setSheetVisible("Tab1", 3, false);
        formObject.setSheetVisible("Tab1", 4, false);
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");

//        Query = "select calculatewithholdingtax,TDSGroup from VendorMaster where VendorCode	= '" + formObject.getNGValue("accountcode") + "'";
//        result = formObject.getDataFromDataSource(Query);
//        if (result.size() > 0) {
//            if (result.get(0).get(0).equalsIgnoreCase("0")) {
//                formObject.setSheetVisible("Tab1", 5, false);
//            } else {
//                formObject.setSheetVisible("Tab1", 5, true);
//            }
//        }
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
        // TODO Auto-generated method stub
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        AccountsGeneral gen = new AccountsGeneral();
        gen.getsetOutwardFreightSummary(processInstanceId);
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
        objGeneral = new General();
        AccountsGeneral gen = new AccountsGeneral();
        gen.getsetOutwardFreightSummary(processInstanceId);
        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag")) + 1;
        String sQuery = "", nextactivity = "", strLevelFlag = "";
        String filestatus = formObject.getNGValue("filestatus");
        if (filestatus.equalsIgnoreCase("Approved")) {
            Query = "select count(*) from FreightBillApproverMaster "
                    + "where site = '" + formObject.getNGValue("site") + "' "
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
        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("exempt"), "q_transactionhistory");
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
