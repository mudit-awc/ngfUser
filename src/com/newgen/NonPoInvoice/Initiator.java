/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.NonPoInvoice;

import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Richa Maheshwari
 */
public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null, processInstanceId = null,
            workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist = null;
    General objGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getSource().getName() :" + pEvent.getSource().getName());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objGeneral = new General();
        System.out.println("After creating all class");
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                System.out.println("Inside value change");
                String currency = "",
                 baseamount = "";
                switch (pEvent.getSource().getName()) {

                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;

                    case "invoiceamount":
                    case "exchangerateotherthaninr":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        break;

                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        break;

                    case "accounttype":
                        System.out.println("inside value change of account type");
                        String accounttype = formObject.getNGValue("accounttype");
                        if (accounttype.equalsIgnoreCase("Vendor")) {
                            System.out.println("inside if");
                            formObject.setNGValue("amounttype", "Credit");
                        } else {
                            System.out.println("inside else");
                            formObject.setNGValue("amounttype", "--Select--");
                        }
                        break;

                    case "account":
                        String acctype = formObject.getNGValue("accounttype");
                        if (acctype.equalsIgnoreCase("Vendor")) {
                            String accountc = formObject.getNGValue("accountcode");
                            String accountn = formObject.getNGValue("accountname");
                            formObject.setNGValue("VendorCode", accountc);
                            formObject.setNGValue("VendorName", accountn);
                        }
                        break;

                    case "accountcode":
//                        CallVebndorService vendorservice = new CallVebndorService();
//                        String AccessToken = new CallAccessTokenService().getAccessToken();
//                        vendorservice.GetSetPrePaymentLines(AccessToken, formObject.getNGValue("accountcode"));

                        break;

                    case "site":
                        String site = formObject.getNGValue("site");
                        if (site.equalsIgnoreCase("104")) {
                            System.out.println("inside site: 104");
                            formObject.setEnabled("state", true);
                            formObject.setNGValue("state", "--Select--");
                            formObject.setNGValue("department", "2031");
                            formObject.setNGValue("departmentdsc", "2031-Sales Accounts");
                        } else {
                            System.out.println("inside else");
                            formObject.setEnabled("q_ledgerstate", false);
                            Query = "select StateName from StateMaster where"
                                    + " AxRecId = (select StateAxRecId from SiteStateLinking where "
                                    + "businessunitaxrecid = (select AxRecId from SiteMaster where SiteCode = '" + site + "'))";
                            System.out.println("Quer is " + Query);
                            result = formObject.getDataFromDataSource(Query);
                            if (result.size() > 0) {
                                formObject.setNGValue("state", result.get(0).get(0));
                            }
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                System.out.println("Inside mouse click");
                switch (pEvent.getSource().getName()) {
                    case "Pick_journalname":
                        System.out.println("Inside pick journal");
                        Query = "select code,Description from JournalNamemaster order by code asc";
                        System.out.println("Query :" + Query);
                        objPicklistListenerHandler.openPickList("journalname", "Code,Description", "Journal Master", 150, 500, Query);
                        break;

                    case "Pick_department":
                        Query = "select value,description from department order by description asc";
                        objPicklistListenerHandler.openPickList("departmentdsc", "Code,Description", "Department Master", 150, 500, Query);
                        break;

                    case "Pick_account":
                        String accounttype = formObject.getNGValue("accounttype");
                        if (accounttype.equalsIgnoreCase("Vendor")) {
                            Query = "select VendorCode,VendorName from VendorMaster order by VendorCode asc";
                            objPicklistListenerHandler.openPickList("account", "Code,Name", "Vendor Master", 150, 500, Query);
                        } else if (accounttype.equalsIgnoreCase("Customer")) {
                            Query = "select Code,Description from CustomerMaster order by Code asc";
                            objPicklistListenerHandler.openPickList("account", "Code,Name", "Customer Master", 150, 500, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the account type value"));
                        }
                        break;

                    case "Pick_paymentterm":
                        Query = "select PaymentTermCode,PaymentTermDesc from PaymentTermMaster";
                        objPicklistListenerHandler.openPickList("paymentterm", "Code,Description", "Payment Term Master", 150, 500, Query);
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
        objGeneral = new General();
        formObject.setSheetVisible("Tab1", 1, false);
        formObject.setSheetVisible("Tab1", 2, false);
        formObject.setSheetVisible("Tab1", 3, false);

        formObject.clear("proctype");
        Query = "select HeadName from ServiceNonPoHeadMaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }

        formObject.clear("site");
        Query = "select sitecode from sitemaster order by sitecode asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("site", result.get(i).get(0), result.get(i).get(0));
        }

        formObject.clear("filestatus");
        if (!formObject.getNGValue("previousactivity").equalsIgnoreCase("Approver")
                && (!formObject.getNGValue("previousactivity").equalsIgnoreCase("AccountsMaker")
                || !formObject.getNGValue("previousactivity").equalsIgnoreCase("AccountsChecker"))) {
            formObject.addComboItem("filestatus", "Initiate", "Initiate");
        } else {
            formObject.addComboItem("filestatus", "Hold", "Hold");
            formObject.addComboItem("filestatus", "Query Cleared", "Query Cleared");
        }
        formObject.addComboItem("filestatus", "Discard", "Discard");
        objGeneral.setInvoiceExtractedData("", "invoicenumber", "invoiceamount", "invoicedate");
        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", null, new Date(objGeneral.getCurrDateForRange()));
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

        System.out.println("**********-------SUBMIT FORM Started Non PO Invoice------------*************");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objCalculations = new Calculations();
        objGeneral = new General();
        String levelflag = formObject.getNGValue("levelflag");

        objGeneral.checkDuplicateInvoice(
                formObject.getNGValue("accountcode"),
                formObject.getNGValue("invoicenumber"),
                formObject.getNGValue("fiscalyear"),
                processInstanceId
        );
        if (!formObject.getNGValue("filestatus").equalsIgnoreCase("Discard")) {
            objGeneral.checkServiceNonPoDoAUser(levelflag, "");
        }
        formObject.setNGValue("FilterDoA_Department", formObject.getNGValue("department"));
        formObject.setNGValue("FilterDoA_Head", formObject.getNGValue("proctype"));
        formObject.setNGValue("FilterDoA_Site", formObject.getNGValue("site"));
        formObject.setNGValue("FilterDoA_StateName", formObject.getNGValue("state"));

//        formObject.setNGValue("nextactivity", nextactivity);
        formObject.setNGValue("previousactivity", activityName);
        objGeneral.maintainHistory(
                userName,
                activityName,
                formObject.getNGValue("filestatus"),
                "",
                formObject.getNGValue("Text51"),
                "q_transactionhistory"
        );
        formObject.setNGValue("VendorCode", formObject.getNGValue("accountcode"));
        formObject.setNGValue("VendorName", formObject.getNGValue("accountname"));
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
