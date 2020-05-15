package com.newgen.OutwardFreight;

import com.newgen.Webservice.CallGetFreightDetail;
import com.newgen.common.AccountsGeneral;
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
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist = null;
    General objGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    CallGetFreightDetail objGetSetFreightDetail = null;
    //   int levelflag = 0;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {

    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objGetSetFreightDetail = new CallGetFreightDetail();
        objCalculations = new Calculations();
        objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
                    case "baseamount":

                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;

                    case "account":
                        System.out.println("Account Code value : " + formObject.getNGValue("accountcode"));

                        Query = "select Address, GSTINNumber, StateName,AddressId,AddressName from AddressMaster where PartyCode = '"
                                + formObject.getNGValue("accountcode") + "'";
                        System.out.println("Query value: " + Query);
                        result = formObject.getDataFromDataSource(Query);
                        System.out.println("result value: " + result.get(0).get(0) + "," + result.get(0).get(1));
                        if (result.size() > 0) {

                            formObject.setNGValue("vendoraddress", result.get(0).get(0));
                            formObject.setNGValue("vendorgstingdiuid", result.get(0).get(1));
                            formObject.setNGValue("vendorstate", result.get(0).get(2));
                            formObject.setNGValue("vendorlocation", result.get(0).get(3));
                            formObject.setNGValue("vendortaxinformation", result.get(0).get(4));

                        }
                        break;
                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount",
                                "exchangerateotherthaninr");
                        break;
                    case "exchangerateotherthaninr":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "baseamount", "newbaseamount",
                                "exchangerateotherthaninr");
                        break;

                    case "order_type":
                        System.out.println("inside value change of order_type");
                        if (formObject.getNGValue("order_type").equalsIgnoreCase("SalesOrder")) {
                            formObject.setEnabled("q_AdjustedShortageValue", true);
                        }
                        break;

                    case "CheckBox1":
                        System.out.println("inside value change of CheckBox1");
                        if (formObject.getNGValue("CheckBox1").equalsIgnoreCase("true")) {
                            formObject.setNGValue("qwht_tdsgroup", "");
                            formObject.setNGValue("qwht_tdspercent", "");
                            formObject.setNGValue("qwht_adjustedoriginamount", "");
                            formObject.setNGValue("qwht_tdsamount", "");
                            formObject.setNGValue("qwht_adjustedtdsamount", "");
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Button2":                      // fetch button
                        System.out.println("Inside fetch button called for web service");
                        try {
                            System.out.println("Invoice Number: " + formObject.getNGValue("invoicenumber"));
                            System.out.println("Account Code: " + formObject.getNGValue("accountcode"));
                            objGetSetFreightDetail.GetSetFreightDetail("NewGen", formObject.getNGValue("accountcode"),
                                    formObject.getNGValue("invoicenumber"));

                            Query = "select calculatewithholdingtax,TDSGroup from VendorMaster where VendorCode	= '" + formObject.getNGValue("accountcode") + "'";
                            System.out.println("Query account pick tds group: " + Query);
                            result = formObject.getDataFromDataSource(Query);
                            System.out.println("result account pick tds group: " + result.get(0).get(0) + "," + result.get(0).get(1));
                            if (result.size() > 0) {
                                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                                    System.out.println("Inside sheet disable");
                                    formObject.setSheetVisible("Tab1", 5, false);
                                } else {
                                    formObject.setNGValue("qofwht_tdsgroup", result.get(0).get(1));
                                    System.out.println("Inside tdsdatainitialize account");
                                    tdsdatainitialize(result.get(0).get(1), formObject.getNGValue("TotalFreight"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case "Pick_department":
                        Query = "select description from department order by description asc";
                        objPicklistListenerHandler.openPickList("department", "Description", "Department Master", 35, 35, Query);
                        break;

                    case "Pick_account":
                        Query = "select VendorCode,VendorName from VendorMaster order by VendorCode asc";
                        objPicklistListenerHandler.openPickList("account", "Code,Name", "Vendor Master", 70, 70, Query);
                        break;

                    case "Pick_paymentterm":
                        Query = "select PaymentTermCode,PaymentTermDesc from PaymentTermMaster";
                        objPicklistListenerHandler.openPickList("paymentterm", "Code,Description", "Payment Term Master", 70,
                                70, Query);
                        break;

                    case "Pick_tdsgroup":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("qofwht_tdsgroup", "Code,Description", "TDS Group Master", 70, 70,
                                Query);
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

        formObject.setNGValue("accounttype", "Vendor");
        formObject.setNGValue("amounttype", "Credit");

        formObject.clear("filestatus");
        if (!formObject.getNGValue("previousactivity").equalsIgnoreCase("Approver")
                && !formObject.getNGValue("previousactivity").equalsIgnoreCase("Accounts")) {
            formObject.addComboItem("filestatus", "Initiate", "Initiate");
            formObject.addComboItem("filestatus", "Discard", "Discard");
        } else {

            formObject.addComboItem("filestatus", "Hold", "Hold");
            formObject.addComboItem("filestatus", "Query Cleared", "Query Cleared");
            formObject.addComboItem("filestatus", "Discard", "Discard");

        }
        formObject.setSheetVisible("Tab1", 3, false);
        formObject.setSheetVisible("Tab1", 4, false);

//        formObject.clear("site");
//        Query = "select sitecode from sitemaster order by sitecode asc";
//        System.out.println("Query is " + Query);
//        result = formObject.getDataFromDataSource(Query);
//        for (int i = 0; i < result.size(); i++) {
//            formObject.addComboItem("site", result.get(i).get(0), result.get(i).get(0));
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
        AccountsGeneral gen = new AccountsGeneral();
        gen.getsetOutwardFreightSummary(processInstanceId);
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
        objCalculations = new Calculations();
        objGeneral = new General();
        objGeneral.checkDuplicateInvoice(
                formObject.getNGValue("accountcode"),
                formObject.getNGValue("invoicenumber"),
                formObject.getNGValue("fiscalyear"),
                processInstanceId
        );

        System.out.println("**********-------SUBMIT FORM Started Outward Freight------------*************");
        AccountsGeneral gen = new AccountsGeneral();
        gen.getsetOutwardFreightSummary(processInstanceId);
        String sQuery = "", nextactivity = "", strLevelFlag = "";
        int levelflag = Integer.parseInt(formObject.getNGValue("levelflag"));
        Query = "select count(*) from FreightBillApproverMaster "
                + "where site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' ";
        sQuery = Query + "and ApproverLevel = '" + levelflag + "' ";
        System.out.println("Query: " + sQuery);
        result = formObject.getDataFromDataSource(sQuery);
        System.out.println("result is" + result);
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
        formObject.setNGValue("FilterDoA_ApproverLevel", strLevelFlag);
        formObject.setNGValue("FilterDoA_Department", formObject.getNGValue("department"));
        //     formObject.setNGValue("FilterDoA_Head", formObject.getNGValue("proctype"));
        formObject.setNGValue("FilterDoA_Site", formObject.getNGValue("site"));
        formObject.setNGValue("Filter_DoA_StateName", formObject.getNGValue("state"));
        formObject.setNGValue("levelflag", strLevelFlag);
        formObject.setNGValue("nextactivity", nextactivity);
        formObject.setNGValue("previousactivity", activityName);

        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("exempt"), "q_transactionhistory");
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void tdsdatainitialize(String tdsgroup, String adjustedamountorigin) {
        System.out.println("Inside tdsdatainitialize method");
        String calculatedValue = "";
//		String adjustedamountorigin = formObject.getNGValue("qwht_adjustedoriginamount");
//		adjustedamountorigin = formObject.getNGValue("TotalFreight");
        Query = "select TaxPercwithPAN from TDSMaster where code = '" + tdsgroup + "'";
        System.out.println("TDS Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("TDS result : " + result.get(0).get(0));
        if (result.size() > 0) {
//             formObject.setNGValue("qwht_tdspercent", result.get(0).get(0));
            calculatedValue = objCalculations.calculatePercentAmount(adjustedamountorigin, result.get(0).get(0));
//             formObject.setNGValue("qwht_tdsamount", calculatedValue);
//             formObject.setNGValue("qwht_adjustedtdsamount", calculatedValue);
        }
        formObject.clear("q_withholding_tax");
        String withholdingXML = "<ListItem>"
                + "<SubItem>" + tdsgroup + "</SubItem>"
                + "<SubItem>" + result.get(0).get(0) + "</SubItem>"
                + "<SubItem>" + adjustedamountorigin + "</SubItem>"
                + "<SubItem>" + calculatedValue + "</SubItem>"
                + "<SubItem>" + calculatedValue + "</SubItem>"
                + "</ListItem>";
        System.out.println("withholdingtaxXML: " + withholdingXML);
        formObject.NGAddListItem("q_withholding_tax", withholdingXML);
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
