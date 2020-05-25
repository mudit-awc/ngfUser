/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallAccessTokenService;
import com.newgen.Webservice.CallPrePaymentService;
import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.common.AccountsGeneral;
import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Admin
 */
public class AccountUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null, Query1 = null;
    List<List<String>> result = null;
    List<List<String>> result1 = null;
    Calculations objCalculations = null;
    General objGeneral = null;
    AccountsGeneral objAccountsGeneral = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    MultipleGrnGeneral objMultipleGrnGeneral = null;
    Float updated_partial_payment;
    private String activityId;

    @Override
    public void formLoaded(FormEvent fe) {
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
            activityId = formConfig.getConfigElement("ActivityId");
            processInstanceId = formConfig.getConfigElement("ProcessInstanceId");
            workItemId = formConfig.getConfigElement("WorkitemId");
            userName = formConfig.getConfigElement("UserName");
            processDefId = formConfig.getConfigElement("ProcessDefId");

            System.out.println("ProcessInstanceId===== " + processInstanceId);
            System.out.println("Activityname=====" + activityName);
            System.out.println("activityId=====" + activityId);
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
    public void formPopulated(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        String multiplegrn = formObject.getNGValue("multiplegrn");
        String previousactivity = formObject.getNGValue("previousactivity");
        String previousstatus = formObject.getNGValue("previousstatus");

        formObject.setNGValue("accountsremarks", "");
        formObject.setSelectedSheet("Tab2", 4);
        formObject.setNGValue("processid", processInstanceId); //Do not remove. This is for Multiple GRN Introduction WS
        String currentdate = objGeneral.getCurrentDate();
        if ("".equalsIgnoreCase(formObject.getNGValue("postingdate"))) {
            formObject.setNGValue("postingdate", currentdate);
        }
        if ("".equalsIgnoreCase(formObject.getNGValue("duedate"))) {
            formObject.setNGValue("duedate", currentdate);
        }

        if (activityName.equalsIgnoreCase("AccountsMaker")) {
            if (previousstatus.equalsIgnoreCase("Accept RGP")) {
                formObject.addComboItem("accountsstatus", "Challan Attached", "Challan Attached");
            } else {
                formObject.addComboItem("accountsstatus", "Submit For Invoicing", "Submit For Invoicing");
                if (previousstatus.equalsIgnoreCase("Accept NRGP")) {
                } else {
                    if (multiplegrn.equalsIgnoreCase("False")) {
                        formObject.addComboItem("accountsstatus", "GRN Cancellation Required", "GRN Cancellation Required");
                    }
                }
                formObject.addComboItem("accountsstatus", "Exception", "Exception");
//                formObject.addComboItem("accountsstatus", "Discard", "Discard");
            }
        } else if (activityName.equalsIgnoreCase("AccountsChecker")) {
            if (previousstatus.equalsIgnoreCase("Challan Attached")) {
                formObject.addComboItem("accountsstatus", "Challan Accepted", "Challan Accepted");
            } else {
                formObject.addComboItem("accountsstatus", "Create Invoicing", "Create Invoicing");
                formObject.addComboItem("accountsstatus", "Reject", "Reject");
                formObject.addComboItem("accountsstatus", "Exception", "Exception");
//                formObject.addComboItem("accountsstatus", "Discard", "Discard");
            }
        }

        formObject.clear("proctype");
        Query = "select HeadName from supplypoheadmaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }

        //retention
        Query = "select amount from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
        objAccountsGeneral.setRetention(
                Query,
                "paymenttermid",
                "retentioncredit",
                "retentionpercent",
                "retentionamount"
        );
        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);
    }

    @Override
    public void saveFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
    }

    @Override
    public void saveFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
    }

    @Override
    public void submitFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();

        objGeneral.compareDate(formObject.getNGValue("invoicedate"), formObject.getNGValue("postingdate"));
        if (activityName.equalsIgnoreCase("AccountsChecker")) {
            formObject.setNGValue("accountschecker", userName);
        }
        String accountsException = "";
        String accountStatus = formObject.getNGValue("accountsstatus");
        if (activityName.equalsIgnoreCase("AccountsMaker")) {
            if (accountStatus.equalsIgnoreCase("Submit For Invoicing")) {
                objGeneral.checkSupplyPoDoAUser("AccountsChecker");
            } else if (accountStatus.equalsIgnoreCase("GRN Cancellation Required")) {
                objGeneral.checkSupplyPoDoAUser("StoreMaker");
            }
        }

        if (accountStatus.equalsIgnoreCase("Challan Attached")) {
            Query = "select count(*) from PDBDocument where DocumentIndex in "
                    + "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                    + "(select itemindex from ext_supplypoinvoices where processid ='" + processInstanceId + "'))"
                    + "and Name = 'Challan'";
            System.out.println("Query : " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                objGeneral.checkSupplyPoDoAUser("AccountsChecker");
                throw new ValidatorException(new FacesMessage("Kindly attach Challan Document", ""));
            }
        }

        if (accountStatus.equalsIgnoreCase("Exception")) {
            accountsException = ": " + formObject.getNGValue("accountsexception");
        }
        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        objGeneral.maintainHistory(
                userName,
                activityName,
                accountStatus
                + accountsException,
                "",
                formObject.getNGValue("accountsremarks"),
                "q_transactionhistory"
        );
        formObject.setNGValue("previousactivity", activityName);
        formObject.setNGValue("previousstatus", accountStatus);
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
        objCalculations = new Calculations();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        objMultipleGrnGeneral = new MultipleGrnGeneral();
        System.out.println("inside event disoatch");
        switch (pEvent.getType().name()) {
            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {

                    case "Btn_Resolve":
                        System.out.println("inside btn resolve");
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Btn_Modify_Taxdocument":
                        objAccountsGeneral.setSameSgstCgst(
                                formObject.getSelectedIndex("q_taxdocument"),
                                formObject.getNGValue("qtd_taxcomponent")
                        );
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Modify_Prepayment":
                        int selectedrow = formObject.getSelectedIndex("q_prepayment");
                        float remaining_newgen = Float.parseFloat(formObject.getNGValue("q_prepayment", selectedrow, 2)) + Float.parseFloat(formObject.getNGValue("q_prepayment", selectedrow, 3));
                        System.out.println("Default remaining value: " + remaining_newgen);
                        float remaining_now = remaining_newgen - updated_partial_payment;
                        System.out.println("Updated value: " + remaining_now);
                        if (remaining_now < 0) {
                            throw new ValidatorException(new FacesMessage("Partial Payment cannot be greater then remaining amount"));
                        } else {
                            formObject.setNGValue("q_prepayment", selectedrow, 3, String.valueOf(remaining_now));
                            System.out.println("Updated value set in ListView");
                        }
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_prepayment");
                        break;

                    case "Btn_Clear_Vendor":
                        formObject.setNGValue("qoc_vendoraccount", "");
                        formObject.setNGValue("qoc_vendoraccountcode", "");
                        break;

                    case "Btn_Modify_Withholdingtax":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_withholdingtax");
                        break;

                    case "Pick_qocVendor":
                        Query = "select VendorCode, VendorName from VendorMaster order by VendorCode asc";
                        objPicklistListenerHandler.openPickList("qoc_vendoraccount", "Vendor Code, Vendor Name", "Vendor Master", 70, 70, Query);
                        System.out.println("inside Pick_tdsgroup ");
                        break;

                    case "Btn_Add_Maintaincharges":

                        String Line_number = formObject.getNGValue("qoc_linenumber");
                        String Item_number = formObject.getNGValue("qoc_itemnumber");
                        if (Line_number.equalsIgnoreCase("") || Item_number.equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly select Line number and Item number "));
                        }

                        boolean whtRefreshFlag = true;
                        boolean tdRefreshFlag = true;
                        String qoc_assessablevalue = formObject.getNGValue("qoc_assessablevalue");
                        if (qoc_assessablevalue.equalsIgnoreCase("true")) {
                            addAssessableAmount(
                                    "Add",
                                    formObject.getNGValue("qoc_linenumber"),
                                    formObject.getNGValue("qoc_itemnumber"),
                                    new BigDecimal(formObject.getNGValue("qoc_calculatedamount")));
                            formObject.clear("q_taxdocument");
                            formObject.clear("q_withholdingtax");

                        }
                        formObject.ExecuteExternalCommand("NGAddRow", "q_othercharges");
                        if (qoc_assessablevalue.equalsIgnoreCase("TRUE")) {
                            refreshWithHoldingTaxDocument();
                        }
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Modify_Maintaincharges":
                        int selectedRowIndex1 = formObject.getSelectedIndex("q_othercharges");
                        String qoc_assessablevalue_mod = formObject.getNGValue("qoc_assessablevalue");
//                        if (formObject.getNGValue("q_othercharges", selectedRowIndex1, 4).equalsIgnoreCase("TRUE")) {
                        if (qoc_assessablevalue_mod.equalsIgnoreCase("TRUE")) {
                            String old_assesssble = formObject.getNGValue("q_othercharges", selectedRowIndex1, 4);
                            if (old_assesssble.equalsIgnoreCase("FALSE")) {
                                formObject.setNGValue("q_othercharges", selectedRowIndex1, 5, "0");
                                formObject.setNGValue("q_othercharges", selectedRowIndex1, 6, "0");
                            }
                            float old_value = Float.parseFloat(formObject.getNGValue("q_othercharges", selectedRowIndex1, 6));
                            System.out.println("Old value : " + old_value);
                            float new_value = Float.parseFloat(formObject.getNGValue("qoc_calculatedamount"));
                            System.out.println("new value: " + new_value);
                            float diff_value = new_value - old_value;
                            System.out.println("Diff_value: " + diff_value);
                            if (diff_value > 0) {
                                System.out.println("inside add assesableamount");
                                addAssessableAmount(
                                        "ADD",
                                        formObject.getNGValue("qoc_linenumber"),
                                        formObject.getNGValue("qoc_itemnumber"),
                                        new BigDecimal(String.valueOf(diff_value))
                                );
                                System.out.println("line no: " + formObject.getNGValue("qoc_linenumber"));
                            } else if (diff_value < 0) {
                                System.out.println("inside subtract asseassable amount");
                                diff_value = Math.abs(diff_value);
                                System.out.println("absolute value: " + diff_value);
                                addAssessableAmount(
                                        "Subtract",
                                        formObject.getNGValue("qoc_linenumber"),
                                        formObject.getNGValue("qoc_itemnumber"),
                                        new BigDecimal(String.valueOf(diff_value))
                                );

                            }
                        } else {
                            String old_assessablevalue = formObject.getNGValue("q_othercharges", selectedRowIndex1, 4);
                            if (old_assessablevalue.equalsIgnoreCase("TRUE")) {
                                addAssessableAmount(
                                        "Subtract",
                                        formObject.getNGValue("q_othercharges", selectedRowIndex1, 0),
                                        formObject.getNGValue("q_othercharges", selectedRowIndex1, 1),
                                        new BigDecimal(formObject.getNGValue("q_othercharges", selectedRowIndex1, 6))
                                );
                            }
                        }
                        System.out.println("modify ho gya samjho ");
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_othercharges");
                        if (qoc_assessablevalue_mod.equalsIgnoreCase("TRUE")) {
                            refreshWithHoldingTaxDocument();
                        }
                        formObject.RaiseEvent("WFSave");
                        break;
                    case "Btn_Delete_Maintaincharges":
                        System.out.println("Inside mouse_click Btn_Delete_Maintaincharges");
                        String qoc_assessablevalue_del = formObject.getNGValue("qoc_assessablevalue");
                        int selectedRowIndex = formObject.getSelectedIndex("q_othercharges");
                        System.out.println("getSelectedIndex : " + selectedRowIndex);
                        if (formObject.getNGValue("q_othercharges", selectedRowIndex, 4).equalsIgnoreCase("TRUE")) {
                            addAssessableAmount(
                                    "Subtract",
                                    formObject.getNGValue("q_othercharges", selectedRowIndex, 0),
                                    formObject.getNGValue("q_othercharges", selectedRowIndex, 1),
                                    new BigDecimal(formObject.getNGValue("q_othercharges", selectedRowIndex, 6))
                            );
                        }
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_othercharges");
                        if (qoc_assessablevalue_del.equalsIgnoreCase("TRUE")) {
                            refreshWithHoldingTaxDocument();
                        }
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Allocate_Maintaincharges":
                        String qoc_assessablevalueflag = formObject.getNGValue("qoc_assessablevalue");
                        String qoc_category = formObject.getNGValue("qoc_category");
                        Query = "select amount,linenumber,itemid,assessableamount from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
                        System.out.println("Query :" + Query);
                        result = formObject.getDataFromDataSource(Query);
                        if (qoc_category.equalsIgnoreCase("Fixed")) {
                            String OtherChargesLineXML = "";
                            ArrayList<String> baseamount = new ArrayList<String>();
                            for (int i = 0; i < result.size(); i++) {
                                baseamount.add(result.get(i).get(0));
                            }
                            BigDecimal btotalnetamount = objCalculations.calculateSum(baseamount);
                            System.out.println("btotalnetamount : " + btotalnetamount);
                            BigDecimal bchargesvalue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));
                            System.out.println("bchargesvalue : " + bchargesvalue);

                            for (int i = 0; i < result.size(); i++) {
                                BigDecimal blinenetamount = new BigDecimal(result.get(i).get(3));
                                BigDecimal bchargesvalue_line = (blinenetamount.divide(btotalnetamount, RoundingMode.HALF_UP)).multiply(bchargesvalue).setScale(2, BigDecimal.ROUND_FLOOR);
                                OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                        append("<ListItem><SubItem>").append(result.get(i).get(1)).
                                        append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargescode")).
                                        append("</SubItem><SubItem>").append(qoc_category).
                                        append("</SubItem><SubItem>").append(qoc_assessablevalueflag).
                                        append("</SubItem><SubItem>").append(bchargesvalue_line).
                                        append("</SubItem><SubItem>").append(bchargesvalue_line).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesat")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                                        append("</SubItem></ListItem>").toString();

                                if (qoc_assessablevalueflag.equalsIgnoreCase("TRUE")) {
                                    addAssessableAmount(
                                            "Add",
                                            result.get(i).get(1),
                                            result.get(i).get(2),
                                            bchargesvalue_line
                                    );
                                }
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);

                        } else if (qoc_category.equalsIgnoreCase("Percent")) {
                            String OtherChargesLineXML = "";
                            for (int i = 0; i < result.size(); i++) {
                                String calculatedamount = objCalculations.calculatePercentAmount(result.get(i).get(3), formObject.getNGValue("qoc_chargesvalue"));
                                OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                        append("<ListItem><SubItem>").append(result.get(i).get(1)).
                                        append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargescode")).
                                        append("</SubItem><SubItem>").append(qoc_category).
                                        append("</SubItem><SubItem>").append(qoc_assessablevalueflag).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesvalue")).
                                        append("</SubItem><SubItem>").append(calculatedamount).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesat")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                                        append("</SubItem></ListItem>").toString();
                                if (qoc_assessablevalueflag.equalsIgnoreCase("TRUE")) {
                                    addAssessableAmount(
                                            "Add",
                                            result.get(i).get(1),
                                            result.get(i).get(2),
                                            new BigDecimal(calculatedamount)
                                    );
                                }
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
//                            Query = "select assessableamount from cmplx_invoiceline where "
//                                    + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
//                            System.out.println("Query :" + Query);
//                            result = formObject.getDataFromDataSource(Query);
//                            String calculatedamount = objCalculations.calculatePercentAmount(result.get(0).get(0), formObject.getNGValue("qoc_chargesvalue"));
//                            formObject.setNGValue("qoc_calculatedamount", calculatedamount);
                        } else if (qoc_category.equalsIgnoreCase("Pcs")) {
                            throw new ValidatorException(new FacesMessage("Not Applicable"));
                        }
                        if (qoc_assessablevalueflag.equalsIgnoreCase("TRUE")) {
                            refreshWithHoldingTaxDocument();
                        }
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_add_multplegrn":
                        objMultipleGrnGeneral.addMultipleGrnClick(processInstanceId);
                        break;

                    case "Btn_delete_multiplegrn":
                        objMultipleGrnGeneral.deleteMultipleGrnClick();
                        break;

                    case "Btn_combine":
                        objMultipleGrnGeneral.combineMultipleGrnClick();
                        break;

                    case "qtd_exempt":
                        String exempt = formObject.getNGValue("qtd_exempt");
                        if (exempt.equalsIgnoreCase("true")) {
                            formObject.setNGValue("qtd_taxamount", "0");
                            formObject.setNGValue("qtd_taxamountadjustment", "0");
                            formObject.setLocked("qtd_taxamountadjustment", true);
                        } else {
                            String Query = "select newassessableamount from cmplx_invoiceline where "
                                    + "pinstanceid = '" + processInstanceId + "' "
                                    + "and itemid ='" + formObject.getNGValue("qtd_itemnumber") + "' "
                                    + "and linenumber='" + formObject.getNGValue("qtd_linenumber") + "'";
                            System.out.println("Query :" + Query);
                            String taxamount = objCalculations.calculatePercentAmount(
                                    formObject.getDataFromDataSource(Query).get(0).get(0),
                                    formObject.getNGValue("qtd_taxrate")
                            );
                            formObject.setNGValue("qtd_taxamount", taxamount);
                            formObject.setNGValue("qtd_taxamountadjustment", taxamount);
                            formObject.setLocked("qtd_taxamountadjustment", false);
                        }
                        break;
                }
                break;
            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
                    case "accountsstatus":
                        String filestatus = formObject.getNGValue("accountsstatus");
                        if (filestatus.equalsIgnoreCase("Exception")) {
                            formObject.addComboItem("accountsexception", "PO number not mentioned on invoice", "PO number not mentioned on invoice");
                            formObject.addComboItem("accountsexception", "Incorrect PO number on invoice", "Incorrect PO number on invoice");
                            formObject.addComboItem("accountsexception", "Invoice Number not mentioned on invoice", "Invoice Number not mentioned on invoice");
                            formObject.addComboItem("accountsexception", "Incorrect invoice number on invoice", "Incorrect invoice number on invoice");
                            formObject.addComboItem("accountsexception", "Incorrect details of Wonder Cement on invoice", "Incorrect details of Wonder Cement on invoice");
                            formObject.addComboItem("accountsexception", "Mismatch of vendor name in invoice and PO", "Mismatch of vendor name in invoice and PO");
                            formObject.addComboItem("accountsexception", "Quantity and Rate Variance between PO and Invoice", "Quantity and Rate Variance between PO and Invoice");
                            formObject.addComboItem("accountsexception", "GST Rate is not matching", "GST Rate is not matching");
                            formObject.addComboItem("accountsexception", "GST categorization is not matching", "GST categorization is not matching");
                            formObject.addComboItem("accountsexception", "Mismatch of HSN Code between PO and Invoice", "Mismatch of HSN Code between PO and Invoice");
                            formObject.addComboItem("accountsexception", "Budget Related Exception", "Budget Related Exception");
                            formObject.addComboItem("accountsexception", "Invoice value is more than the PO value", "Invoice value is more than the PO value");
                        }
                        break;

                    case "invoicedate":
                        System.out.println("value set ho rhi h");
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        System.out.println("value set hogyi");
                        break;

                    case "qwht_tdspercent":
                        float checker = Float.parseFloat(formObject.getNGValue("qwht_tdspercent"));
                        System.out.println("checker " + checker);
                        if (checker == 0 || checker == 0.0 || checker == 0.00) {
                            System.out.println("chjsgdfhgsdhgjh");
                            formObject.setEnabled("qwht_adjustedoriginamount", false);
                            formObject.setVisible("Btn_Add_Withholdingtax", false);
                            formObject.setEnabled("qwht_adjustedtdsamount", false);
                        } else {
                            System.out.println("else m aaaayaa -----");
                            formObject.setEnabled("qwht_adjustedoriginamount", true);
                            formObject.setVisible("Btn_Add_Withholdingtax", true);
                            formObject.setEnabled("qwht_adjustedtdsamount", true);
                        }
                        break;

                    case "qwht_adjustedoriginamount":
                        String tdsadjustedbaseamount = formObject.getNGValue("qwht_adjustedoriginamount");
                        String tdspercent = formObject.getNGValue("qwht_tdspercent");
                        String calculatedTDSValue = objCalculations.calculatePercentAmount(tdsadjustedbaseamount, tdspercent);
                        formObject.setNGValue("qwht_tdsamount", calculatedTDSValue);
//                        formObject.setNGValue("qwht_adjustedtdsamount", calculatedTDSValue);
                        formObject.setNGValue("qwht_adjustedtdsamount", new BigDecimal(calculatedTDSValue).setScale(0, BigDecimal.ROUND_HALF_UP));
                        break;

                    case "qoc_linenumber":
                        setPOItemNumber("qoc_itemnumber", "qoc_linenumber", "");
                        String suppliercode = formObject.getNGValue("suppliercode");
                        formObject.setNGValue("qoc_vendoraccount", suppliercode + "_" + formObject.getNGValue("suppliername"));
                        formObject.setNGValue("qoc_vendoraccountcode", suppliercode);
                        formObject.setNGValue("qoc_ponumber", formObject.getNGValue("purchaseorderno"));
                        break;

                    case "qprepayment_partialpaymentamount":
                        updated_partial_payment = Float.parseFloat(formObject.getNGValue("qprepayment_partialpaymentamount"));

                        break;
                    case "qoc_category":
                    case "qoc_chargesvalue":
                        String qoc_category = formObject.getNGValue("qoc_category");

                        if (formObject.getNGValue("qoc_chargesat").equalsIgnoreCase("Line")) {
                            if (qoc_category.equalsIgnoreCase("Fixed")) {
                                formObject.setNGValue("qoc_calculatedamount", formObject.getNGValue("qoc_chargesvalue"));
                            } else if (qoc_category.equalsIgnoreCase("Pcs")) {
                                Query = "select quantity from cmplx_invoiceline where "
                                        + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
                                System.out.println("Query :" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                BigDecimal bChargeValue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));
                                BigDecimal bQuantity = new BigDecimal(result.get(0).get(0));
                                formObject.setNGValue("qoc_calculatedamount", bChargeValue.multiply(bQuantity).setScale(2, BigDecimal.ROUND_FLOOR).toString());
                            } else if (qoc_category.equalsIgnoreCase("Percent")) {
                                Query = "select assessableamount from cmplx_invoiceline where "
                                        + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
                                System.out.println("Query :" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                System.out.println("result :" + result);
                                String calculatedamount = objCalculations.calculatePercentAmount(result.get(0).get(0), formObject.getNGValue("qoc_chargesvalue"));
                                System.out.println("calculatedamount ::" + calculatedamount);
                                formObject.setNGValue("qoc_calculatedamount", calculatedamount);
                            }
                        }
//                        else if (formObject.getNGValue("qoc_chargesat").equalsIgnoreCase("Header")) {
//
//                        }

                        break;

                    case "qoc_chargesat":
                        String qoc_chargesat = formObject.getNGValue("qoc_chargesat");
                        formObject.setNGValue("qoc_linenumber", "");
                        formObject.setNGValue("qoc_itemnumber", "");
                        formObject.setNGValue("qoc_chargescode", "");
                        formObject.setNGValue("qoc_category", "");
                        formObject.setNGValue("qoc_chargesvalue", "");
                        formObject.setNGValue("qoc_calculatedamount", "");
                        if (qoc_chargesat.equalsIgnoreCase("Header")) {
                            formObject.setEnabled("qoc_linenumber", false);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", true);
                            formObject.setVisible("Btn_Add_Maintaincharges", false);
                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
//                            formObject.setVisible("Btn_Delete_Maintaincharges", false);
//                            formObject.setNGValue("qoc_assessablevalue", true);
//                            formObject.setVisible("qoc_assessablevalue", false);
                        } else if (qoc_chargesat.equalsIgnoreCase("Line")) {
                            formObject.setEnabled("qoc_linenumber", true);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", true);
                            formObject.setVisible("Btn_Modify_Maintaincharges", true);
//                            formObject.setVisible("Btn_Delete_Maintaincharges", true);
//                            formObject.setNGValue("qoc_assessablevalue", false);
//                            formObject.setVisible("qoc_assessablevalue", true);
                        } else {
                            formObject.setEnabled("qoc_linenumber", false);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", false);
                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
                            formObject.setVisible("Btn_Delete_Maintaincharges", false);
                            formObject.setVisible("qoc_assessablevalue", false);
                        }
                        break;

                    case "grnstartdate":
                    case "grnenddate":
                        objMultipleGrnGeneral.grnStartEndDateChange();
                        break;

                    case "invoiceamount":
                        formObject.setNGValue("summ_invoiceamount", formObject.getNGValue("invoiceamount"));
                        break;

                    case "newbaseamount":
                        formObject.setNGValue("summ_invoiceamountreporting", formObject.getNGValue("newbaseamount"));
                        break;
                }
                break;
            case "TAB_CLICKED":
                System.out.println("inside tab clicked of account by supply po");
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        System.out.println("inside tab1 clicked of account by supply po");
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 3: { //Other Charges
                                String OtherChargesLineXML = "";
                                setPOLineNumber("qoc_linenumber");
                                formObject.clear("qoc_chargescode");
                                result = formObject.getDataFromDataSource("select code from ChargesMaster");
                                for (int i = 0; i < result.size(); i++) {
                                    formObject.addComboItem("qoc_chargescode", result.get(i).get(0), result.get(i).get(0));
                                }

                                String multiplegrn = formObject.getNGValue("multiplegrn");
                                if (multiplegrn.equalsIgnoreCase("False")) {
                                    System.out.println("Inside multiplegrn False");
                                    Query = "select COALESCE(freight,'0'),itemid,linenumber, "
                                            //                                            + "Case When challanqty <= grnqty And challanqty <= wbnetweight Then challanqty "
                                            //                                            + "When grnqty <= challanqty And grnqty <= wbnetweight Then grnqty  "
                                            //                                            + "Else wbnetweight "
                                            //                                            + "End As TheMin "

                                            + "Case "
                                            + "When cast(challanqty as numeric(38,2))<= cast(grnqty  as numeric(38,2)) "
                                            + "And cast(challanqty  as numeric(38,2))<= cast(wbnetweight  as numeric(38,2)) "
                                            + "Then cast(challanqty  as numeric(38,2)) "
                                            + "When "
                                            + "cast(grnqty  as numeric(38,2))<= cast(challanqty  as numeric(38,2)) "
                                            + "And cast(grnqty  as numeric(38,2))<= cast(wbnetweight  as numeric(38,2)) "
                                            + "Then cast(grnqty  as numeric(38,2)) "
                                            + "Else cast(wbnetweight  as numeric(38,2)) End As TheMin "
                                            + "from cmplx_gateentryline where "
                                            + "pinstanceid ='" + processInstanceId + "'";

                                    result = formObject.getDataFromDataSource(Query);
                                    BigDecimal freightRate = new BigDecimal(result.get(0).get(0));
                                    System.out.println("freightRate :" + freightRate);
                                    if (freightRate.compareTo(new BigDecimal(BigInteger.ZERO)) == 1) {
                                        System.out.println("Inside fr8>0");
                                        String calculatedamount = freightRate.multiply(new BigDecimal(result.get(0).get(3))).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                                        OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                                append("<ListItem><SubItem>").append(result.get(0).get(2)). //line nnuumber
                                                append("</SubItem><SubItem>").append(result.get(0).get(1)). //item number
                                                append("</SubItem><SubItem>").append("FRT_TRANS"). //chrges code
                                                append("</SubItem><SubItem>").append("Fixed"). //categorydescription
                                                append("</SubItem><SubItem>").append("FALSE").
                                                append("</SubItem><SubItem>").append(calculatedamount).//charges value 
                                                append("</SubItem><SubItem>").append(calculatedamount). //calculated amount
                                                append("</SubItem><SubItem>").append(formObject.getNGValue("transportername")).
                                                append("</SubItem><SubItem>").append("Line").
                                                append("</SubItem><SubItem>").append(formObject.getNGValue("transportercode")).
                                                append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                                                append("</SubItem></ListItem>").toString();
                                    }
                                } else {
                                    System.out.println("Inside multiplegrn True");
                                    String transportercode = "";
//                                    Query = "select ext.transportercode,cmplx.linenumber,cmplx.itemid, "
//                                            + "sum(cast(cmplx.freight as numeric(38,2))), "
//                                            + "sum(cast(cmplx.challanqty as numeric(38,2))), "
//                                            + "sum(cast(cmplx.grnqty as numeric(38,2))), "
//                                            + "sum(cast(cmplx.wbnetweight as numeric(38,2))) "
//                                            + "from cmplx_gateentryline cmplx, ext_supplypoinvoices ext "
//                                            + "where ext.processid = cmplx.pinstanceid and cmplx.pinstanceid IN "
//                                            + "(select grnprocessid from cmplx_multiplegrninvoicing "
//                                            + "where pinstanceid = '" + processInstanceId + "') and freight <> 'NULL' "
//                                            + "and freight  is not null group by ext.transportercode,cmplx.linenumber,cmplx.itemid";

                                    Query = "Select transportercode,linenumber,itemid,sum(cast(min_val as numeric(38,2))) from ( "
                                            + "select ext.transportercode,cmplx.linenumber,cmplx.itemid, "
                                            + "--cmplx.grnqty,cmplx.challanqty,cmplx.wbnetweight,cmplx.freight, "
                                            + "--case when cmplx.grnqty <= cmplx.challanqty and cmplx.grnqty <= cmplx.wbnetweight then cast(cmplx.grnqty as numeric(38,2)) "
                                            + "--when cmplx.challanqty <= cmplx.grnqty and cmplx.challanqty <= cmplx.wbnetweight then cast(cmplx.challanqty as numeric(38,2)) "
                                            + "--else cast(cmplx.wbnetweight as numeric(38,2)) "
                                            + "--end min_qty, "
                                            + "case when cast(cmplx.grnqty as numeric(38,2))<= cast(cmplx.challanqty as numeric(38,2)) and cast(cmplx.grnqty as numeric(38,2))<= cast(cmplx.wbnetweight as numeric(38,2)) "
                                            + "then cast(cmplx.grnqty as numeric(38,2)) * cast(cmplx.freight as numeric(38,2)) "
                                            + "when cast(cmplx.challanqty as numeric(38,2))<= cast(cmplx.grnqty as numeric(38,2)) and cast(cmplx.challanqty as numeric(38,2))<= cast(cmplx.wbnetweight as numeric(38,2)) "
                                            + "then cast(cmplx.challanqty as numeric(38,2)) * cast(cmplx.freight as numeric(38,2)) "
                                            + "else cast(cmplx.wbnetweight as numeric(38,2)) * cast(cmplx.freight as numeric(38,2)) "
                                            + "end min_val "
                                            + "from ext_supplypoinvoices ext, cmplx_gateentryline cmplx "
                                            + "where ext.processid = cmplx.pinstanceid "
                                            + "and pinstanceid IN (select grnprocessid from cmplx_multiplegrninvoicing "
                                            + "where pinstanceid = '" + processInstanceId + "') "
                                            + "and freight <> 'NULL'and freight  is not null) as a "
                                            + "group by a.transportercode,a.linenumber,a.itemid";

                                    System.out.println("Query: " + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    for (int i = 0; i < result.size(); i++) {
                                        transportercode = result.get(i).get(0);
//                                        BigDecimal freightRate = new BigDecimal(result.get(i).get(3));
//                                        System.out.println("freightRate :" + freightRate);
//                                        if (freightRate.compareTo(new BigDecimal(BigInteger.ZERO)) == 1) {
//                                        System.out.println("Inside fr8>0");
//                                            float challanqty = Float.parseFloat(result.get(i).get(4));
//                                            float grnqty = Float.parseFloat(result.get(i).get(5));
//                                            float wbnetwieght = Float.parseFloat(result.get(i).get(6));
//                                            float minqty;
//                                            if (challanqty <= grnqty & challanqty <= wbnetwieght) {
//                                                minqty = challanqty;
//                                            } else if (grnqty <= challanqty & grnqty <= wbnetwieght) {
//                                                minqty = grnqty;
//                                            } else {
//                                                minqty = wbnetwieght;
//                                            }
//                                            System.out.println("Min QTY : " + minqty);
                                        Query1 = "Select distinct transportername from ext_supplypoinvoices "
                                                + "where transportercode = '" + transportercode + "' and transportername is not null";
                                        result1 = formObject.getDataFromDataSource(Query1);
                                        String transportername = result1.get(0).get(0);
//                                        String calculatedamount = freightRate.multiply(new BigDecimal(minqty)).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                                        String calculatedamount = result.get(i).get(3);
                                        System.out.println("calculatedamount : " + calculatedamount);
                                        OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                                append("<ListItem><SubItem>").append(result.get(0).get(1)). //line nnuumber
                                                append("</SubItem><SubItem>").append(result.get(0).get(2)). //item number
                                                append("</SubItem><SubItem>").append("FRT_TRANS"). //chrges code
                                                append("</SubItem><SubItem>").append("Fixed"). //categorydescription
                                                append("</SubItem><SubItem>").append("FALSE").
                                                append("</SubItem><SubItem>").append(calculatedamount).//charges value 
                                                append("</SubItem><SubItem>").append(calculatedamount). //calculated amount
                                                append("</SubItem><SubItem>").append(transportercode + "_" + transportername).
                                                append("</SubItem><SubItem>").append("Line").
                                                append("</SubItem><SubItem>").append(transportercode).
                                                append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                                                append("</SubItem></ListItem>").toString();
//                                        }
                                    }
                                }

                                int rowCount = formObject.getLVWRowCount("q_othercharges");
                                if (rowCount == 0) {
                                    Query = "select ch.linenumber,ch.itemnumber,ch.chargescode,ch.categorydescription,"
                                            + "ch.chargesvalue,ch.calculatedamount, ch.assessablevalue,"
                                            + "inv.assessableamount,inv.quantity "
                                            + "from cmplx_linechargesdetails ch, cmplx_invoiceline inv "
                                            + "where ch.pinstanceid = inv.pinstanceid "
                                            + "and ch.linenumber = inv.linenumber "
                                            + "and ch.purchaseorderno = inv.purchaseorderno "
                                            + "and ch.pinstanceid = '" + processInstanceId + "'";
                                    System.out.println("Query :" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    if (result.size() > 0) {
                                        for (int i = 0; i < result.size(); i++) {
                                            String AssessableValue = "FALSE";
                                            String calculatedamount = "";

                                            if (result.get(i).get(3).equalsIgnoreCase("Percent")) {
                                                calculatedamount = objCalculations.calculatePercentAmount(
                                                        result.get(i).get(7),
                                                        result.get(i).get(4)
                                                );
                                            } else if (result.get(i).get(3).equalsIgnoreCase("Pcs")) {
                                                BigDecimal bChargeValue = new BigDecimal(result.get(i).get(4));
                                                BigDecimal bQuantity = new BigDecimal(result.get(i).get(8));
                                                calculatedamount = bChargeValue.multiply(bQuantity).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                                            } else if (result.get(i).get(3).equalsIgnoreCase("Fixed")) {
                                                calculatedamount = result.get(i).get(4);
                                            }

                                            if (result.get(i).get(6).equals("1")) {
                                                AssessableValue = "TRUE";
                                                addAssessableAmount(
                                                        "Add",
                                                        result.get(i).get(0),
                                                        result.get(i).get(1),
                                                        new BigDecimal(calculatedamount)
                                                );
                                            }

                                            OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                                    append("<ListItem><SubItem>").append(result.get(i).get(0)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(1)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(3)).
                                                    append("</SubItem><SubItem>").append(AssessableValue).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(4)).
                                                    append("</SubItem><SubItem>").append(calculatedamount).
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("suppliercode") + "_" + formObject.getNGValue("suppliername")).
                                                    append("</SubItem><SubItem>").append("Line").
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("suppliercode")).
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                                                    append("</SubItem></ListItem>").toString();
                                        }
                                    }
                                    if (!OtherChargesLineXML.equalsIgnoreCase("")) {
                                        System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                                        formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                                    }
                                }
                                formObject.RaiseEvent("WFSave");
                            }
                            break;

//                            case 5: { //Retention
//                                Query = "select amount from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
//                                objAccountsGeneral.setRetention(
//                                        Query,
//                                        "paymentterm",
//                                        "retentioncredit",
//                                        "retentionpercent",
//                                        "retentionamount"
//                                );
//                            }
//                            break;
                            case 6: { //WithHolding Tax
                                System.out.println("Inside tab 6 click");
                                Query = "select po.linenumber,po.itemnumber,po.tdsgroup,po.tdspercent,"
                                        + "inv.newassessableamount,po.purchaseorderno,inv.assessableamount from cmplx_poline po, cmplx_invoiceline inv "
                                        + "where po.pinstanceid = inv.pinstanceid and po.linenumber = inv.linenumber "
                                        + "and po.purchaseorderno = inv.purchaseorderno "
                                        + "and po.pinstanceid = '" + processInstanceId + "' "
                                        + "and po.tdsgroup is not null";
                                objAccountsGeneral.setWithHoldingTax(Query, "q_withholdingtax", processInstanceId);
                            }
                            break;
                            case 7: { //Tax Document
                                System.out.println("Inside case 7 Tax Document ");
                                Query = "select po.linenumber,po.itemnumber,gstin_gdi_uid,COALESCE(hsn,''),COALESCE(sac,''),COALESCE(igstrate,'0'),"
                                        + "COALESCE(igsttaxamount,'0'),COALESCE(cgstrate,'0'),COALESCE(cgsttaxamount,'0'),"
                                        + "COALESCE(sgstrate,'0'),COALESCE(sgsttaxamount,'0'),COALESCE(nonbusinessusagepercent,'0'),"
                                        + "exempt,inv.newassessableamount,po.nongst,COALESCE(po.taxratetype,''),"
                                        + "COALESCE(po.vatrate,'0'),COALESCE(po.vattaxamount,'0'),po.purchaseorderno "
                                        + "from cmplx_poline po, cmplx_invoiceline inv where "
                                        + "po.pinstanceid = '" + processInstanceId + "' and po.pinstanceid = inv.pinstanceid "
                                        + "and po.linenumber = inv.linenumber and po.itemnumber = inv.itemid order by linenumber,itemnumber";
                                objAccountsGeneral.setTaxDocument(Query, "q_taxdocument", processInstanceId);

                            }
                            break;
                            case 8: {   // Prepayment 
                                String AccessToken = new CallAccessTokenService().getAccessToken();
                                new CallPrePaymentService().GetSetPrePaymentLines(AccessToken, formObject.getNGValue("purchaseorderno"), "Supply", processInstanceId);
                            }
                            break;
                        }
                        break;
                    case "Tab2":
                        switch (formObject.getSelectedSheet("Tab2")) {
                            case 7: {
                                objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
                            }
                            break;
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void continueExecution(String string, HashMap<String, String> hm
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String decrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setPOItemNumber(String itemnumberId, String linenumberId, String assessableamountId
    ) {
        Query = "select itemid,assessableamount from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "' "
                + "and linenumber = '" + formObject.getNGValue(linenumberId) + "'";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        formObject.setNGValue(itemnumberId, result.get(0).get(0));

        if (!assessableamountId.equalsIgnoreCase("")) {
            formObject.setNGValue(assessableamountId, result.get(0).get(1));
        }
    }

    void setPOLineNumber(String linenumberId) {
        formObject.clear(linenumberId);
        Query = "select linenumber from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            for (List<String> result1 : result) {
                formObject.addComboItem(linenumberId, result1.get(0), result1.get(0));
            }
        }
    }

    void addAssessableAmount(String operator, String linenumber, String itemnumber, BigDecimal assessableamount
    ) {

        String addedAssessable = "";
        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoiceline");
        int rowCount = ListViewq_invoicedetails.getRowCount();
        System.out.println("Row count : " + rowCount);
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                if (formObject.getNGValue("q_invoiceline", i, 0).equalsIgnoreCase(linenumber)
                        && formObject.getNGValue("q_invoiceline", i, 1).equalsIgnoreCase(itemnumber)) {
                    if (operator.equalsIgnoreCase("Add")) {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoiceline", i, 12)).add(assessableamount).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                    } else {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoiceline", i, 12)).subtract(assessableamount).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                    }
                    formObject.setNGValue("q_invoiceline", i, 12, addedAssessable);
                    break;
                }
            }

        }
    }

    void refreshWithHoldingTaxDocument() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        boolean whtRefreshFlag = true;
        boolean tdRefreshFlag = true;
        Query = "select count(*) from cmplx_taxdocument where pinstanceid='" + processInstanceId + "'";
        if (formObject.getDataFromDataSource(Query).get(0).get(0).equalsIgnoreCase("0")) { //rowcount of tax document > 0
            tdRefreshFlag = false;
        }

        Query = "select count(*) from cmplx_withholdingtax where pinstanceid='" + processInstanceId + "'";
        if (formObject.getDataFromDataSource(Query).get(0).get(0).equalsIgnoreCase("0")) { //rowcount of tax document > 0
            whtRefreshFlag = false;
        }

        if (tdRefreshFlag && whtRefreshFlag) {
            formObject.clear("q_taxdocument");
            formObject.clear("q_withholdingtax");
            formObject.RaiseEvent("WFSave");
            throw new ValidatorException(new FacesMessage("Withholding and Tax Document details has been refreshed"));
        }
        if (tdRefreshFlag) {
            formObject.clear("q_taxdocument");
            formObject.RaiseEvent("WFSave");
            throw new ValidatorException(new FacesMessage("Tax Document details has been refreshed"));
        }
        if (whtRefreshFlag) {
            formObject.clear("q_withholdingtax");
            formObject.RaiseEvent("WFSave");
            throw new ValidatorException(new FacesMessage("Withholding details has been refreshed"));
        }
    }

    void addMultipleGRN(String GRNnumber) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside btn add multiple grn");
        Query = "select processid , grnnumber,  format(grnsyncdate,'dd/MM/yyyy'), gateentryid, invoiceno,format(invoicedate,'dd/MM/yyyy'), "
                + "invoiceamount, lrno, format(lrdate,'dd/MM/yyyy'), loadingcity, transportercode, transportername, "
                + "vehicleno from ext_supplypoinvoices where grnnumber = '" + GRNnumber + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        String MultipleGrnXML = "";
        MultipleGrnXML = (new StringBuilder()).append(MultipleGrnXML).
                append("<ListItem><SubItem>").append(result.get(0).get(0)). //pid
                append("</SubItem><SubItem>").append(result.get(0).get(1)). //grn number
                append("</SubItem><SubItem>").append(result.get(0).get(2)). //grn date
                append("</SubItem><SubItem>").append(result.get(0).get(3)). //gate netry id
                append("</SubItem><SubItem>").append(result.get(0).get(4)). //invoice number
                append("</SubItem><SubItem>").append(result.get(0).get(5)). //invoice date
                append("</SubItem><SubItem>").append(result.get(0).get(6)). //invoice amount
                append("</SubItem><SubItem>").append(result.get(0).get(7)). //lr number
                append("</SubItem><SubItem>").append(result.get(0).get(8)). //lr date
                append("</SubItem><SubItem>").append(result.get(0).get(9)). //loading city
                append("</SubItem><SubItem>").append(result.get(0).get(10)). //transporter code
                append("</SubItem><SubItem>").append(result.get(0).get(11)). //transporter name
                append("</SubItem><SubItem>").append(result.get(0).get(12)). //vehicle number
                append("</SubItem></ListItem>").toString();

        System.out.println("XML :" + MultipleGrnXML);
        formObject.NGAddListItem("q_multiplegrninvoicing", MultipleGrnXML);
        objGeneral.linkWorkitem(engineName, sessionId, processInstanceId, result.get(0).get(0), "A");
    }

    void CombineMultipleGrn() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("inside CombineMultipleGrn");
        Query = "select linenumber, itemid, itemname, challanqty, grnqty, wbfirstwt,wbsecondwt, "
                + "wbnetweight, ponumber,freight,businessunit,state,costcentergroup,costcenter,department,gla from cmplx_gateentryline "
                + "where pinstanceid in (select grnprocessid from cmplx_multiplegrninvoicing where "
                + "pinstanceid = '" + processInstanceId + "')";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result : " + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside for " + i);
            ListView listview1 = (ListView) formObject.getComponent("q_gateentrylines");
            int rowcount = listview1.getRowCount();
            String GateLineContractXML = "";
            String q_linenumber = result.get(i).get(0);
            String q_itemnumber = result.get(i).get(1);

            if (rowcount == 0 && i == 0) {
                System.out.println("inside if of ==zero");
                GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                        append("<ListItem><SubItem>").append(result.get(0).get(0)).
                        append("</SubItem><SubItem>").append(result.get(0).get(1)).
                        append("</SubItem><SubItem>").append(result.get(0).get(2)).
                        append("</SubItem><SubItem>").append(result.get(0).get(3)).
                        append("</SubItem><SubItem>").append(result.get(0).get(4)).
                        append("</SubItem><SubItem>").append(result.get(0).get(5)).
                        append("</SubItem><SubItem>").append(result.get(0).get(6)).
                        append("</SubItem><SubItem>").append(result.get(0).get(7)).
                        append("</SubItem><SubItem>").append(result.get(0).get(8)).
                        append("</SubItem><SubItem>").append(result.get(0).get(9)).
                        append("</SubItem><SubItem>").append(result.get(0).get(10)).
                        append("</SubItem><SubItem>").append(result.get(0).get(11)).
                        append("</SubItem><SubItem>").append(result.get(0).get(12)).
                        append("</SubItem><SubItem>").append(result.get(0).get(13)).
                        append("</SubItem><SubItem>").append(result.get(0).get(14)).
                        append("</SubItem><SubItem>").append(result.get(0).get(15)).
                        append("</SubItem></ListItem>").toString();
                System.out.println("GateLineContractXML :" + GateLineContractXML);
                formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
            } else {
                System.out.println("inside else");
                boolean itemexistflag = false;
                for (int j = 0; j < rowcount; j++) {
                    String ge_linenumber = formObject.getNGValue("q_gateentrylines", j, 0);
                    String ge_itemnumber = formObject.getNGValue("q_gateentrylines", j, 1);

                    if (ge_itemnumber.equalsIgnoreCase(q_itemnumber)
                            && ge_linenumber.equals(q_linenumber)) {
                        System.out.println("Item matched breaking loop");
                        itemexistflag = true;

                        BigDecimal challanqty = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 3));
                        BigDecimal grnqty = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 4));
                        BigDecimal wbfirstwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 5));
                        BigDecimal wbsecondwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 6));
                        BigDecimal wbnetwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 7));

                        BigDecimal q_challanqty = new BigDecimal(result.get(i).get(3));
                        BigDecimal q_grnqty = new BigDecimal(result.get(i).get(4));
                        BigDecimal q_wbfirstwt = new BigDecimal(result.get(i).get(5));
                        BigDecimal q_wbsecondwt = new BigDecimal(result.get(i).get(6));
                        BigDecimal q_wbnetwt = new BigDecimal(result.get(i).get(7));

                        formObject.setNGValue("q_gateentrylines", j, 3, challanqty.add(q_challanqty).toString());
                        formObject.setNGValue("q_gateentrylines", j, 4, grnqty.add(q_grnqty).toString());
                        formObject.setNGValue("q_gateentrylines", j, 5, wbfirstwt.add(q_wbfirstwt).toString());
                        formObject.setNGValue("q_gateentrylines", j, 6, wbsecondwt.add(q_wbsecondwt).toString());
                        formObject.setNGValue("q_gateentrylines", j, 7, q_wbnetwt.add(q_wbnetwt).toString());

                        break;
                    }
                }

                if (itemexistflag == false) {
                    System.out.println("Item does not matched");
                    GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                            append("<ListItem><SubItem>").append(result.get(i).get(0)).
                            append("</SubItem><SubItem>").append(result.get(i).get(1)).
                            append("</SubItem><SubItem>").append(result.get(i).get(2)).
                            append("</SubItem><SubItem>").append(result.get(i).get(3)).
                            append("</SubItem><SubItem>").append(result.get(i).get(4)).
                            append("</SubItem><SubItem>").append(result.get(i).get(5)).
                            append("</SubItem><SubItem>").append(result.get(i).get(6)).
                            append("</SubItem><SubItem>").append(result.get(i).get(7)).
                            append("</SubItem><SubItem>").append(result.get(i).get(8)).
                            append("</SubItem><SubItem>").append(result.get(i).get(9)).
                            append("</SubItem><SubItem>").append(result.get(i).get(10)).
                            append("</SubItem><SubItem>").append(result.get(i).get(11)).
                            append("</SubItem><SubItem>").append(result.get(i).get(12)).
                            append("</SubItem><SubItem>").append(result.get(i).get(13)).
                            append("</SubItem><SubItem>").append(result.get(i).get(14)).
                            append("</SubItem><SubItem>").append(result.get(i).get(15)).
                            append("</SubItem></ListItem>").toString();
                    System.out.println("GateLineContractXML :" + GateLineContractXML);
                    formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
                }
            }
//                            String AccessToken = new CallAccessTokenService().getAccessToken();
            new CallPurchaseOrderService().GetSetPurchaseOrder("", "Supply", formObject.getNGValue("purchaseorderno"), "Supply");
        }
        formObject.RaiseEvent("WFSave");
    }
}
