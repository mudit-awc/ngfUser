package com.newgen.OutwardFreight;

import com.newgen.common.AccountsGeneral;
import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Accounts implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    AccountsGeneral objAccountsGeneral = null;

    @Override
    public void continueExecution(String aurg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();

        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "CheckBox1":
                        System.out.println("inside value change of CheckBox1");
                        if (formObject.getNGValue("CheckBox1").equalsIgnoreCase("true")) {
                            formObject.setNGValue("qofwht_tdsgroup", "");
                            formObject.setNGValue("qofwht_tdspercent", "");
                            formObject.setNGValue("qofwht_adjustedoriginamount", "");
                            formObject.setNGValue("qofwht_tdsamount", "");
                            formObject.setNGValue("qofwht_adjustedtdsamount", "");
                        }
                        break;
                }
            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Pick_tdsgroup":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("qofwht_tdsgroup", "Code,Description", "TDS Group Master", 70, 70,
                                Query);
                        break;

                    case "Pick_hsnsac":
                        String hsnsaccodetype = formObject.getNGValue("qoftd_hsnsactype");
                        System.out.println("hsnsaccodetype inside pick_hsansac: " + formObject.getNGValue("qtd_hsnsactype"));
                        if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
                            Query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                            objPicklistListenerHandler.openPickList("qoftd_hsnsacdescription", "Code,Description", "HSN Master", 70, 70, Query);
                        } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
                            Query = "select SACCode,Description from SACMaster order by SACCode asc";
                            objPicklistListenerHandler.openPickList("qoftd_hsnsacdescription", "Code,Description", "SAC Master", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the type value"));
                        }
                        break;

                    case "Pick_journalname":
                        Query = "select Code,Description from JournalNamemaster order by Code asc";
                        objPicklistListenerHandler.openPickList("journalname", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "CheckBox1":
                        System.out.println("inside mouse click of CheckBox1");
                        if (formObject.getNGValue("CheckBox1").equalsIgnoreCase("true")) {
                            formObject.setNGValue("qofwht_tdsgroup", "");
                            formObject.setNGValue("qofwht_tdspercent", "");
                            formObject.setNGValue("qofwht_adjustedoriginamount", "");
                            formObject.setNGValue("qofwht_tdsamount", "");
                            formObject.setNGValue("qofwht_adjustedtdsamount", "");
                        }
                        break;

                    case "Btn_Modify_Taxdocument":
                        System.out.println("inside mouse click of Btn_Modify_Taxdocument");
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_tds_document");
                        break;

                    case "Button1":
                        System.out.println("inside mouse click of Button1");
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_withholding_tax");
                        break;
                }

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                }
        }
    }

    @Override
    public void formLoaded(FormEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        // TODO Auto-generated method stub
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
        System.out.println("inside form populate ofaccounts service po");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated-----------------------------------------");
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        System.out.println("filestatus value" + formObject.getNGValue("filestatus"));

        formObject.setSheetVisible("Tab1", 3, true);
        formObject.setEnabled("Pick_journalname", true);
        formObject.setVisible("Pick_journalname", true);
        formObject.setEnabled("Pick_hsnsac", true);
        formObject.setEnabled("Pick_tdsgroup", true);
        formObject.setEnabled("CheckBox1", true);
        formObject.setEnabled("Btn_Modify_Taxdocument", true);
        formObject.setEnabled("Button1", true);

        Query = "select calculatewithholdingtax,TDSGroup from VendorMaster where VendorCode= '" + formObject.getNGValue("accountcode") + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                formObject.setSheetVisible("Tab1", 4, false);
            } else {
                formObject.setSheetVisible("Tab1", 4, true);
            }
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
        objGeneral = new General();
        try {
            if (formObject.getNGValue("filestatus").equalsIgnoreCase("Query Raised")) {
                System.out.println("inside activity accounts");
                Query = "select TOP 1 ApproverCode from FreightBillApproverMaster where state = '" + formObject.getNGValue("state") + "' order by ApproverLevel DESC";
                System.out.println("Query1:" + Query);
                result = formObject.getDataFromDataSource(Query);
                System.out.println("result is" + result);
                if (result.size() > 0) {
                    formObject.setNGValue("assignto", result.get(0).get(0));
                } else {
                    formObject.setNGValue("assignto", "NA");
                }
            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Approved")) {
                String levelflag = formObject.getNGValue("levelflag");
                if (levelflag.equalsIgnoreCase("Maker")) {
                    Query = "select ApproverLevel, ApproverCode from FreightBillApproverMaster where state = '"
                            + formObject.getNGValue("state") + "' " + "and ApproverLevel ='Checker'";
                    System.out.println("Query " + Query);
                    result = formObject.getDataFromDataSource(Query);
                    if (result.size() > 0) {
                        formObject.setNGValue("assignto", result.get(0).get(1));
                        formObject.setNGValue("nextactivity", "Accounts");
                        formObject.setNGValue("levelflag", "Checker");
                    } else {
                        formObject.setNGValue("nextactivity", "SchedularAccount");
                    }
                } else {
                    formObject.setNGValue("nextactivity", "SchedularAccount");
                }
            }
            System.out.println("value in assign to" + formObject.getNGValue("assignto"));

//                formObject.setNGValue("nextactivity", formObject.getNGValue("previousactivity"));
            formObject.setNGValue("previousactivity", activityName);
            objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "",
                    formObject.getNGValue("exempt"), "q_transactionhistory");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }
}
