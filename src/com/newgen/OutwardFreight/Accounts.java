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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Accounts implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    boolean taxdoc = false, withtax = false;
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
                    case "qtd_gstratetype":
                        if (formObject.getNGValue("qtd_gstratetype").equalsIgnoreCase("RCM")) {
                            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                                    formObject.getNGValue("qoftd_hsnsactype"),
                                    formObject.getNGValue("qoftd_hsnsaccode"),
                                    formObject.getNGValue("qoftd_taxcomponent"),
                                    formObject.getNGValue("accounttype"),
                                    formObject.getNGValue("accountcode")
                            );
                            String reversechargeamount = objCalculations.calculatePercentAmount(
                                    formObject.getNGValue("qoftd_taxamount"),
                                    reversechargerate
                            );
                            formObject.setNGValue("qoftd_reversechargepercent", reversechargerate);
                            formObject.setNGValue("qoftd_reversechargeamount", reversechargeamount);
                        } else {
                            formObject.setNGValue("qoftd_reversechargepercent", "0.00");
                            formObject.setNGValue("qoftd_reversechargeamount", "0.00");
                        }
                        break;

                    case "qoftd_taxamount":
                        if (formObject.getNGValue("qoftd_taxamount").equalsIgnoreCase("0") || formObject.getNGValue("qoftd_taxamount").equalsIgnoreCase("0.0") || formObject.getNGValue("qoftd_taxamount").equalsIgnoreCase("0.00")) {
                            formObject.setEnabled("qoftd_taxamountadjustment", false);
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

                    case "qtd_exempt":
                        String exempt = formObject.getNGValue("qtd_exempt");
                        if (exempt.equalsIgnoreCase("true")) {
                            formObject.setNGValue("qoftd_taxamount", "0");
                            formObject.setNGValue("qoftd_taxamountadjustment", "0");
                            formObject.setLocked("qtd_taxamountadjustment", true);
                        } else {
                            String taxamount = objCalculations.calculatePercentAmount(
                                    formObject.getNGValue("Finalbillamount"),
                                    formObject.getNGValue("qoftd_taxrate")
                            );
                            formObject.setNGValue("qoftd_taxamount", taxamount);
                            formObject.setNGValue("qoftd_taxamountadjustment", taxamount);
                            formObject.setLocked("qtd_taxamountadjustment", false);
                        }
                        break;

                    case "Btn_Modify_Taxdocument":
                        System.out.println("inside mouse click of Btn_Modify_Taxdocument");
                        int rowIndex = formObject.getSelectedIndex("q_tds_document");
                        sgst_cgst(rowIndex, formObject.getNGValue("qoftd_taxcomponent"));
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_tds_document");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Button1":
                        System.out.println("inside mouse click of Button1");
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_withholding_tax");
                        break;

                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Pick_companylocation":
                        System.out.println("inside Pick_companylocation");
                        Query = "select StateName,AddressId,Address,AddressName  from AddressMaster "
                                + "where AddressType = 'Company'";
                        objPicklistListenerHandler.openPickList("customerlocation", "State,Address Id,Address,Address Name", "Address Master", 100, 450, Query);
                        break;
                }

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 3:  //taxdocument
                                taxdoc = true;
                                break;

                            case 4: //withholdingtax
                                withtax = true;
                                break;
                        }
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
        formObject.addComboItem("filestatus", "Reject", "Reject");
        System.out.println("filestatus value" + formObject.getNGValue("filestatus"));

        formObject.setSheetVisible("Tab1", 3, true);
        formObject.setEnabled("Pick_journalname", true);
        formObject.setVisible("Pick_journalname", true);
        formObject.setEnabled("Pick_hsnsac", true);
        formObject.setEnabled("Pick_tdsgroup", true);
        formObject.setEnabled("CheckBox1", true);
        formObject.setEnabled("Btn_Modify_Taxdocument", true);
        formObject.setEnabled("Button1", true);
        formObject.setEnabled("qtd_gstratetype", true);
        formObject.setEnabled("qtd_exempt", true);
        Query = "select calculatewithholdingtax,TDSGroup from VendorMaster where VendorCode= '" + formObject.getNGValue("accountcode") + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                formObject.setSheetVisible("Tab1", 4, false);
            } else {
                formObject.setSheetVisible("Tab1", 4, true);
            }
        }

        Query = "select sitecode from sitemaster order by sitecode asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
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
        if (taxdoc == false) {
            throw new ValidatorException(new FacesMessage("Kindly select Tax Document Tab"));
        }
        if (withtax == false) {
            throw new ValidatorException(new FacesMessage("Kindly select withholding Tax Tab"));
        }
        AccountsGeneral gen = new AccountsGeneral();
        String sQuery = "", nextactivity = "", strLevelFlag = "";
        String filestatus = formObject.getNGValue("filestatus");
        String levelflag = formObject.getNGValue("levelflag");
        if (activityName.equalsIgnoreCase("AccountsMaker")) {
            if (filestatus.equalsIgnoreCase("Approved")) {
                objGeneral.checkOutwardFreightDoAUser("AccountsChecker", "");
            } else if (filestatus.equalsIgnoreCase("Reject")) {
                Query = "select top 1 ApproverLevel from FreightBillApproverMaster "
                        + "where site = '" + formObject.getNGValue("site") + "' "
                        + "and state = '" + formObject.getNGValue("state") + "' "
                        + "and department = '" + formObject.getNGValue("department") + "'"
                        + "and ApproverLevel not in ('AccountsMaker','AccountsChecker') order by ApproverLevel desc";
                System.out.println("Query :" + Query);
                result = formObject.getDataFromDataSource(Query);
                formObject.setNGValue("FilterDoA_ApproverLevel", result.get(0).get(0));
                formObject.setNGValue("levelflag", result.get(0).get(0));
            }
            formObject.setNGValue("accountsmaker", userName);
        } else if (activityName.equalsIgnoreCase("AccountsChecker")) {
            if (filestatus.equalsIgnoreCase("Reject")) {
                formObject.setNGValue("FilterDoA_ApproverLevel", "AccountsMaker");
                formObject.setNGValue("levelflag", "AccountsMaker");
            }
            formObject.setNGValue("accountschecker", userName);
        } else if (activityName.equalsIgnoreCase("AXSyncException")) {
            Query = "select count(*) from cmplx_axintegration_error where "
                    + "resolve = 'False' and pinstanceid = '" + processInstanceId + "'";
            if (!formObject.getDataFromDataSource(Query).get(0).get(0).equals("0")) {
                throw new ValidatorException(new FacesMessage("Kindly resolve all the errors to proceed further"));
            }
        }
        formObject.setNGValue("previousactivity", activityName);
        gen.getsetOutwardFreightSummary(processInstanceId);
        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("exempt"), "q_transactionhistory");

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

    public void sgst_cgst(int rowIndex, String TaxComponent) {
        System.out.println("Inside sgst_cgst method");
        String TaxComponent1 = "", TaxDocumentXML1 = "", Line_no = "";
        if (TaxComponent.equalsIgnoreCase("SGST") || TaxComponent.equalsIgnoreCase("CGST")) {
            if (TaxComponent.equalsIgnoreCase("SGST")) {
                TaxComponent1 = "CGST";
                Line_no = String.valueOf(rowIndex - 1);
            } else {
                TaxComponent1 = "SGST";
                Line_no = String.valueOf(rowIndex + 1);
            }
            System.out.println("Updating Listview for " + TaxComponent1 + "");
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 0, formObject.getNGValue("qoftd_gstingdiuid")); //gstingdiuid
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 1, formObject.getNGValue("qoftd_hsnsactype")); //hsnsac type
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 2, formObject.getNGValue("qoftd_hsnsaccode")); //hsnsac code
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 3, formObject.getNGValue("qoftd_hsnsacdescription")); //hsnsac description
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 4, TaxComponent1); //tax component
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 5, formObject.getNGValue("qoftd_taxrate")); //rate
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 6, formObject.getNGValue("qoftd_taxamount")); //tax amount
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 7, formObject.getNGValue("qoftd_taxamountadjustment")); //adjustment tax amount
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 8, formObject.getNGValue("qoftd_nonbusinessusagepercent")); //non business usage %
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 9, formObject.getNGValue("qoftd_reversechargeamount")); //reverse charge amount
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 10, formObject.getNGValue("qoftd_reversechargepercent")); //reverse charge %
            formObject.setNGValue("q_tds_document", Integer.parseInt(Line_no), 11, formObject.getNGValue("qtd_gstratetype")); //gst rate type
            System.out.println("Values updated");
        }
    }
}
