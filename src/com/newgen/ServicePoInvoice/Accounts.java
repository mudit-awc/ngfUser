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
import com.newgen.omniforms.listener.FormListener;
import java.math.BigDecimal;
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
                        break;

                    case "newbaseamount":
                        formObject.setNGValue("adjustedamountorigin", formObject.getNGValue("newbaseamount"));
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

                    case "qoc_linenumber":
//                        setPOItemNumber("qoc_itemnumber", "qoc_linenumber", "");
//                        String suppliercode = formObject.getNGValue("suppliercode");
//                        formObject.setNGValue("qoc_vendoraccount", suppliercode + "-" + formObject.getNGValue("suppliername"));
//                        formObject.setNGValue("qoc_vendoraccountcode", suppliercode);
                        break;

                    case "qwht_linenumber":
                        setPOItemNumber("qwht_itemnumber", "qwht_linenumber", "qwht_adjustedoriginamount");
                        break;

                    case "qwht_adjustedoriginamount":
                        String tdsadjustedbaseamount = formObject.getNGValue("qwht_adjustedoriginamount");
                        String tdspercent = formObject.getNGValue("qwht_tdspercent");
                        String calculatedTDSValue = objCalculations.calculatePercentAmount(tdsadjustedbaseamount, tdspercent);
                        formObject.setNGValue("qwht_tdsamount", calculatedTDSValue);
                        formObject.setNGValue("qwht_adjustedtdsamount", calculatedTDSValue);
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
                                        + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
                                System.out.println("Query :" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                BigDecimal bChargeValue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));
                                BigDecimal bQuantity = new BigDecimal(result.get(0).get(0));
                                formObject.setNGValue("qoc_calculatedamount", bChargeValue.multiply(bQuantity).setScale(2, BigDecimal.ROUND_FLOOR).toString());
                            } else if (qoc_category.equalsIgnoreCase("Percent")) {
                                Query = "select assessableamount from cmplx_invoicedetails where "
                                        + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
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
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Clear_qocVendor":
                        formObject.setNGValue("qoc_vendoraccount", "");
                        formObject.setNGValue("qoc_vendoraccountcode", "");
                        break;
                    case "Btn_addtoinvoice":
                        System.out.println("Inside btn add to invoice click");
                        boolean rowexist = false;
                        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoicedetails");
                        int rowCount = ListViewq_invoicedetails.getRowCount();
                        System.out.println("Row count : " + rowCount);
                        if (rowCount > 0) {
                            System.out.println(">0");
                            for (int i = 0; i < rowCount; i++) {
                                System.out.println("Loop " + i);
                                if (formObject.getNGValue("qpo_linenumber").equalsIgnoreCase(formObject.getNGValue("q_invoicedetails", i, 0))) {
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
                            formObject.RaiseEvent("WFSave");
                        }
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
                        Query = "select linenumber,itemid,purchaseorderno from cmplx_invoicedetails where pinstanceid ='"+processInstanceId+"'";
                        objPicklistListenerHandler.openPickList("qoc_linenumber", "linenumber,itemid,purchaseorderno", "TDS Group Master", 70, 70, Query);
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

                    case "Btn_Add_Prepayment":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_prepayment");
                        break;

                    case "Btn_Modify_Prepayment":
                        int row_count = formObject.getLVWRowCount("q_prepayment");
                        System.out.println("Listview Rowcount: " + row_count);
                        for (int i = 0; i < row_count; i++) {
                            System.out.println("Inside for loop");
                            float remaining_newgen = Float.parseFloat(formObject.getNGValue("q_prepayment", i, 3));
                            System.out.println("Default remaining value: " + remaining_newgen);
                            float remaining_now = remaining_newgen - updated_partial_payment;
                            System.out.println("Updated value: " + remaining_now);
                            if (remaining_now < 0) {
                                throw new ValidatorException(new FacesMessage("Partial Payment cannot be greater then remaining amount"));
                            } else {
                                formObject.setNGValue("q_prepayment", i, 3, String.valueOf(remaining_now));
                                System.out.println("Updated value set in ListView");
                            }
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
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Allocate_Maintaincharges":
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
                            BigDecimal btotalnetamount = objCalculations.calculateSum(baseamount);
                            BigDecimal bchargesvalue = new BigDecimal(formObject.getNGValue("qoc_chargesvalue"));

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
                                        append("</SubItem><SubItem>").append(result.get(i).get(4)).
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
                                        append("</SubItem><SubItem>").append(result.get(i).get(4)).
                                        append("</SubItem></ListItem>").toString();

                                addAssessableAmount("Add", result.get(i).get(1), result.get(i).get(2), new BigDecimal(formObject.getNGValue("qoc_chargesvalue")));
                            }
                            System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                            formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                            Query = "select assessableamount from cmplx_invoicedetails where "
                                    + "pinstanceid = '" + processInstanceId + "' and linenumber = '" + formObject.getNGValue("qoc_linenumber") + "'";
                            System.out.println("Query :" + Query);
                            result = formObject.getDataFromDataSource(Query);
                            String calculatedamount = objCalculations.calculatePercentAmount(result.get(0).get(0), formObject.getNGValue("qoc_chargesvalue"));
                            formObject.setNGValue("qoc_calculatedamount", calculatedamount);
                        } else if (qoc_category.equalsIgnoreCase("Pcs")) {
                            throw new ValidatorException(new FacesMessage("Not Applicable"));
                        }
                        break;

                    case "Btn_Add_Taxdocument":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        break;

                    case "Btn_fetchpodetails":
//                        String AccessToken = new CallAccessTokenService().getAccessToken();
                        new CallPurchaseOrderService().GetSetPurchaseOrder("", "Service", formObject.getNGValue("ponumber"), "Service");
                        formObject.setNGValue("ponumber", "");
                        break;

                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 2: { //OtherCharges
                                //  setPOLineNumber("qoc_linenumber");
                                formObject.clear("qoc_chargescode");
                                result = formObject.getDataFromDataSource("select code from ChargesMaster");
                                for (int i = 0; i < result.size(); i++) {
                                    formObject.addComboItem("qoc_chargescode", result.get(i).get(0), result.get(i).get(0));
                                }
                                ListView ListViewq_othercharges = (ListView) formObject.getComponent("q_othercharges");
                                int rowCount = ListViewq_othercharges.getRowCount();
                                if (rowCount == 0) {
                                    Query = "select ch.linenumber,ch.itemnumber,ch.chargescode,ch.categorydescription,"
                                            + "ch.chargesvalue,ch.calculatedamount,ch.purchaseorderno "
                                            + "from cmplx_linechargesdetails ch, cmplx_invoicedetails inv "
                                            + "where ch.pinstanceid = inv.pinstanceid "
                                            + "and ch.linenumber = inv.linenumber "
                                            + "and ch.purchaseorderno = inv.purchaseorderno "
                                            + "and ch.pinstanceid = '" + processInstanceId + "'";
                                    System.out.println("Query :" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    if (result.size() > 0) {
                                        String OtherChargesLineXML = "";
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
                                                    append("</SubItem><SubItem>").append(result.get(i).get(6)).
                                                    append("</SubItem></ListItem>").toString();
                                        }
                                        System.out.println("OtherCharges Line XML " + OtherChargesLineXML);
                                        formObject.NGAddListItem("q_othercharges", OtherChargesLineXML);
                                    }
                                }
                            }
                            break;

                            case 4: { //Retention
                                Query = "select amount from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
                                objAccountsGeneral.setRetention(
                                        Query,
                                        "paymenttermid",
                                        "retentioncredit",
                                        "retentionpercent",
                                        "retentionamount"
                                );
                            }
                            break;

                            case 5: { //WithHolding Tax
                                System.out.println("Inside tab 5 click");
                                Query = "select po.linenumber,po.itemnumber,po.tdsgroup,po.tdspercent,"
                                        + "inv.assessableamount,po.purchaseorderno from cmplx_polinedetails po, cmplx_invoicedetails inv "
                                        + "where po.pinstanceid = inv.pinstanceid and po.linenumber = inv.linenumber "
                                        + "and po.purchaseorderno = inv.purchaseorderno and po.pinstanceid = '" + processInstanceId + "' "
                                        + "and po.tdsgroup is not null";
                                objAccountsGeneral.setWithHoldingTax(Query, "q_withholdingtax", processInstanceId);
                            }
                            break;

                            case 6: { //Tax Document
                                System.out.println("Inside case 6 Tax Document Testing");
                                Query = "select po.linenumber,po.itemnumber,gstngdiuid,hsn,sac,igstrate,igsttaxamount,"
                                        + "cgstrate,cgsttaxamount,sgstrate,sgsttaxamount,nonbussinessusagepercent,exempt,"
                                        + "inv.newassessableamount,po.nongst,po.taxratetype,po.vatrate,po.vattaxamount,po.purchaseorderno "
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
        System.out.println("inside form populate ofaccounts service po");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------I ntiation Workstep Loaded from form populated........---------------------------");
        objGeneral = new General();
        formObject.setNGValue("filestatus", "");
        formObject.clear("filestatus");
        formObject.addComboItem("filestatus", "Approved", "Approved");
        formObject.addComboItem("filestatus", "Query Raised", "Query Raised");
        formObject.addComboItem("filestatus", "Exception", "Exception");

        formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);
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
        objAccountsGeneral.getsetServicePoSummary(processInstanceId);
        String proctype = formObject.getNGValue("proctype").replace(",", "%");
        try {
            //if (formObject.getNGValue("filestatus").equalsIgnoreCase("Exception")) {
            //  formObject.setNGValue("nextactivity", "PurchaseUser");
            //} else 
            if (formObject.getNGValue("filestatus").equalsIgnoreCase("Query Raised")) {

                Query = "select ApproverCode from ServicePOApproverMaster where Head = '" + formObject.getNGValue("proctype").replace(",", "%") + "'"
                        + "and State = '" + formObject.getNGValue("state") + "'"
                        + "and ApproverLevel = '0'";
                System.out.println("Query1:" + Query);
                result = formObject.getDataFromDataSource(Query);
                System.out.println("result is" + result);
                if (result.size() > 0) {
                    formObject.setNGValue("assignto", result.get(0).get(0));
                } else {
                    formObject.setNGValue("assignto", "");
                }

                formObject.setNGValue("nextactivity", "Initiator");
                formObject.setNGValue("levelflag", "0");
            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Approved")) {
                String levelflag = formObject.getNGValue("levelflag");
                if (levelflag.equalsIgnoreCase("Maker")) {
                    Query = "select ApproverLevel, ApproverCode from ServicePOApproverMaster where  "
                            + "head = '" + formObject.getNGValue("proctype").replace(",", "%") + "' "
                            + "and state = '" + formObject.getNGValue("state") + "' "
                            + "and approverlevel ='Checker'";
                    System.out.println("Query " + Query);
                    result = formObject.getDataFromDataSource(Query);
                    System.out.println("result" + result);
                    if (result.size() > 0) {
                        formObject.setNGValue("assignto", result.get(0).get(1));
                        formObject.setNGValue("nextactivity", "Accounts");
                        formObject.setNGValue("levelflag", "Checker");
                    } else {
                        formObject.setNGValue("nextactivity", "SchedulerAccount");
                    }
                } else {
                    formObject.setNGValue("nextactivity", "SchedulerAccount");
                }
            } else if (formObject.getNGValue("filestatus").equalsIgnoreCase("Exception")) {
                objGeneral.setException(userName, "Combo3", "Text15");
                formObject.setNGValue("nextactivity", "PurchaseUser");
            }
            formObject.setNGValue("previousactivity", activityName);
            objGeneral.maintainHistory(userName, activityName, formObject.getNGValue("filestatus"), "", formObject.getNGValue("Text15"), "q_transactionhistory");

        } catch (Exception e) {
            e.printStackTrace();
        }
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

//    void setPOLineNumber(String linenumberId) {
//        formObject.clear(linenumberId);
//        Query = "select linenumber from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
//        System.out.println("Query :" + Query);
//        result = formObject.getDataFromDataSource(Query);
//        if (result.size() > 0) {
//            for (List<String> result1 : result) {
//                formObject.addComboItem(linenumberId, result1.get(0), result1.get(0));
//            }
//        }
//    }
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

    void addAssessableAmount(String operator, String linenumber, String itemnumber, BigDecimal assessableamount) {

        String addedAssessable = "";
        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoicedetails");
        int rowCount = ListViewq_invoicedetails.getRowCount();
        System.out.println("Row count : " + rowCount);
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                if (formObject.getNGValue("q_invoicedetails", i, 0).equalsIgnoreCase(linenumber)
                        && formObject.getNGValue("q_invoicedetails", i, 1).equalsIgnoreCase(itemnumber)) {
                    if (operator.equalsIgnoreCase("Add")) {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoicedetails", i, 10)).add(assessableamount).toString();
                    } else {
                        addedAssessable = new BigDecimal(formObject.getNGValue("q_invoicedetails", i, 10)).subtract(assessableamount).toString();
                    }
                    formObject.setNGValue("q_invoicedetails", i, 10, addedAssessable);
                    break;
                }
            }

        }
    }
}
