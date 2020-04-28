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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.json.JSONArray;

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
    public void formPopulated(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        formObject.setNGValue("processid", processInstanceId);
        if ("MultipleGRNInvoicing".equalsIgnoreCase(formObject.getNGValue("IntroducedAt"))
                || activityName.equalsIgnoreCase("MultipleGRNInvoicing")) {
            formObject.setNGValue("multiplegrn", "True");
            Query = "select distinct ext.purchaseorderno from ext_supplypoinvoices ext, WFINSTRUMENTTABLE wf "
                    + "where wf.ProcessInstanceID = ext.processid "
                    + "and wf.ActivityName='AccountsUser' "
                    + "and ext.multiplegrn='False' "
                    + "and ext.purchaseorderno is not null";
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("purchaseorderno", result.get(i).get(0), result.get(i).get(0));
            }
            formObject.setVisible("Frame11", true);
            formObject.setVisible("Frame9", false);
        }

        formObject.setNGValue("challanflag", "false");
        formObject.setSelectedSheet("Tab2", 4);
        String currentdate = objGeneral.getCurrentDate();
        System.out.println("currentdate : " + currentdate);
        if ("".equalsIgnoreCase(formObject.getNGValue("postingdate"))) {
            formObject.setNGValue("postingdate", currentdate);
        }
        if ("".equalsIgnoreCase(formObject.getNGValue("duedate"))) {
            formObject.setNGValue("duedate", currentdate);
        }

        formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);
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
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        System.out.println("inside submit form started");
        // Code for Mandatory document started
        String prevActivity = formObject.getNGValue("previousactivity");
        String accountStatus = formObject.getNGValue("accountsstatus");
        String accountremarks = formObject.getNGValue("accountremarks");
        String purchasestatuschecker = formObject.getNGValue("purchasestatuschecker");

        int challanCounter = 0;

        String processid = "";
        processid = formObject.getNGValue("processid");
        System.out.println("first query : " + Query);
        System.out.println("first result : " + result);
        System.out.println("process id : " + processid);

        Query = "select Name from PDBDocument where DocumentIndex in \n"
                + "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                + "(select itemindex from ext_supplypoinvoices where processid ='" + processid + "'))";
        result = formObject.getDataFromDataSource(Query);
        System.out.println("second query : " + Query);
        System.out.println("second result : " + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside query loop");
            if (result.get(i).get(0).equalsIgnoreCase("Challan")) {
                System.out.println("Challan counter m aagya");
                challanCounter++;
            }
        }
        System.out.println("accountStatus ** " + accountStatus);
        if (accountStatus.equalsIgnoreCase("GRN Cancel Required")) {
            formObject.setNGValue("previousstatus", "GRN Cancel Required");
        }
        System.out.println("Value of challanCounter : " + challanCounter);
        System.out.println("activityName :" + activityName);
        System.out.println("prevActivity :" + prevActivity);
        System.out.println("purchasestatuschecker ::::::::::" + purchasestatuschecker);

        if (activityName.equalsIgnoreCase("AccountsUser") && prevActivity.equalsIgnoreCase("StoreUser")) {
            if (purchasestatuschecker.equalsIgnoreCase("Replacement/Exchange") || purchasestatuschecker.equalsIgnoreCase("Purchase Return")) {
                System.out.println("inside Challan Accepted");
                if (challanCounter <= 0) {
                    System.out.println("Challan exception m aagyaEQUAL WALE");
                    throw new ValidatorException(new FacesMessage("Kindly attach Challan Document", ""));
                }
                formObject.setNGValue("challanflag", "true");
            }
        }

        String username = formObject.getUserName();
        ListView ListViewq_history = (ListView) formObject.getComponent("q_transactionhistory");
        int RowCountq_history = ListViewq_history.getRowCount();
        System.out.println("RowCountq_history : " + RowCountq_history);
        String initiatorRemarks = formObject.getNGValue("initiatorremarks");
        objGeneral.maintainHistory(username, activityName, accountStatus, "", accountremarks, "q_transactionhistory");

        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        // Code for Mandatory document Ended here    
        formObject.setNGValue("previousactivity", activityName);
        System.out.println("Previous Activity :" + formObject.getNGValue("previousactivity"));
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
        System.out.println("inside event disoatch");
        switch (pEvent.getType().name()) {
            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Modify_Taxdocument":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        break;

                    case "Btn_Modify_Prepayment":
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
                        if (formObject.getNGValue("qoc_assessablevalue").equalsIgnoreCase("true")) {
                            addAssessableAmount("Add", formObject.getNGValue("qoc_linenumber"), formObject.getNGValue("qoc_itemnumber"), new BigDecimal(formObject.getNGValue("qoc_chargesvalue")));
                        }
                        formObject.ExecuteExternalCommand("NGAddRow", "q_othercharges");
                        break;

                    case "Btn_Modify_Maintaincharges":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_othercharges");
                        break;

                    case "Btn_Delete_Maintaincharges":
                        if (formObject.getNGValue("qoc_assessablevalue").equalsIgnoreCase("true")) {
                            addAssessableAmount("Subtract", formObject.getNGValue("qoc_linenumber"), formObject.getNGValue("qoc_itemnumber"), new BigDecimal(formObject.getNGValue("qoc_chargesvalue")));
                        }
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_othercharges");
                        break;

                    case "Btn_Allocate_Maintaincharges":
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
                                BigDecimal bchargesvalue_line = (blinenetamount.divide(btotalnetamount)).multiply(bchargesvalue).setScale(2, BigDecimal.ROUND_FLOOR);
                                OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                        append("<ListItem><SubItem>").append(result.get(i).get(1)).
                                        append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargescode")).
                                        append("</SubItem><SubItem>").append(qoc_category).
                                        append("</SubItem><SubItem>").append("true").
                                        append("</SubItem><SubItem>").append(bchargesvalue_line).
                                        append("</SubItem><SubItem>").append(bchargesvalue_line).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesat")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                        append("</SubItem></ListItem>").toString();

                                addAssessableAmount("Add", result.get(i).get(1), result.get(i).get(2), bchargesvalue_line);
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);

                        } else if (qoc_category.equalsIgnoreCase("Percent")) {
                            String OtherChargesLineXML = "";
                            for (int i = 0; i < result.size(); i++) {
                                OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                        append("<ListItem><SubItem>").append(result.get(i).get(1)).
                                        append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargescode")).
                                        append("</SubItem><SubItem>").append(qoc_category).
                                        append("</SubItem><SubItem>").append("true").
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesvalue")).
                                        append("</SubItem><SubItem>").append(objCalculations.calculatePercentAmount(result.get(i).get(3), formObject.getNGValue("qoc_chargesvalue"))).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesat")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                        append("</SubItem></ListItem>").toString();

                                addAssessableAmount("Add", result.get(i).get(1), result.get(i).get(2), new BigDecimal(formObject.getNGValue("qoc_chargesvalue")));
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                            Query = "select assessableamount from cmplx_invoiceline where "
                                    + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
                            System.out.println("Query :" + Query);
                            result = formObject.getDataFromDataSource(Query);
                            String calculatedamount = objCalculations.calculatePercentAmount(result.get(0).get(0), formObject.getNGValue("qoc_chargesvalue"));
                            formObject.setNGValue("qoc_calculatedamount", calculatedamount);
                        } else if (qoc_category.equalsIgnoreCase("Pcs")) {
                            throw new ValidatorException(new FacesMessage("Not Applicable"));
                        }
                        break;

                    case "Btn_add_multplegrn":
                        String grnnumber = formObject.getNGValue("q_grn");
                        Query = "select count(*) from cmplx_multiplegrninvoicing where "
                                + "pinstanceid = '" + processInstanceId + "' and grnnumber = '" + grnnumber + "'";
                        System.out.println("Query : " + Query);

                        if (formObject.getDataFromDataSource(Query).get(0).get(0).equals("0")) {
                            addMultipleGRN(grnnumber);
                            formObject.RaiseEvent("WFSave");
                        } else {
                            throw new ValidatorException(new FacesMessage("GRN number already added.", ""));
                        }
                        break;

                    case "Btn_delete_multiplegrn":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_multiplegrninvoicing");
                        formObject.RaiseEvent("WFSave");
                        formObject.clear("q_polines");
                        formObject.clear("q_gateentrylines");
                        formObject.clear("q_invoiceline");
                        formObject.clear("q_othercharges");
                        formObject.clear("q_withholdingtax");
                        formObject.clear("q_taxdocument");
                        formObject.clear("q_prepayment");
                        formObject.setNGValue("retentioncredit", "");
                        formObject.setNGValue("retentionpercent", "");
                        formObject.setNGValue("retentioncredit", "");
                        formObject.setNGValue("retentionamount", "");
                        formObject.setNGValue("companytaxinformation", "");
                        formObject.setNGValue("companyaddress", "");
                        formObject.setNGValue("vendortaxinformation", "");
                        formObject.setNGValue("vendoraddress", "");
                        break;

                    case "Btn_combine":
                        System.out.println("button click Btn_combine");
                        CombineMultipleGrn();
                        break;
                }
                break;
            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
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

                    case "qoc_linenumber":
                        setPOItemNumber("qoc_itemnumber", "qoc_linenumber", "");
                        String suppliercode = formObject.getNGValue("suppliercode");
                        formObject.setNGValue("qoc_vendoraccount", suppliercode + "-" + formObject.getNGValue("suppliername"));
                        formObject.setNGValue("qoc_vendoraccountcode", suppliercode);
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
                            //formObject.setVisible("Btn_Modify_Maintaincharges", false);
                            formObject.setVisible("Btn_Delete_Maintaincharges", false);
                            formObject.setNGValue("qoc_assessablevalue", true);
                            formObject.setVisible("qoc_assessablevalue", false);
                        } else if (qoc_chargesat.equalsIgnoreCase("Line")) {
                            formObject.setEnabled("qoc_linenumber", true);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", true);
//                            formObject.setVisible("Btn_Modify_Maintaincharges", true);
                            formObject.setVisible("Btn_Delete_Maintaincharges", true);
                            formObject.setNGValue("qoc_assessablevalue", false);
                            formObject.setVisible("qoc_assessablevalue", true);
                        } else {
                            formObject.setEnabled("qoc_linenumber", false);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", false);
//                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
                            formObject.setVisible("Btn_Delete_Maintaincharges", false);
                            formObject.setVisible("qoc_assessablevalue", false);
                        }
                        break;

                    case "grnstartdate":
                    case "grnenddate":
                        formObject.clear("q_grn");
                        formObject.clear("q_multiplegrninvoicing");
                        Query = "select grnnumber from ext_supplypoinvoices "
                                + "where purchaseorderno = '" + formObject.getNGValue("purchaseorderno") + "' "
                                + "and multiplegrn='False' "
                                + "and format(grnsyncdate,'dd/MM/yyyy') "
                                + "between '" + formObject.getNGValue("grnstartdate") + "' "
                                + "and '" + formObject.getNGValue("grnenddate") + "'";
                        System.out.println("Query: " + Query);
                        List<List<String>> grnresult = formObject.getDataFromDataSource(Query);
                        for (int i = 0; i < grnresult.size(); i++) {
                            String grnnumber = grnresult.get(i).get(0);
                            formObject.addComboItem("q_grn", grnnumber, grnnumber);
                            addMultipleGRN(grnnumber);
                        }
                        formObject.RaiseEvent("WFSave");
                        break;

                }
                break;
            case "TAB_CLICKED":
                System.out.println("inside tab clicked of account by supply po");
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        System.out.println("inside tab1 clicked of account by supply po");
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 3: { //OtherCharges
                                setPOLineNumber("qoc_linenumber");
                                formObject.clear("qoc_chargescode");

                                String OtherChargesLineXML = "";
                                Query = "select freight,itemid,linenumber from cmplx_gateentryline where pinstanceid ='" + processInstanceId + "'";
                                result = formObject.getDataFromDataSource(Query);
                                float freightRate = Float.parseFloat(result.get(0).get(0));
//                                String freightRate = result.get(0).get(0);
//                                if (null != freightRate
//                                        || !freightRate.equalsIgnoreCase("0")
//                                        || !freightRate.equalsIgnoreCase("0.0")
//                                        || !freightRate.equalsIgnoreCase("0.00")) {

                                if ((null != (result.get(0).get(0))) && (Float.compare(freightRate, 0) > 0)) {
                                    System.out.println("freightRate null nhi h and value bhi badi h ");

                                    Query1 = "select assessableamount from cmplx_invoiceline where "
                                            + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + result.get(0).get(2) + "'";
                                    System.out.println("Query1 :" + Query1);
                                    result1 = formObject.getDataFromDataSource(Query1);
                                    String calculatedamount = objCalculations.calculatePercentAmount(result1.get(0).get(0), result.get(0).get(0));

                                    OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                            append("<ListItem><SubItem>").append(result.get(0).get(2)). //line nnuumber
                                            append("</SubItem><SubItem>").append(result.get(0).get(1)). //item number
                                            append("</SubItem><SubItem>").append("FRT"). //chrges code
                                            append("</SubItem><SubItem>").append("Pcs"). //categorydescription
                                            append("</SubItem><SubItem>").append("false").
                                            append("</SubItem><SubItem>").append(freightRate).//charges value 
                                            append("</SubItem><SubItem>").append(calculatedamount). //calculated amount
                                            append("</SubItem><SubItem>").append(formObject.getNGValue("suppliername")).
                                            append("</SubItem><SubItem>").append("Line").
                                            append("</SubItem><SubItem>").append(formObject.getNGValue("suppliercode")).
                                            append("</SubItem></ListItem>").toString();
                                }

                                result = formObject.getDataFromDataSource("select code from ChargesMaster");
                                for (int i = 0; i < result.size(); i++) {
                                    formObject.addComboItem("qoc_chargescode", result.get(i).get(0), result.get(i).get(0));
                                }
                                ListView ListViewq_othercharges = (ListView) formObject.getComponent("q_othercharges");
                                int rowCount = ListViewq_othercharges.getRowCount();
                                if (rowCount == 0) {
                                    Query = "select ch.linenumber,ch.itemnumber,ch.chargescode,ch.categorydescription,"
                                            + "ch.chargesvalue,ch.calculatedamount "
                                            + "from cmplx_linechargesdetails ch, cmplx_invoiceline inv "
                                            + "where ch.pinstanceid = inv.pinstanceid "
                                            + "and ch.linenumber = inv.linenumber "
                                            + "and ch.purchaseorderno = inv.purchaseorderno "
                                            + "and ch.pinstanceid = '" + processInstanceId + "'";
                                    System.out.println("Query :" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    if (result.size() > 0) {
                                        for (int i = 0; i < result.size(); i++) {
                                            OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                                    append("<ListItem><SubItem>").append(result.get(i).get(0)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(1)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(3)).
                                                    append("</SubItem><SubItem>").append("false").
                                                    append("</SubItem><SubItem>").append(result.get(i).get(4)).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(5)).
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                                    append("</SubItem><SubItem>").append("Line").
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                                    append("</SubItem></ListItem>").toString();
                                        }
                                    }
                                    if (!OtherChargesLineXML.equalsIgnoreCase("")) {
                                        System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                                        formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                                    }
                                }
///yaha tha pehle upper wala code
                            }
                            break;

                            case 5: { //Retention
                                Query = "select amount from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
                                objAccountsGeneral.setRetention(
                                        Query,
                                        "paymentterm",
                                        "retentioncredit",
                                        "retentionpercent",
                                        "retentionamount"
                                );

                            }
                            break;
                            case 6: { //WithHolding Tax
                                System.out.println("Inside tab 6 click");
                                Query = "select po.linenumber,po.itemnumber,po.tdsgroup,po.tdspercent,"
                                        + "inv.assessableamount,po.purchaseorderno from cmplx_poline po, cmplx_invoiceline inv "
                                        + "where po.pinstanceid = inv.pinstanceid and po.linenumber = inv.linenumber "
                                        + "and po.purchaseorderno = inv.purchaseorderno "
                                        + "and po.pinstanceid = '" + processInstanceId + "' "
                                        + "and po.tdsgroup is not null";
                                objAccountsGeneral.setWithHoldingTax(Query, "q_withholdingtax", processInstanceId);
                            }
                            break;
                            case 7: { //Tax Document
                                System.out.println("Inside case 7 Tax Document ");
                                Query = "select po.linenumber,po.itemnumber,gstin_gdi_uid,hsn,sac,igstrate,igsttaxamount,"
                                        + "cgstrate,cgsttaxamount,sgstrate,sgsttaxamount,nonbusinessusagepercent,exempt,"
                                        + "inv.newassessableamount,po.nongst,po.taxratetype,po.vatrate,po.vattaxamount,po.purchaseorderno "
                                        + "from cmplx_poline po, cmplx_invoiceline inv where "
                                        + "po.pinstanceid = '" + processInstanceId + "' and po.pinstanceid = inv.pinstanceid "
                                        + "and po.linenumber = inv.linenumber and po.itemnumber = inv.itemid ";
                                objAccountsGeneral.setTaxDocument(Query, "q_taxdocument", processInstanceId);

                            }
                            break;
                            case 8: {
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

    void setPOItemNumber(String itemnumberId, String linenumberId, String assessableamountId) {
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

    void addAssessableAmount(String operator, String linenumber, String itemnumber, BigDecimal assessableamount) {

        String addedAssessable = "";
        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoiceline");
        int rowCount = ListViewq_invoicedetails.getRowCount();
        System.out.println("Row count : " + rowCount);
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                if (formObject.getNGValue("q_invoiceline", i, 0).equalsIgnoreCase(linenumber)
                        && formObject.getNGValue("q_invoiceline", i, 1).equalsIgnoreCase(itemnumber)) {
                    if (operator.equalsIgnoreCase("Add")) {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoiceline", i, 10)).add(assessableamount).toString();
                    } else {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoiceline", i, 10)).subtract(assessableamount).toString();
                    }
                    formObject.setNGValue("q_invoiceline", i, 10, addedAssessable);
                    break;
                }
            }

        }
    }

    void addMultipleGRN(String GRNnumber) {
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
    }

    void CombineMultipleGrn() {
        System.out.println("inside CombineMultipleGrn");
        Query = "select linenumber, itemid, itemname, challanqty, grnqty, wbfirstwt,wbsecondwt, "
                + "wbnetweight, ponumber,freight,businessunit,state,costcentergroup,costcenter,department,gla from cmplx_gateentryline "
                + "where pinstanceid in (select grnprocessid from cmplx_multiplegrninvoicing where "
                + "pinstanceid = '" + processInstanceId + "')";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result : "+result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside for "+i);
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
