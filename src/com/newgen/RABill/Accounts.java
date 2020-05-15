/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.RABill;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Admin
 */
public class Accounts implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    List<List<String>> result;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    General objGeneral = null;
    AccountsGeneral objAccountsGeneral = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;

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
        System.out.println("inside formPopulated ");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        System.out.println("1");
        formObject.clear("filestatus");
        System.out.println("2");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        formObject.addComboItem("filestatus", "Exception", "Exception");
        System.out.println("3");
        String currentdate = objGeneral.getCurrentDate();
        System.out.println("currentdate : " + currentdate);
        if ("".equalsIgnoreCase(formObject.getNGValue("postingdate"))) {
            formObject.setNGValue("postingdate", currentdate);
        }
        if ("".equalsIgnoreCase(formObject.getNGValue("duedate"))) {
            formObject.setNGValue("duedate", currentdate);
        }
        try {
            Query = "select StateName from StateMaster order by StateCode asc";
            System.out.println("Query is " + Query);
            result = formObject.getDataFromDataSource(Query);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("state", result.get(i).get(0), result.get(i).get(0));
            }
            //for Location
            Query = "select WMSLocationId from WarehouseLocationMaster where WarehouseCode='" + formObject.getNGValue("warehouse") + "'";
            System.out.println("query" + Query);
            result = formObject.getDataFromDataSource(Query);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("location", result.get(i).get(0).toString(), result.get(i).get(0).toString());
            }

            //Retention:
            Query = "select netamount from cmplx_raabstractsheet where pinstanceid = '" + processInstanceId + "'";
            System.out.println("Query: " + Query);
            objAccountsGeneral.setRetention(
                    Query,
                    "paymenttermid",
                    "raretentioncredit",
                    "raretentionpercent",
                    "raretentionamount"
            );

            formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
            formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
            formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);
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
        //int levelflag = Integer.parseInt(levelflag_) + 1;
        //System.out.println("level flag : " + levelflag);
        String state = formObject.getNGValue("state");
        System.out.println("State : " + state);
        objGeneral.compareDate(formObject.getNGValue("invoicedate"), formObject.getNGValue("postingdate"));
//        try {
//            //if (formObject.getNGValue("filestatus").equalsIgnoreCase("Exception")) {
//            //  formObject.setNGValue("nextactivity", "PurchaseUser");
//            //} else 
//            if (formObject.getNGValue("filestatus").equalsIgnoreCase("Query Raised")) {
//                Query = "select ApproverCode from RABillApproverMaster where Head = 'RABill'"
//                        + "and State = '" + formObject.getNGValue("state") + "'"
//                        + "and ApproverLevel = '0'";
//                System.out.println("Query1:" + Query);
//                result = formObject.getDataFromDataSource(Query);
//                if (result.size() > 0) {
//                    formObject.setNGValue("assignto", result.get(0).get(0));
//                } else {
//                    formObject.setNGValue("assignto", "");
//                }
//                //formObject.setNGValue("nextactivity", "Indexer");
//                // formObject.setNGValue("assignto", formObject.getNGValue("CreatedByName"));
//                formObject.setNGValue("nextactivity", "Indexer");
//                formObject.setNGValue("levelflag", "0");
//            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Approved")) {
//                // String levelflag_ = formObject.getNGValue("levelflag");
//                if (formObject.getNGValue("levelflag").equalsIgnoreCase("Maker")) {
//                    System.out.println("inside if level flag is maker");
//                    Query = "select ApproverLevel, ApproverCode from RABillApproverMaster where  "
//                            + "head = 'RABill' "
//                            + "and state = '" + formObject.getNGValue("state") + "' "
//                            + "and approverlevel ='Checker'";
//                    System.out.println("Query " + Query);
//                    result = formObject.getDataFromDataSource(Query);
//                    if (result.size() > 0) {
//                        formObject.setNGValue("assignto", result.get(0).get(1));
//                        formObject.setNGValue("nextactivity", "Accounts");
//                        formObject.setNGValue("levelflag", "Checker");
//                    } else {
//                        formObject.setNGValue("nextactivity", "JournalPosting");
//                    }
//                } else {
//                    formObject.setNGValue("nextactivity", "JournalPosting");
//                }
//            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Exception")) {
//                objGeneral.setException(userName, "Combo1", "Text69");
//                formObject.setNGValue("nextactivity", "PurchaseUser");
//            }
//            Query = "select COUNT(*) from cmplx_raitemjournal where pinstanceid = '" + processInstanceId + "'";
//            result = formObject.getDataFromDataSource(Query);
//            System.out.println("Query: " + Query);
//            System.out.println("resul: " + result);
//            int journalsyncrequired = Integer.parseInt(result.get(0).get(0));
//            System.out.println("journalsyncrequired : " + journalsyncrequired);
//            if (journalsyncrequired == 0) {
//                System.out.println("journalsyncrequired is zero hence set False");
//                formObject.setNGValue("journalsyncrequired", "False");
//            } else if (journalsyncrequired > 0) {
//                System.out.println("journalsyncrequired is greater then zero hence set True");
//                formObject.setNGValue("journalsyncrequired", "True");
//            }
//
//        objAccountsGeneral.getsetRABILLSummary(processInstanceId);
//            formObject.setNGValue("previousactivity", activityName);
//            objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text15"), "q_transactionhistory");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String sQuery = "", nextactivity = "", strLevelFlag = "";
        String levelflag = formObject.getNGValue("levelflag");
        if (filestatus.equalsIgnoreCase("Approved")) {
            Query = "select count(*) from RABillApproverMaster "
                    + "where site = '" + formObject.getNGValue("site") + "' "
                    + "and state = '" + formObject.getNGValue("state") + "' "
                    + "and department = '" + formObject.getNGValue("department") + "' ";

            if (levelflag.equalsIgnoreCase("Maker")) {
                sQuery = Query + "and ApproverLevel = 'Checker' ";
                System.out.println("Query :" + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("No Account Checker defined in the DoA."));
                } else {
                    strLevelFlag = "Checker";
                    nextactivity = "Accounts";
                }
            } else if (levelflag.equalsIgnoreCase("Checker")) {
                strLevelFlag = "SchedulerAccount";
                nextactivity = "SchedulerAccount";
            }
        } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Exception")) {
            objGeneral.setException(userName, "Combo1", "Text69");
            formObject.setNGValue("nextactivity", "PurchaseUser");
        }
        objAccountsGeneral.getsetRABILLSummary(processInstanceId);
        Query = "select COUNT(*) from cmplx_raitemjournal where pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        System.out.println("Query: " + Query);
        System.out.println("resul: " + result);
        int journalsyncrequired = Integer.parseInt(result.get(0).get(0));
        System.out.println("journalsyncrequired : " + journalsyncrequired);
        if (journalsyncrequired == 0) {
            System.out.println("journalsyncrequired is zero hence set False");
            formObject.setNGValue("journalsyncrequired", "False");
        } else if (journalsyncrequired > 0) {
            System.out.println("journalsyncrequired is greater then zero hence set True");
            formObject.setNGValue("journalsyncrequired", "True");
        }
        formObject.setNGValue("FilterDoA_ApproverLevel", strLevelFlag);
        formObject.setNGValue("levelflag", strLevelFlag);
        formObject.setNGValue("nextactivity", nextactivity);
        formObject.setNGValue("previousactivity", activityName);
        
        
        objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text15"), "q_transactionhistory");

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
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objAccountsGeneral = new AccountsGeneral();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "currency": {
                        objCalculations.exronCurrencyChange(
                                "currency",
                                "vendoramount",
                                "newbaseamount",
                                "exchangerate"
                        );
                    }
                    break;

                    case "filestatus":
                        String filestatus = formObject.getNGValue("filestatus");
                        if (filestatus.equalsIgnoreCase("Exception")) {
                            formObject.setVisible("Label58", true);
                            formObject.setVisible("Combo1", true);
                            formObject.addComboItem("Combo1", "PO number not mentioned on invoice", "PO number not mentioned on invoice");
                            formObject.addComboItem("Combo1", "Incorrect PO number on invoice", "Incorrect PO number on invoice");
                            formObject.addComboItem("Combo1", "Invoice Number not mentioned on invoice", "Invoice Number not mentioned on invoice");
                            formObject.addComboItem("Combo1", "Incorrect invoice number on invoice", "Incorrect invoice number on invoice");
                            formObject.addComboItem("Combo1", "Incorrect details of Wonder Cement on invoice", "Incorrect details of Wonder Cement on invoice");
                            formObject.addComboItem("Combo1", "Mismatch of vendor name in invoice and PO", "Mismatch of vendor name in invoice and PO");
                            formObject.addComboItem("Combo1", "Quantity and Rate Variance between PO and Invoice", "Quantity and Rate Variance between PO and Invoice");
                            formObject.addComboItem("Combo1", "GST Rate is not matching", "GST Rate is not matching");
                            formObject.addComboItem("Combo1", "GST categorization is not matching", "GST categorization is not matching");
                            formObject.addComboItem("Combo1", "Mismatch of HSN Code between PO and Invoice", "Mismatch of HSN Code between PO and Invoice");
                            formObject.addComboItem("Combo1", "Budget Related Exception", "Budget Related Exception");
                            formObject.addComboItem("Combo1", "Invoice value is more than the PO value", "Invoice value is more than the PO value");
                        }
                        break;

                    case "vendoramount":
                    case "exchangerate":
                        objCalculations.exronBaseamountandExchangerateChange(
                                "currency",
                                "vendoramount",
                                "newbaseamount",
                                "exchangerate"
                        );
                        break;

                    case "qrawht_adjustedoriginamount":
                        BigDecimal bq_invoiceamount = new BigDecimal(formObject.getNGValue("invoiceamount"));
                        BigDecimal badjustedoriginamount = new BigDecimal(formObject.getNGValue("qrawht_adjustedoriginamount"));

                        if (badjustedoriginamount.compareTo(bq_invoiceamount) > 0) {
                            throw new ValidatorException(new FacesMessage("The Adjusted Origin Amount can not be greater than invoice Amount "));
                        } else {
                            String calculatedValue = objCalculations.calculatePercentAmount(
                                    badjustedoriginamount.toString(),
                                    formObject.getNGValue("qrawht_tdspercent")
                            );
                            formObject.setNGValue("qrawht_tdsamount", calculatedValue);
                            formObject.setNGValue("qrawht_adjustedtdsamount", calculatedValue);
                        }
                        break;

                    case "qratd_taxamountadjustment":
                        String rcmamount = objCalculations.calculatePercentAmount(
                                formObject.getNGValue("qratd_taxamountadjustment"),
                                formObject.getNGValue("qratd_reversechargepercent")
                        );
                        System.out.println("RCM amount: " + rcmamount);
                        formObject.setNGValue("qratd_reversechargeamount", rcmamount);
                        break;

                    case "qratd_gstratetype":
                        if (formObject.getNGValue("qratd_gstratetype").equalsIgnoreCase("RCM")) {
                            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                                    formObject.getNGValue("qratd_hsnsactype"),
                                    formObject.getNGValue("qratd_hsnsaccode"),
                                    formObject.getNGValue("qratd_taxcomponent"),
                                    "Vendor",
                                    formObject.getNGValue("contractor")
                            );
                            String reversechargeamount = objCalculations.calculatePercentAmount(
                                    formObject.getNGValue("qratd_taxamount"),
                                    reversechargerate
                            );
                            formObject.setNGValue("qratd_reversechargepercent", reversechargerate);
                            formObject.setNGValue("qratd_reversechargeamount", reversechargeamount);
                        } else {
                            formObject.setNGValue("qratd_reversechargepercent", "0.00");
                            formObject.setNGValue("qratd_reversechargeamount", "0.00");
                        }
                        break;

                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "qratd_exempt":
                        String exempt = formObject.getNGValue("qratd_exempt");
                        if (exempt.equalsIgnoreCase("true")) {
                            formObject.setNGValue("qratd_taxamount", "0");
                            formObject.setNGValue("qratd_taxamountadjustment", "0");
                        } else {
                            String Query = "select projectdebitamount from cmplx_linejournal where "
                                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                                    + "and projectcode = '" + formObject.getNGValue("qratd_projectcode") + "'";
                            System.out.println("Query :" + Query);
                            String taxamount = objCalculations.calculatePercentAmount(
                                    formObject.getDataFromDataSource(Query).get(0).get(0),
                                    formObject.getNGValue("qratd_taxrate")
                            );
                            formObject.setNGValue("qratd_taxamount", taxamount);
                            formObject.setNGValue("qratd_taxamountadjustment", taxamount);

                        }
                        break;
                        
                    case "Btn_Modify_Taxdocument":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        break;

                    case "Btn_Modify_LineJournal":
                        formObject.setNGValue("q_ljexchangerate", formObject.getNGValue("exchangerate"));
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linejournal");
                        break;

                    case "Btn_Update_Withholdingtax":
                        BigDecimal bq_invoiceamount = new BigDecimal(formObject.getNGValue("invoiceamount"));
                        BigDecimal badjustedoriginamount = new BigDecimal(formObject.getNGValue("qrawht_adjustedoriginamount"));
                        if (badjustedoriginamount.compareTo(bq_invoiceamount) > 0) {
                            throw new ValidatorException(new FacesMessage("The Adjusted Origin Amount can not be greater than Invoice Amount "));
                        } else {
                            ListView ListViewq_withholdingtax = (ListView) formObject.getComponent("q_withholdingtax");
                            int rowCount = ListViewq_withholdingtax.getRowCount();
                            if (rowCount == 0) {
                                formObject.ExecuteExternalCommand("NGAddRow", "q_withholdingtax");
                            } else {
                                formObject.ExecuteExternalCommand("NGModifyRow", "q_withholdingtax");
                            }
                        }
                        break;

                    case "Btn_RA_TDS":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("qrawht_tdsgroup", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Btn_Applytoall":
                        ListView ListViewq_taxdocument = (ListView) formObject.getComponent("q_taxdocument");
                        int rowcount = ListViewq_taxdocument.getRowCount();
                        for (int i = 0; i < rowcount; i++) {
                            applyTaxToAll(
                                    i,
                                    formObject.getNGValue("q_taxdocument", i, 1),
                                    formObject.getNGValue("q_taxdocument", i, 6));
                        }
                        break;

                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Pick_companylocation":
                        Query = "select StateName,AddressId,Address,AddressName  from AddressMaster "
                                + "where AddressType = 'Company'";
                        objPicklistListenerHandler.openPickList("companylocation", "State,Address Id,Address,Address Name", "Address Master", 70, 70, Query);
                        break;

                    case "Pick_vendorlocation":
                        Query = "select StateName, AddressId, GSTINNumber,Address,AddressName from AddressMaster "
                                + "where AddressType = 'Vendor' and PartyCode = '" + formObject.getNGValue("contractor") + "'";
                        objPicklistListenerHandler.openPickList("vendorlocation", "State,Address Id,GSTIN Number,Address,Address Name", "Address Master", 70, 70, Query);
                        break;

                    case "Pick_hsnsacvalue":
                        String hsnsactype = formObject.getNGValue("qratd_hsnsactype");
                        if (hsnsactype.equalsIgnoreCase("HSN")) {
                            Query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                            objPicklistListenerHandler.openPickList("qratd_hsnsacdescription", "HSNCode,Description", "HSN Value", 70, 70, Query);
                        } else if (hsnsactype.equalsIgnoreCase("SAC")) {
                            Query = "select SACCode,Description from SACMaster order by SACCode asc";
                            objPicklistListenerHandler.openPickList("qratd_hsnsacdescription", "SACCode,Description", "SAC Value", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the GST type value"));
                        }
                        break;

                    case "Pick_VendorLocation":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("vendorlocation", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Pick_TDSGroup":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("tdsgrp", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Pick_prjcategory_Vi_li":
                        Query = "select ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster order by ProjCategoryDesc asc";
                        objPicklistListenerHandler.openPickList("Vi_lj_ProjectCtegory", "Code,Category", "Project Category Master", 70, 70, Query);
                        break;

                    case "Pick_tefrid":
                        System.out.println("inside button click of tefrid");
                        Query = "Select TEFRId, TEFRLineItemId from TEFRMaster where businessunit = '" + formObject.getNGValue("site") + "'";
                        System.out.println("Query is " + Query);
                        objPicklistListenerHandler.openPickList("q_ljtefrid", "TEFRId,TEFRLineItemId", "TEFRId Master", 70, 70, Query);
                        break;

                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        System.out.println("inside tab1 RA click");
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 3: { // Vendor Invoice
                                ListView ListViewq_linejournal = (ListView) formObject.getComponent("q_linejournal");
                                int rowCount = ListViewq_linejournal.getRowCount();
                                if (rowCount == 0) {
                                    Query = "select structurecode,SUM(cast(netamount as  numeric(38,2))) from cmplx_raabstractsheet\n"
                                            + "where pinstanceid = '" + processInstanceId + "' group by structurecode";
                                    result = formObject.getDataFromDataSource(Query);

                                    String ItemJournalXML = "";
                                    for (int i = 0; i < result.size(); i++) {
                                        ItemJournalXML = (new StringBuilder()).append(ItemJournalXML).
                                                append("<ListItem><SubItem>").append("").
                                                append("</SubItem><SubItem>").append(result.get(i).get(0)).
                                                append("</SubItem><SubItem>").append(result.get(i).get(1)).
                                                append("</SubItem><SubItem>").append("").
                                                append("</SubItem><SubItem>").append("").
                                                append("</SubItem><SubItem>").append("").
                                                append("</SubItem><SubItem>").append("").
                                                append("</SubItem><SubItem>").append("").
                                                append("</SubItem></ListItem>").toString();
                                    }
                                    formObject.clear("q_linejournal");
                                    formObject.NGAddListItem("q_linejournal", ItemJournalXML);
                                }
                            }
                            break;

                            case 4: {
                                String vendorloc = formObject.getNGValue("vendorlocation");
                                System.out.println("Vendor Loc: " + vendorloc);
                                if (!"".equalsIgnoreCase(formObject.getNGValue("vendorlocation"))) {
                                    Query = "select AddressId, StateName, Address, GSTINNumber, AddressName "
                                            + "from AddressMaster where PartyCode ='" + formObject.getNGValue("contractor") + "'";
                                    System.out.println("Query for Vendor" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    System.out.println("result for vendor is " + result);
                                    formObject.setNGValue("vendorlocation", result.get(0).get(0));
                                    formObject.setNGValue("vendorstate", result.get(0).get(1));
                                    formObject.setNGValue("vendoraddress", result.get(0).get(2));
                                    formObject.setNGValue("vendorgstingdiuid", result.get(0).get(3));
                                    formObject.setNGValue("vendortaxinformation", result.get(0).get(4));
                                }
                            }
                            break;
//                            case 5: { //Retention /*Bug: formObject.SetNGValue is not working.Therfore, code moved to form populate.*/
                            //                                formObject = FormContext.getCurrentInstance().getFormReference();
                            //                                System.out.println("Inside retention click");
                            //                                Query = "select netamount from cmplx_raabstractsheet where pinstanceid = '" + processInstanceId + "'";
                            //                                System.out.println("Query: " + Query);
                            //                                objAccountsGeneral.setRetention(
                            //                                        Query,
                            //                                        "paymenttermid",
                            //                                        "raretentioncredit",
                            //                                        "raretentionpercent",
                            //                                        "raretentionamount"
                            //                                );
                            //                            }
                            //                            break;
                            //                            case 6: { //Withholding Tax
                            //                                Query = "select po.linenumber,po.itemnumber,po.tdsgroup,po.tdspercent,inv.assessableamount "
                            //                                        + "from cmplx_polinedetails po , cmplx_raabstractsheet inv where "
                            //                                        + "po.linenumber = inv.linenumber and po.itemnumber = inv.itemnumber "
                            //                                        + "and po.pinstanceid = inv.pinstanceid and po.pinstanceid = '" + processInstanceId + "'";
                            //                                objAccountsGeneral.setWithHoldingTax(Query, "q_withholdingtax");
                            //                            }
                            //                            break;
                            case 7: { //Tax Document
                                System.out.println("inside sheet 7 Tax Document");
                                ListView ListViewq_taxdocument = (ListView) formObject.getComponent("q_taxdocument");
                                int rowCount = ListViewq_taxdocument.getRowCount();
                                System.out.println("Row count :" + rowCount);
                                if (rowCount == 0) {
                                    String vendorstate = formObject.getNGValue("vendorstate");
                                    String customerstate = formObject.getNGValue("companystate");
                                    if (!vendorstate.equalsIgnoreCase("") && !customerstate.equalsIgnoreCase("")) {
                                        String taxcomponent = "", taxrate = "", taxamount = "", reversechargerate = "", reversechargeamount = "", TaxDocumentXML = "";
                                        Query = "select projectcode from cmplx_linejournal where pinstanceid = '" + processInstanceId + "'";
                                        System.out.println("Query :" + Query);
                                        result = formObject.getDataFromDataSource(Query);
                                        for (int j = 0; j < result.size(); j++) {
                                            if (vendorstate.equalsIgnoreCase(customerstate)) {
                                                for (int i = 0; i < 2; i++) {
                                                    System.out.println("before tax component");
                                                    if (i == 0) {
                                                        System.out.println("Inside 0");
                                                        taxcomponent = "SGST";
                                                    } else {
                                                        System.out.println("Inside else 0");
                                                        taxcomponent = "CGST";
                                                    }

                                                    TaxDocumentXML = (new StringBuilder()).append(TaxDocumentXML).
                                                            append("<ListItem><SubItem>").append(""). //line number
                                                            append("</SubItem><SubItem>").append(result.get(j).get(0)). //item number
                                                            //  append("</SubItem><SubItem>").append(formObject.getNGValue("vendorgstingdiuid")). //gstingdiuid
                                                            append("</SubItem><SubItem>").append(""). //gstingdiuid
                                                            append("</SubItem><SubItem>").append(""). //hsnsac type
                                                            append("</SubItem><SubItem>").append(""). //hsnsac code
                                                            append("</SubItem><SubItem>").append(""). //hsnsac description
                                                            append("</SubItem><SubItem>").append(taxcomponent). //tax component
                                                            append("</SubItem><SubItem>").append(taxrate). //rate
                                                            append("</SubItem><SubItem>").append(taxamount). //tax amount
                                                            append("</SubItem><SubItem>").append(taxamount). //adjustment tax amount
                                                            append("</SubItem><SubItem>").append(""). //non business usage %
                                                            append("</SubItem><SubItem>").append(reversechargerate). //reverse charge %
                                                            append("</SubItem><SubItem>").append(reversechargeamount). //reverse charge amount
                                                            append("</SubItem><SubItem>").append(""). //CST/VAT %
                                                            append("</SubItem><SubItem>").append(""). //CST/VAT Amount
                                                            append("</SubItem><SubItem>").append(""). //Non GST
                                                            append("</SubItem><SubItem>").append("false"). //exempt
                                                            append("</SubItem></ListItem>").toString();
                                                }

                                            } else {
                                                taxcomponent = "IGST";
                                                TaxDocumentXML = (new StringBuilder()).append(TaxDocumentXML).
                                                        append("<ListItem><SubItem>").append(""). //line number
                                                        append("</SubItem><SubItem>").append(result.get(j).get(0)). //item number
                                                        //  append("</SubItem><SubItem>").append(formObject.getNGValue("vendorgstingdiuid")). //gstingdiuid
                                                        append("</SubItem><SubItem>").append(""). //gstingdiuid
                                                        append("</SubItem><SubItem>").append(""). //hsnsac type
                                                        append("</SubItem><SubItem>").append(""). //hsnsac code
                                                        append("</SubItem><SubItem>").append(""). //hsnsac description
                                                        append("</SubItem><SubItem>").append(taxcomponent). //tax component
                                                        append("</SubItem><SubItem>").append(taxrate). //rate
                                                        append("</SubItem><SubItem>").append(taxamount). //tax amount
                                                        append("</SubItem><SubItem>").append(taxamount). //adjustment tax amount
                                                        append("</SubItem><SubItem>").append(""). //non business usage %
                                                        append("</SubItem><SubItem>").append(reversechargerate). //reverse charge %
                                                        append("</SubItem><SubItem>").append(reversechargeamount). //reverse charge amount
                                                        append("</SubItem><SubItem>").append(""). //CST/VAT %
                                                        append("</SubItem><SubItem>").append(""). //CST/VAT Amount
                                                        append("</SubItem><SubItem>").append(""). //Non GST
                                                        append("</SubItem><SubItem>").append("false"). //exempt
                                                        append("</SubItem></ListItem>").toString();
                                            }
                                        }
                                        System.out.println("Tax Document XML " + TaxDocumentXML);
                                        formObject.NGAddListItem("q_taxdocument", TaxDocumentXML);
                                    } else {
                                        formObject.setSelectedSheet("Tab1", 0);
                                        throw new ValidatorException(new FacesMessage("Kindly fill company and vendor tax information"));
                                    }
                                }

//                                Query = "select po.linenumber,po.itemnumber,gstngdiuid,hsn,sac,igstrate,igsttaxamount, "
//                                        + "cgstrate,cgsttaxamount,sgstrate,sgsttaxamount,nonbussinessusagepercent,exempt, "
//                                        + "inv.assessableamount,po.nongst,po.taxratetype,po.vatrate,po.vattaxamount "
//                                        + "from cmplx_polinedetails po, cmplx_raabstractsheet inv where "
//                                        + "po.pinstanceid = '" + processInstanceId + "' and po.pinstanceid = inv.pinstanceid "
//                                        + "and po.linenumber = inv.linenumber and po.itemnumber = inv.itemnumber";
//                                objAccountsGeneral.setTaxDocument(Query, "q_taxdocument", processInstanceId);
                            }
                            break;
                        }
                        break;
                    case "Tab2":
                        switch (formObject.getSelectedSheet("Tab2")) {
                            case 3: {
                                objAccountsGeneral.getsetRABILLSummary(processInstanceId);
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

    void applyTaxToAll(int RowIndex, String ProjectCode, String TaxComponent) {
        String nonbusinessusagepercent = "";
        String Query = "select projectdebitamount from cmplx_linejournal where "
                + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                + "and projectcode = '" + ProjectCode + "'";

        String taxamount = objCalculations.calculatePercentAmount(
                formObject.getDataFromDataSource(Query).get(0).get(0),
                formObject.getNGValue("qratd_taxrate")
        );

        String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                formObject.getNGValue("qratd_hsnsactype"),
                formObject.getNGValue("qratd_hsnsaccode"),
                TaxComponent,
                formObject.getNGValue("accounttype"),
                formObject.getNGValue("accountcode")
        );
        String reversechargeamount = objCalculations.calculatePercentAmount(
                taxamount,
                reversechargerate
        );

        if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("HSN")) {
            Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from HSNRateMaster where hsncode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";

        } else if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("SAC")) {
            Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from SACRateMaster where saccode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";
        }
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("CGST")
                    && ("0.00".equalsIgnoreCase(result.get(0).get(0))
                    || "0.0".equalsIgnoreCase(result.get(0).get(0))
                    || "0".equalsIgnoreCase(result.get(0).get(0))
                    || null == result.get(0).get(0)
                    || "NULL".equalsIgnoreCase(result.get(0).get(0))
                    || "Null".equalsIgnoreCase(result.get(0).get(0)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }

            if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("SGST")
                    && ("0.00".equalsIgnoreCase(result.get(0).get(1))
                    || "0.0".equalsIgnoreCase(result.get(0).get(1))
                    || "0".equalsIgnoreCase(result.get(0).get(1))
                    || null == result.get(0).get(1)
                    || "NULL".equalsIgnoreCase(result.get(0).get(1))
                    || "Null".equalsIgnoreCase(result.get(0).get(1)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }

            if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("IGST")
                    && (!"0.00".equalsIgnoreCase(result.get(0).get(2))
                    || !"0.0".equalsIgnoreCase(result.get(0).get(2))
                    || !"0".equalsIgnoreCase(result.get(0).get(2))
                    || null == result.get(0).get(2)
                    || "NULL".equalsIgnoreCase(result.get(0).get(2))
                    || "Null".equalsIgnoreCase(result.get(0).get(2)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }
        }
        formObject.setNGValue("q_taxdocument", RowIndex, 3, formObject.getNGValue("qratd_hsnsactype"));
        formObject.setNGValue("q_taxdocument", RowIndex, 4, formObject.getNGValue("qratd_hsnsaccode"));
        formObject.setNGValue("q_taxdocument", RowIndex, 5, formObject.getNGValue("qratd_hsnsacdescription"));
        formObject.setNGValue("q_taxdocument", RowIndex, 7, formObject.getNGValue("qratd_taxrate"));
        formObject.setNGValue("q_taxdocument", RowIndex, 8, taxamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 9, taxamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 10, nonbusinessusagepercent);
        formObject.setNGValue("q_taxdocument", RowIndex, 11, reversechargerate);
        formObject.setNGValue("q_taxdocument", RowIndex, 12, reversechargeamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 13, formObject.getNGValue("qratd_gstratetype"));
        formObject.setNGValue("q_taxdocument", RowIndex, 14, formObject.getNGValue("qratd_exempt"));

    }
}
