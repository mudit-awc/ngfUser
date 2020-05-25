/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.ServicePoInvoice;

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
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.excp.CustomExceptionHandler;
import com.newgen.omniforms.listener.FormListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Richa Maheshwari
 */
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
    float updated_partial_payment;
    float remaining_newgen;
    ArrayList<String> rem_new;

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
        System.out.println("Inside Event Dispached");
        System.out.println("event id : " + pEvent.getType().name());
        System.out.println("control id : " + pEvent.getSource().getName());
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
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
                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        break;

                    case "invoiceamount":
                    case "exchangerateotherthaninr":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        formObject.setNGValue("summ_invoiceamount", formObject.getNGValue("invoiceamount"));
                        break;

                    case "newbaseamount":
                        formObject.setNGValue("adjustedamountorigin", formObject.getNGValue("newbaseamount"));
                        formObject.setNGValue("summ_invoiceamountreporting", formObject.getNGValue("newbaseamount"));
                        break;

                    case "filestatus":
                        String filestatus = formObject.getNGValue("filestatus");
                        if (filestatus.equalsIgnoreCase("Exception")) {
                            formObject.setVisible("Label66", true);
                            formObject.setVisible("Combo3", true);
                            formObject.addComboItem("Combo3", "PO number not mentioned on invoice", "PO number not mentioned on invoice");
                            formObject.addComboItem("Combo3", "Incorrect PO number on invoice", "Incorrect PO number on invoice");
                            formObject.addComboItem("Combo3", "Invoice Number not mentioned on invoice", "Invoice Number not mentioned on invoice");
                            formObject.addComboItem("Combo3", "Incorrect invoice number on invoice", "Incorrect invoice number on invoice");
                            formObject.addComboItem("Combo3", "Incorrect details of Wonder Cement on invoice", "Incorrect details of Wonder Cement on invoice");
                            formObject.addComboItem("Combo3", "Mismatch of vendor name in invoice and PO", "Mismatch of vendor name in invoice and PO");
                            formObject.addComboItem("Combo3", "Quantity and Rate Variance between PO and Invoice", "Quantity and Rate Variance between PO and Invoice");
                            formObject.addComboItem("Combo3", "GST Rate is not matching", "GST Rate is not matching");
                            formObject.addComboItem("Combo3", "GST categorization is not matching", "GST categorization is not matching");
                            formObject.addComboItem("Combo3", "Mismatch of HSN Code between PO and Invoice", "Mismatch of HSN Code between PO and Invoice");
                            formObject.addComboItem("Combo3", "Budget Related Exception", "Budget Related Exception");
                            formObject.addComboItem("Combo3", "Invoice value is more than the PO value", "Invoice value is more than the PO value");
                        }
                        break;

                    case "qwht_linenumber":
                        setPOItemNumber("qwht_itemnumber", "qwht_linenumber", "qwht_adjustedoriginamount");
                        break;

                    case "qwht_adjustedoriginamount":
                        String tdsadjustedbaseamount = formObject.getNGValue("qwht_adjustedoriginamount");
                        String tdspercent = formObject.getNGValue("qwht_tdspercent");
                        String calculatedTDSValue = objCalculations.calculatePercentAmount(tdsadjustedbaseamount, tdspercent);
                        formObject.setNGValue("qwht_tdsamount", calculatedTDSValue);
//                        formObject.setNGValue("qwht_adjustedtdsamount", calculatedTDSValue);
                        formObject.setNGValue("qwht_adjustedtdsamount", new BigDecimal(calculatedTDSValue).setScale(0, BigDecimal.ROUND_HALF_UP));
                        break;

                    case "qpo_remainingqty":
                        BigDecimal remainingqty = new BigDecimal(formObject.getNGValue("qpo_remainingqty"));
                        BigDecimal poqty = new BigDecimal(formObject.getNGValue("qpo_poquantity"));
                        System.out.println("Rqty: " + remainingqty);
                        System.out.println("POqty: " + poqty);
                        if (remainingqty.compareTo(BigDecimal.ZERO) == 0) {
                            formObject.setNGValue("qpo_quantity", remainingqty);
                            formObject.setEnabled("qpo_quantity", false);
                            formObject.setVisible("Btn_addtoinvoice", false);
                            throw new ValidatorException(new FacesMessage("Remaining quantity is zero", ""));
                        } else {
                            formObject.setNGValue("qpo_quantity", objGeneral.getServicePoRemainingQty(remainingqty));
                            formObject.setEnabled("qpo_quantity", true);
                            formObject.setVisible("Btn_addtoinvoice", true);
                        }
                        break;

                    case "qpo_quantity":
                    case "qpo_rate":
                        String quantity = formObject.getNGValue("qpo_quantity");
                        String rate = formObject.getNGValue("qpo_rate");
                        BigDecimal bquantity = new BigDecimal(quantity);
                        BigDecimal remainingquantity = objGeneral.getServicePoRemainingQty(new BigDecimal(formObject.getNGValue("qpo_remainingqty")));
                        if (bquantity.compareTo(remainingquantity) > 0) {
                            formObject.setNGValue("qpo_quantity", BigDecimal.ZERO);
                            formObject.setNGValue("qpo_amount", BigDecimal.ZERO);
                            formObject.setNGValue("qpo_amountwithtax", BigDecimal.ZERO);
                            formObject.setVisible("Btn_addtoinvoice", false);
                            throw new ValidatorException(new FacesMessage("The Quantity can not be greater than its Remaining  Quantity :" + remainingquantity, ""));
                        }

                        if (!"".equalsIgnoreCase(quantity)
                                && !"".equalsIgnoreCase(rate)) {
                            System.out.println("Inside !blank");
                            String calculatedvalues[] = objCalculations.calculateLineTotalWithTax(quantity, rate, formObject.getNGValue("qpo_taxgroup")).split("/");
                            System.out.println("Calculatevalues" + calculatedvalues);
                            formObject.setNGValue("qpo_amountwithtax", calculatedvalues[0]);
                            formObject.setNGValue("qpo_amount", calculatedvalues[1]);
                            formObject.setNGValue("qpo_taxamount", calculatedvalues[2]);
                            formObject.setNGValue("qpo_taxpercent", calculatedvalues[3]);
                            formObject.setVisible("Btn_addtoinvoice", true);
                        }
                        break;

                    /* case "qpo_quantity":
                     case "qpo_rate":
                     String quantity = formObject.getNGValue("qpo_quantity");
                     String rate = formObject.getNGValue("qpo_rate");

                     if (!"".equalsIgnoreCase(quantity)
                     && !"".equalsIgnoreCase(rate)) {
                     System.out.println("Inside !blank");
                     String calculatedvalues[] = objCalculations.calculateLineTotalWithTax(quantity, rate, formObject.getNGValue("qpo_taxgroup")).split("/");
                     System.out.println("Calculatevalues" + calculatedvalues);
                     formObject.setNGValue("qpo_amountwithtax", calculatedvalues[0]);
                     formObject.setNGValue("qpo_amount", calculatedvalues[1]);
                     formObject.setNGValue("qpo_taxamount", calculatedvalues[2]);
                     formObject.setNGValue("qpo_taxpercent", calculatedvalues[3]);
                     }
                     break; */
                    case "qoc_category":
                    case "qoc_chargesvalue":
                        String qoc_category = formObject.getNGValue("qoc_category");

                        if (formObject.getNGValue("qoc_chargesat").equalsIgnoreCase("Line")) {
                            if (qoc_category.equalsIgnoreCase("Fixed")) {
                                formObject.setNGValue("qoc_calculatedamount", formObject.getNGValue("qoc_chargesvalue"));
                            } else if (qoc_category.equalsIgnoreCase("Pcs")) {
                                Query = "select quantity from cmplx_invoicedetails where "
                                        + "pinstanceid = '" + processInstanceId + "' "
                                        + "and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "' "
                                        + "and itemid = '" + formObject.getNGValue("qoc_itemnumber") + "' "
                                        + "and purchaseorderno= '" + formObject.getNGValue("qoc_ponumber") + "' ";
                                System.out.println("Query :" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                BigDecimal bChargeValue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));
                                BigDecimal bQuantity = new BigDecimal(result.get(0).get(0));
                                formObject.setNGValue("qoc_calculatedamount", bChargeValue.multiply(bQuantity).setScale(2, BigDecimal.ROUND_FLOOR).toString());
                            } else if (qoc_category.equalsIgnoreCase("Percent")) {
                                Query = "select assessableamount from cmplx_invoicedetails where "
                                        + "pinstanceid = '" + processInstanceId + "' "
                                        + "and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "' "
                                        + "and itemid = '" + formObject.getNGValue("qoc_itemnumber") + "' "
                                        + "and purchaseorderno= '" + formObject.getNGValue("qoc_ponumber") + "' ";
                                System.out.println("Query :" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                String calculatedamount = objCalculations.calculatePercentAmount(result.get(0).get(0), formObject.getNGValue("qoc_chargesvalue"));
                                formObject.setNGValue("qoc_calculatedamount", calculatedamount);
                            }
                        }
//                        else if (formObject.getNGValue("qoc_chargesat").equalsIgnoreCase("Header")) {
//
//                        }

                        break;
                    case "qprepayment_partialpaymentamount":
                        updated_partial_payment = Float.parseFloat(formObject.getNGValue("qprepayment_partialpaymentamount"));

                        break;
                    case "qoc_chargesat":
                        formObject.setNGValue("qoc_assessablevalue", "FALSE");
                        String qoc_chargesat = formObject.getNGValue("qoc_chargesat");
                        formObject.setNGValue("qoc_linenumber", "");
                        formObject.setNGValue("qoc_itemnumber", "");
                        formObject.setNGValue("qoc_chargescode", "");
                        formObject.setNGValue("qoc_category", "");
                        formObject.setNGValue("qoc_chargesvalue", "");
                        formObject.setNGValue("qoc_calculatedamount", "");
                        if (qoc_chargesat.equalsIgnoreCase("Header")) {
//                            formObject.setEnabled("qoc_linenumber", false);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", true);
                            formObject.setVisible("Btn_Add_Maintaincharges", false);
                            formObject.setVisible("Pick_Otherchrges_line_item_po", false);
                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
                        } else if (qoc_chargesat.equalsIgnoreCase("Line")) {
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", true);
                            formObject.setVisible("Pick_Otherchrges_line_item_po", true);
                            formObject.setVisible("Btn_Modify_Maintaincharges", true);
                        } else {
//                            formObject.setLocked("qoc_linenumber", false);
                            formObject.setVisible("Btn_Allocate_Maintaincharges", false);
                            formObject.setVisible("Btn_Add_Maintaincharges", false);
                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
//                            formObject.setVisible("Btn_Modify_Maintaincharges", false);
                            formObject.setVisible("Btn_Delete_Maintaincharges", false);
                            //                           formObject.setVisible("qoc_assessablevalue", false);
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                System.out.println("Inside mouse_click");
                switch (pEvent.getSource().getName()) {

                    case "Btn_calculateRetention":
                        System.out.println("Inside Btn_calculateRetention");
                        Query = "select amount from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
                        objAccountsGeneral.setRetention(
                                Query,
                                "paymenttermid",
                                "retentioncredit",
                                "retentionpercent",
                                "retentionamount"
                        );
                        break;

                    case "Clear_qocVendor":
                        formObject.setNGValue("qoc_vendoraccount", "");
                        formObject.setNGValue("qoc_vendoraccountcode", "");
                        break;
                    case "Btn_addtoinvoice":
                        System.out.println("Inside btn add to invoice ");
                        boolean rowexist = false;
                        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoicedetails");
                        int rowCount = ListViewq_invoicedetails.getRowCount();
                        int rowcount2 = formObject.getNGListIndex("q_invoicedetails");
                        System.out.println("Row count2 : " + rowcount2);
                        System.out.println("Row count : " + rowCount);
                        if (rowCount > 0) {
                            System.out.println(">0");
                            for (int i = 0; i < rowCount; i++) {
                                System.out.println("Loop " + i);
                                if (formObject.getNGValue("qpo_linenumber").equalsIgnoreCase(formObject.getNGValue("q_invoicedetails", i, 0))
                                        && formObject.getNGValue("qpo_ponumber").equalsIgnoreCase(formObject.getNGValue("q_invoicedetails", i, 13))) {
                                    System.out.println("Row exist");
                                    rowexist = true;
                                    break;
                                }
                            }
                        }
                        if (rowexist) {
                            throw new ValidatorException(new FacesMessage("Line item already added in the invoice", ""));
                        } else {
                            String discountamount = "0";
                            String discount_percent = formObject.getNGValue("qpo_discountpercent");
                            String discount_amount = formObject.getNGValue("qpo_discountamount");
                            if (!discount_percent.equalsIgnoreCase("")
                                    || !discount_percent.equalsIgnoreCase("0.0")
                                    || !discount_percent.equalsIgnoreCase("0")) {
                                System.out.println("Inside qpo_discountpercent");
                                discountamount = objCalculations.calculatePercentAmount(formObject.getNGValue("qpo_amount"), discount_percent);
                            } else {
                                System.out.println("Inside else qpo_discountpercent");
                                discountamount = discount_amount;
                            }
                            System.out.println("Discout Amount :" + discountamount);
                            BigDecimal assessableamount = objCalculations.calculateDifference(formObject.getNGValue("qpo_amount"), discountamount);
                            System.out.println("Assessable amount :" + assessableamount);

                            String InvoiceLineXML = "";
                            InvoiceLineXML = (new StringBuilder()).append(InvoiceLineXML).
                                    append("<ListItem><SubItem>").append(formObject.getNGValue("qpo_linenumber")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_itemnumber")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_itemname")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_quantity")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_rate")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_amount")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_discountpercent")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_discountamount")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_taxpercent")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_taxamount")).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_amountwithtax")).
                                    append("</SubItem><SubItem>").append(assessableamount).
                                    append("</SubItem><SubItem>").append(assessableamount).
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_ponumber")).
                                    append("</SubItem></ListItem>").toString();

                            System.out.println("Invoice Line XML " + InvoiceLineXML);
                            formObject.NGAddListItem("q_invoicedetails", InvoiceLineXML);
                            formObject.setNGValue("qpo_linenumber", "");
                            formObject.setNGValue("qpo_itemnumber", "");
                            formObject.setNGValue("qpo_quantity", "");
                            formObject.setNGValue("qpo_amount", "");
                            formObject.setNGValue("qpo_amountwithtax", "");
                            formObject.setNGValue("qpo_rate", "");
                            refreshRetention();
                            refreshWithHoldingTaxDocument();
                            formObject.RaiseEvent("WFSave");
                        }
                        break;

                    case "Pick_ledgeraccount":
                        Query = "select AccountId,Description from LedgerACMaster order by AccountId asc";
                        objPicklistListenerHandler.openPickList("q_ledgeraccountdesc", "Account ID,Description", "Ledger Account Master", 70, 70, Query);
                        break;

                    case "Pick_ledgerbusinessunit":
                        System.out.println("inside if");
                        Query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
                        objPicklistListenerHandler.openPickList("q_ledgerbusinessunit", "Code, Name", "Business Unit Master", 70, 70, Query);
                        break;

                    case "Pick_ledgerstate":
                        String q_ledgerbusinessunit = formObject.getNGValue("q_ledgerbusinessunit");
                        if (q_ledgerbusinessunit.equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly select the business unit value"));
                        } else {
                            Query = "select statecode,statename from statemaster where businessunitcode = '" + q_ledgerbusinessunit + "' order by statecode asc";
                            objPicklistListenerHandler.openPickList("q_ledgerstate", "Code, Name", "Business Unit Master", 70, 70, Query);
                        }
                        break;

                    case "Pick_ledgercostcenter":
                        Query = "select value, Description from CostCenter order by value asc";
                        objPicklistListenerHandler.openPickList("q_ledgercostcenter", "Value,Description", "Cost Center Master", 70, 70, Query);
                        break;

                    case "Pick_ledgergla":
                        Query = "select Value,Description from GLAMaster order by Value asc";
                        objPicklistListenerHandler.openPickList("q_ledgergla", "Value,Description", "GLA Master", 70, 70, Query);
                        break;

                    case "Pick_qocVendor":
                        Query = "select VendorCode, VendorName from VendorMaster order by VendorCode asc";
                        objPicklistListenerHandler.openPickList("qoc_vendoraccount", "Vendor Code, Vendor Name", "Vendor Master", 70, 70, Query);
                        System.out.println("inside Pick_tdsgroup ");
                        break;

                    case "Pick_hsnsacvalue":
                        String hsnsactype = formObject.getNGValue("hsnsactype");
                        if (hsnsactype.equalsIgnoreCase("HSN")) {
                            Query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                            objPicklistListenerHandler.openPickList("hsnsacvalue", "HSNCode,Description", "HSN Value", 70, 70, Query);
                        } else if (hsnsactype.equalsIgnoreCase("SAC")) {
                            Query = "select SACCode,Description from SACMaster order by SACCode asc";
                            objPicklistListenerHandler.openPickList("hsnsacvalue", "SACCode,Description", "SAC Value", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the GST type value"));
                        }
                        break;

                    case "Pick_tdsgroup":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("qwht_tdsgroup", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Pick_Otherchrges_line_item_po":
                        Query = "select linenumber,itemid,purchaseorderno from cmplx_invoicedetails where pinstanceid ='" + processInstanceId + "'";
                        objPicklistListenerHandler.openPickList("qoc_linenumber", "linenumber,itemid,purchaseorderno", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Pick_fd_department":
                        Query = "select Value,Description from department order by description asc";
                        objPicklistListenerHandler.openPickList("fd_departmentdescription", "Value,Description", "Department Master", 35, 35, Query);
                        break;

                    case "Pick_vendor":
                        Query = "select VendorCode,VendorName from VendorMaster order by VendorCode asc";
                        objPicklistListenerHandler.openPickList("fd_vendordsc", "VendorCode,VendorName", "Vendor Master", 35, 35, Query);
                        break;

                    case "Pick_fd_warehouse":
                        Query = "select WarehouseCode,WarehouseName from WarehouseMaster order by WarehouseCode asc";
                        objPicklistListenerHandler.openPickList("fd_warehousedsc", "WarehouseCode,WarehouseName", "Warehouse Master", 35, 35, Query);
                        break;

                    case "Btn_Add_Maintaincharges":
                        String qoc_assessablevalue = formObject.getNGValue("qoc_assessablevalue");
                        String Line_number = formObject.getNGValue("qoc_linenumber");
                        String Item_number = formObject.getNGValue("qoc_itemnumber");
                        if (Line_number.equalsIgnoreCase("") || Item_number.equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly select Line number and Item number "));
                        } else {
                            if (qoc_assessablevalue.equalsIgnoreCase("TRUE")) {
                                addAssessableAmount(
                                        "Add",
                                        Line_number, Item_number,
                                        formObject.getNGValue("qoc_ponumber"),
                                        new BigDecimal(formObject.getNGValue("qoc_calculatedamount")));
                            }
                            formObject.ExecuteExternalCommand("NGAddRow", "q_othercharges");
                            if (qoc_assessablevalue.equalsIgnoreCase("TRUE")) {
                                refreshWithHoldingTaxDocument();
                            }
                            formObject.RaiseEvent("WFSave");
                        }
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
                                    formObject.getNGValue("q_othercharges", selectedRowIndex, 10),
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
                        Query = "select amount,linenumber,itemid,assessableamount,purchaseorderno from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
                        System.out.println("Query :" + Query);
                        result = formObject.getDataFromDataSource(Query);

                        if (qoc_category.equalsIgnoreCase("Fixed")) {
                            String OtherChargesLineXML = "";
                            ArrayList<String> baseamount = new ArrayList<String>();
                            for (int i = 0; i < result.size(); i++) {
                                baseamount.add(result.get(i).get(0));
                            }
                            System.out.println("baseamount : " + baseamount);
                            BigDecimal btotalnetamount = objCalculations.calculateSum(baseamount);
                            System.out.println("btotalnetamount : " + btotalnetamount);
                            BigDecimal bchargesvalue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));
                            System.out.println("bchargesvalue" + bchargesvalue);
                            for (int i = 0; i < result.size(); i++) {
                                System.out.println("Inside for loop");
                                BigDecimal blinenetamount = new BigDecimal(result.get(i).get(3));//                              
                                BigDecimal bchargesvalue_line = (blinenetamount.divide(btotalnetamount, RoundingMode.HALF_UP)).multiply(bchargesvalue).setScale(2, BigDecimal.ROUND_FLOOR);
                                System.out.println("bchargesvalue_line" + bchargesvalue_line);
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
                                        append("</SubItem><SubItem>").append(result.get(i).get(4)).
                                        append("</SubItem></ListItem>").toString();

                                if (qoc_assessablevalueflag.equalsIgnoreCase("TRUE")) {
                                    addAssessableAmount(
                                            "Add",
                                            result.get(i).get(1),
                                            result.get(i).get(2),
                                            result.get(i).get(4),
                                            bchargesvalue_line
                                    );
                                }
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);

                        } else if (qoc_category.equalsIgnoreCase("Percent")) {
                            String OtherChargesLineXML = "";
                            for (int i = 0; i < result.size(); i++) {
                                String calculatedamount = objCalculations.calculatePercentAmount(
                                        result.get(i).get(3),
                                        formObject.getNGValue("qoc_chargesvalue")
                                );
                                OtherChargesLineXML = (new StringBuilder()).append(OtherChargesLineXML).
                                        append("<ListItem><SubItem>").append(result.get(i).get(1)).
                                        append("</SubItem><SubItem>").append(result.get(i).get(2)).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargescode")).
                                        append("</SubItem><SubItem>").append(qoc_category).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_assessablevalue")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesvalue")).
                                        append("</SubItem><SubItem>").append(calculatedamount).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccount")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_chargesat")).
                                        append("</SubItem><SubItem>").append(formObject.getNGValue("qoc_vendoraccountcode")).
                                        append("</SubItem><SubItem>").append(result.get(i).get(4)).
                                        append("</SubItem></ListItem>").toString();

                                if (qoc_assessablevalueflag.equalsIgnoreCase("TRUE")) {
                                    addAssessableAmount(
                                            "Add",
                                            result.get(i).get(1),
                                            result.get(i).get(2),
                                            result.get(i).get(4),
                                            new BigDecimal(calculatedamount)
                                    );
                                }
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
//                            Query = "select assessableamount from cmplx_invoicedetails where "
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
                                        formObject.getNGValue("qoc_ponumber"),
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
                                        formObject.getNGValue("qoc_ponumber"),
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
                                        formObject.getNGValue("qoc_ponumber"),
                                        new BigDecimal(formObject.getNGValue("q_othercharges", selectedRowIndex1, 6))
                                );
                            }
                        }
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_othercharges");
                        if (qoc_assessablevalue_mod.equalsIgnoreCase("TRUE")) {
                            refreshWithHoldingTaxDocument();
                        }
                        formObject.RaiseEvent("WFSave");
                        break;
//                        throw new ValidatorException(new FacesMessage("Please check withholding tax and tax document"));

                    case "Btn_Modify_financialdimension":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_financialdimension");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Add_Prepayment":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_prepayment");
                        break;

                    case "Btn_Modify_Prepayment":

                        int selectedrow = formObject.getSelectedIndex("q_prepayment");
                        System.out.println("Listview selectedrow: " + selectedrow);
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

                    case "Btn_Delete_Prepayment":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_prepayment");
                        break;

                    case "Btn_Add_Withholdingtax":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_withholdingtax");
                        break;

                    case "Btn_Delete_Invoice":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_invoicedetails");
                        refreshRetention();
                        refreshWithHoldingTaxDocument();
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Add_Taxdocument":
                        objAccountsGeneral.setSameSgstCgst(
                                formObject.getSelectedIndex("q_taxdocument"),
                                formObject.getNGValue("qtd_taxcomponent")
                        );
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_fetchpodetails":
//                        String AccessToken = new CallAccessTokenService().getAccessToken();
                        new CallPurchaseOrderService().GetSetPurchaseOrder("", "Service", formObject.getNGValue("ponumber"), "Service");
                        formObject.setNGValue("ponumber", "");
                        break;

                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Clear_ledgeraccount":
                        formObject.clear("q_ledgeraccountdesc");
                        formObject.clear("q_ledgeraccount");
                        break;

                    case "Clear_ledgercostcenter":
                        formObject.clear("q_ledgercostcenter");
                        formObject.clear("q_ledgercostcentervalue");
                        formObject.clear("q_ledgercostcentergroupvalue");
                        formObject.clear("q_ledgercostcentergroup");
                        break;

                    case "Clear_vendor":
                        formObject.setNGValue("fd_vendor", "");
                        formObject.setNGValue("fd_vendordsc", "");
                        break;

                    case "Clear_ledgergla":
                        formObject.clear("q_ledgergla");
                        formObject.clear("q_ledgerglavalue");
                        break;

                    case "Clear_fd_department":
                        formObject.clear("fd_department");
                        formObject.clear("fd_departmentdescription");
                        break;

                    case "Clear_fd_warehouse":
                        formObject.clear("fd_warehousecode");
                        formObject.clear("fd_warehousedsc");
                        break;

                    case "qtd_exempt":
                        String exempt = formObject.getNGValue("qtd_exempt");
                        if (exempt.equalsIgnoreCase("true")) {
                            formObject.setNGValue("qtd_taxamount", "0");
                            formObject.setNGValue("qtd_taxamountadjustment", "0");
                            formObject.setLocked("qtd_taxamountadjustment", true);
                        } else {
                            String Query = "select newassessableamount from cmplx_invoicedetails where "
                                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                                    + "and itemid = '" + formObject.getNGValue("qtd_itemnumber") + "'";
                            System.out.println("Query :" + Query);
                            String taxamount = objCalculations.calculatePercentAmount(
                                    formObject.getDataFromDataSource(Query).get(0).get(0),
                                    formObject.getNGValue("qtd_taxrate")
                            );
                            formObject.setNGValue("qtd_taxamount", taxamount);
                            formObject.setNGValue("qtd_taxamountadjustment", taxamount);
                            formObject.setLocked("qtd_taxamountadjustment", false);
                        }
                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 2: { //Other Charges
                                formObject.clear("qoc_chargescode");
                                result = formObject.getDataFromDataSource("select code from ChargesMaster");
                                for (int i = 0; i < result.size(); i++) {
                                    formObject.addComboItem("qoc_chargescode", result.get(i).get(0), result.get(i).get(0));
                                }

                                int rowCount = formObject.getLVWRowCount("q_othercharges");
                                if (rowCount == 0) {
                                    Query = "select ch.linenumber,ch.itemnumber,ch.chargescode,ch.categorydescription,"
                                            + "ch.chargesvalue,ch.calculatedamount,ch.purchaseorderno,ch.assessablevalue,"
                                            + "inv.assessableamount,inv.quantity "
                                            + "from cmplx_linechargesdetails ch, cmplx_invoicedetails inv "
                                            + "where ch.pinstanceid = inv.pinstanceid "
                                            + "and ch.linenumber = inv.linenumber "
                                            + "and ch.purchaseorderno = inv.purchaseorderno "
                                            + "and ch.pinstanceid = '" + processInstanceId + "' "
                                            + "order by linenumber,purchaseorderno";
                                    System.out.println("Query :" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    if (result.size() > 0) {
                                        String OtherChargesLineXML = "";
                                        for (int i = 0; i < result.size(); i++) {
                                            String AssessableValue = "FALSE";
                                            String calculatedamount = "";

                                            if (result.get(i).get(3).equalsIgnoreCase("Percent")) {
                                                calculatedamount = objCalculations.calculatePercentAmount(
                                                        result.get(i).get(8),
                                                        result.get(i).get(4)
                                                );
                                            } else if (result.get(i).get(3).equalsIgnoreCase("Pcs")) {
                                                BigDecimal bChargeValue = new BigDecimal(result.get(i).get(4));
                                                BigDecimal bQuantity = new BigDecimal(result.get(i).get(9));
                                                calculatedamount = bChargeValue.multiply(bQuantity).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                                            } else if (result.get(i).get(3).equalsIgnoreCase("Fixed")) {
                                                calculatedamount = result.get(i).get(4);
                                            }

                                            if (result.get(i).get(7).equals("1")) {
                                                AssessableValue = "TRUE";
                                                addAssessableAmount(
                                                        "Add",
                                                        result.get(i).get(0),
                                                        result.get(i).get(1),
                                                        result.get(i).get(6),
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
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("suppliercode")+"_"+formObject.getNGValue("suppliername")).
                                                    append("</SubItem><SubItem>").append("Line").
                                                    append("</SubItem><SubItem>").append(formObject.getNGValue("suppliercode")).
                                                    append("</SubItem><SubItem>").append(result.get(i).get(6)).
                                                    append("</SubItem></ListItem>").toString();
                                        }
                                        System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                                        formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                                    }
                                }
                                formObject.RaiseEvent("WFSave");
                            }
                            break;

                            case 4: { //Retention
//                                System.out.println("Inside retention--");
//                                Query = "select amount from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
//                                objAccountsGeneral.setRetention(
//                                        Query,
//                                        "paymenttermid",
//                                        "retentioncredit",
//                                        "retentionpercent",
//                                        "retentionamount"
//                                );
                                System.out.println("Callig custom event ");
                                HashMap<String, String> hm = new HashMap<>();
                                throw new ValidatorException(new CustomExceptionHandler("Btn_calculateRetention", "", "", hm));
                            }
//                            break;

                            case 5: { //WithHolding Tax
                                System.out.println("Inside tab 5 click");
                                Query = "select po.linenumber,po.itemnumber,po.tdsgroup,po.tdspercent,"
                                        + "inv.newassessableamount,po.purchaseorderno,inv.assessableamount, po.tdsgroup from cmplx_polinedetails po, cmplx_invoicedetails inv"
                                        + " where po.pinstanceid = inv.pinstanceid and po.linenumber = inv.linenumber "
                                        + "and po.purchaseorderno = inv.purchaseorderno and po.pinstanceid = '" + processInstanceId + "' "
                                        + "and po.tdsgroup is not null";
                                System.out.println("Query Accounts: " + Query);
                                objAccountsGeneral.setWithHoldingTax(Query, "q_withholdingtax", processInstanceId);
//                                objAccountsGeneral.setWithHoldingTaxLower(Query, "q_withholdingtax", processInstanceId);
                            }
                            break;

                            case 6: { //Tax Document
                                System.out.println("Inside case 6 Tax Document Testing");
                                Query = "select po.linenumber,po.itemnumber,gstngdiuid,COALESCE(hsn,''),COALESCE(sac,''),COALESCE(igstrate,'0'),"
                                        + "COALESCE(igsttaxamount,'0'),COALESCE(cgstrate,'0'),COALESCE(cgsttaxamount,'0'),"
                                        + "COALESCE(sgstrate,'0'),COALESCE(sgsttaxamount,'0'),COALESCE(nonbussinessusagepercent,'0'),"
                                        + "exempt,inv.newassessableamount,po.nongst,COALESCE(po.taxratetype,''),COALESCE(po.vatrate,'0'),"
                                        + "COALESCE(po.vattaxamount,'0'),po.purchaseorderno "
                                        + "from cmplx_polinedetails po, cmplx_invoicedetails inv where "
                                        + "po.pinstanceid = '" + processInstanceId + "' and po.pinstanceid = inv.pinstanceid "
                                        + "and po.linenumber = inv.linenumber and po.itemnumber = inv.itemid ";
                                objAccountsGeneral.setTaxDocument(Query, "q_taxdocument", processInstanceId);
                            }
                            break;

                            case 7: { //PrePayment
                                String AccessToken = new CallAccessTokenService().getAccessToken();
                                new CallPrePaymentService().GetSetPrePaymentLines(AccessToken, "", "Service", processInstanceId);
                            }
                            break;
                            case 8: { //Financial Dimension
                                objAccountsGeneral.setFinancialDimension("q_financialdimension", processInstanceId);
                            }
                            break;
                        }
                        break;

                    case "Tab2":
                        switch (formObject.getSelectedSheet("Tab2")) {
                            case 3: {
                                objAccountsGeneral.getsetServicePoSummary(processInstanceId);
                            }
                        }
                }
                break;
        }
    }

    @Override
    public void formLoaded(FormEvent arg0
    ) {
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
    public void formPopulated(FormEvent arg0
    ) {
        System.out.println("inside form populate ofaccounts service po");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------I ntiation Workstep Loaded from form populated........---------------------------");
        objGeneral = new General();
        formObject.setEnabled("filestatus", true);
        formObject.setNGValue("Text15", "");
        formObject.setEnabled("Text15", true);
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        formObject.addComboItem("filestatus", "Exception", "Exception");

        String currentdate = objGeneral.getCurrentDate();
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
            System.out.println("resut is " + result);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("state", result.get(i).get(0), result.get(i).get(0));
            }

            Query = "select HeadName from ServicePoHeadMaster order by HeadName asc";
            System.out.println("Query is " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result is" + result);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activityName.equalsIgnoreCase("AXSyncException")) {
            formObject.setEnabled("holdamount", true);
            formObject.setEnabled("retentionamount", true);
        }
//        financial_dimension();
        objAccountsGeneral.setFinancialDimension("q_financialdimension", processInstanceId);

        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);

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
        objAccountsGeneral = new AccountsGeneral();
        objAccountsGeneral.getsetServicePoSummary(processInstanceId);
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
        String withtax = "";
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        if (activityName.equalsIgnoreCase("AccountsChecker")) {
            formObject.setNGValue("accountschecker", userName);
        }
        objGeneral.compareDate(formObject.getNGValue("invoicedate"), formObject.getNGValue("postingdate"));
        objAccountsGeneral.getsetServicePoSummary(processInstanceId);
        Query = "select calculatewithholdingtax,* from VendorMaster where VendorCode = '" + formObject.getNGValue("suppliercode") + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            withtax = result.get(0).get(0);
        }
        int rowCount = formObject.getLVWRowCount("q_withholdingtax");
        int rowCount1 = formObject.getLVWRowCount("q_taxdocument");
        if (withtax.equalsIgnoreCase("1")) {
            rowCount = 1;
        }

        if (rowCount == 0 || rowCount1 == 0) {
            throw new ValidatorException(new FacesMessage("KIndly select Tax Document and Withholding Tax."));
        }
        String filestatus = formObject.getNGValue("filestatus");
        if (activityName.equalsIgnoreCase("AccountsMaker")) {
            if (filestatus.equalsIgnoreCase("Approved")) {
                objGeneral.checkServicePoDoAUser("AccountsChecker", "");
            } else if (filestatus.equalsIgnoreCase("Reject")) {
                Query = "select top 1 ApproverLevel from ServicePOApproverMaster "
                        + "where head = '" + formObject.getNGValue("proctype") + "' "
                        + "and site = '" + formObject.getNGValue("site") + "' "
                        + "and state = '" + formObject.getNGValue("state") + "' "
                        + "and department = '" + formObject.getNGValue("department") + "'"
                        + "and ApproverLevel not in ('AccountsMaker','AccountsChecker') order by ApproverLevel desc";
                System.out.println("Query :" + Query);
                result = formObject.getDataFromDataSource(Query);
                formObject.setNGValue("FilterDoA_ApproverLevel", result.get(0).get(0));
                formObject.setNGValue("levelflag", result.get(0).get(0));
            }
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
        objGeneral.maintainHistory(
                userName,
                activityName,
                formObject.getNGValue("filestatus"),
                "",
                formObject.getNGValue("Text51"),
                "q_transactionhistory"
        );

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

    void setPOItemNumber(String itemnumberId, String linenumberId, String assessableamountId) {
        Query = "select itemid,assessableamount from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "' "
                + "and linenumber = '" + formObject.getNGValue(linenumberId) + "'";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        formObject.setNGValue(itemnumberId, result.get(0).get(0));

        if (!assessableamountId.equalsIgnoreCase("")) {
            formObject.setNGValue(assessableamountId, result.get(0).get(1));
        }
    }

    void addAssessableAmount(String operator, String linenumber, String itemnumber, String PurchaseOrder, BigDecimal assessableamount) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String addedAssessable = "";
        // ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoicedetails");
        int rowCount = formObject.getLVWRowCount("q_invoicedetails");
        System.out.println("Row count : " + rowCount);
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                System.out.println("Inside for");
                System.out.println("Line Number :" + linenumber + " / " + formObject.getNGValue("q_invoicedetails", i, 0));
                System.out.println("Item Number :" + itemnumber + " / " + formObject.getNGValue("q_invoicedetails", i, 1));
                System.out.println("Purchase Order :" + PurchaseOrder + " / " + formObject.getNGValue("q_invoicedetails", i, 13));
                if (formObject.getNGValue("q_invoicedetails", i, 0).equalsIgnoreCase(linenumber)
                        && formObject.getNGValue("q_invoicedetails", i, 1).equalsIgnoreCase(itemnumber)
                        && formObject.getNGValue("q_invoicedetails", i, 13).equalsIgnoreCase(PurchaseOrder)) {
                    System.out.println("Inside for if");
                    if (operator.equalsIgnoreCase("Add")) {
                        System.out.println("Inside add");
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoicedetails", i, 12)).add(assessableamount).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                    } else {
                        System.out.println("Inside else add");
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoicedetails", i, 12)).subtract(assessableamount).setScale(2, BigDecimal.ROUND_FLOOR).toString();
                    }
                    System.out.println("addedAssessable " + addedAssessable);
                    formObject.setNGValue("q_invoicedetails", i, 12, addedAssessable);
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

    void refreshRetention() {
        formObject.clear("retentioncredit");
        formObject.clear("retentionpercent");
        formObject.clear("retentionamount");
    }
}
