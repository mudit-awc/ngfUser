/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.ServicePoInvoice;

import com.newgen.Webservice.CallCLMSService;
import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.common.AccountsGeneral;
import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.common.PicklistListenerHandler;
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
import java.util.ArrayList;
import java.util.Date;

public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    CallCLMSService objGetSetCLMSS = null;
    AccountsGeneral objAccountsGeneral = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;

                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        break;

//                    case "suppliercode":
//                        Query = "select gta from VendorMaster where VendorCode = '" + formObject.getNGValue("suppliercode") + "'";
//                        result = formObject.getDataFromDataSource("Query");
//                        if (result.size() > 0) {
//                            if (result.get(0).get(0).equalsIgnoreCase("0")) {
//                                formObject.setNGValue("gtavendor", "false");
//                            } else {
//                                formObject.setNGValue("gtavendor", "true");
//                            }
//                        }
//                        break;
                    case "invoiceamount":
                    case "exchangerateotherthaninr":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "invoiceamount", "newbaseamount", "exchangerateotherthaninr");
                        break;

                    case "filestatus":
                        String filestatus = formObject.getNGValue("filestatus");
                        if (filestatus.equalsIgnoreCase("Exception")) {
                            formObject.setVisible("Label66", true);
                            formObject.setVisible("Combo3", true);

                        }
                        break;

                    case "newbaseamount":
                        formObject.setNGValue("adjustedamountorigin", formObject.getNGValue("newbaseamount"));
                        break;

                    case "adjustedamountorigin":
                        String q_ledgeradjustedoriginamount = formObject.getNGValue("adjustedamountorigin");
                        String q_ledgertdspercent = formObject.getNGValue("tdspercent");
                        String calculatedValue = objCalculations.calculatePercentAmount(q_ledgeradjustedoriginamount, q_ledgertdspercent);
                        formObject.setNGValue("tdsamount", calculatedValue);
                        formObject.setNGValue("adjustedtdsamountorigin", calculatedValue);
                        break;

                    case "qpo_remainingqty":
                        BigDecimal remainingqty = new BigDecimal(formObject.getNGValue("qpo_remainingqty"));
                        System.out.println("Rqty: " + remainingqty);
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
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {

                    case "Btn_DeletePO":
                        try {
                            ListView LVq_multiplepo = (ListView) formObject.getComponent("q_multiplepo");
                            int selectedrowq_multiplepo = LVq_multiplepo.getSelectedRowIndex();
                            String multiposelected = formObject.getNGValue("q_multiplepo", selectedrowq_multiplepo, 0);
                            int RCq_polinedetails = formObject.getLVWRowCount("q_polinedetails");
                            ArrayList<Integer> q_polinedetailsindexesarr = new ArrayList<Integer>();

                            for (int i = 0; i <= RCq_polinedetails; i++) {
                                if (multiposelected.equalsIgnoreCase(formObject.getNGValue("q_polinedetails", i, 72))) {
                                    System.out.println(i + " Matched");
                                    q_polinedetailsindexesarr.add(i);
                                }
                            }

                            for (int m = 0; m < q_polinedetailsindexesarr.size(); m++) {
                                System.out.println("Loop :" + m);
                                System.out.println("Value :" + q_polinedetailsindexesarr.get(m));
                            }

                            int q_polinedetailsindexes[] = new int[q_polinedetailsindexesarr.size()];

                            System.out.println("Array size: " + q_polinedetailsindexesarr.size());
                            System.out.println("Int Array Length: " + q_polinedetailsindexes.length);
                            for (int k = 0; k < q_polinedetailsindexesarr.size(); k++) {
                                System.out.println("Loop count: " + k);
                                System.out.println("Array value :" + q_polinedetailsindexesarr.get(k));
                                q_polinedetailsindexes[k] = q_polinedetailsindexesarr.get(k);
                            }

                            int RCq_linechargesdetails = formObject.getLVWRowCount("q_linechargesdetails");
                            ArrayList<Integer> q_linechargesdetailsindexesarr = new ArrayList<Integer>();

                            for (int j = 0; j <= RCq_linechargesdetails; j++) {
                                if (multiposelected.equalsIgnoreCase(formObject.getNGValue("q_linechargesdetails", j, 9))) {
                                    q_linechargesdetailsindexesarr.add(j);
                                }
                            }
                            int q_linechargesdetailsindexes[] = new int[q_linechargesdetailsindexesarr.size()];

                            for (int l = 0; l < q_linechargesdetailsindexesarr.size(); l++) {
                                q_linechargesdetailsindexes[l] = q_linechargesdetailsindexesarr.get(l);
                            }

                            formObject.setSelectedIndices("q_polinedetails", q_polinedetailsindexes);
                            formObject.removeSelectedRows("q_polinedetails");

                            formObject.setSelectedIndices("q_linechargesdetails", q_linechargesdetailsindexes);
                            formObject.removeSelectedRows("q_linechargesdetails");

                            formObject.ExecuteExternalCommand("NGDeleteRow", "q_multiplepo");
                        } catch (Exception e) {
                            System.out.println("Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;

                    case "Btn_Delete_Invoice":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_invoicedetails");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_fetchpodetails":
                        boolean rowexists = false;
                        String ponumber = formObject.getNGValue("ponumber");
                        int rowcount = formObject.getLVWRowCount("q_multiplepo");
                        String listvalue = "";
                        for (int j = 0; j <= rowcount; j++) {
                            listvalue = formObject.getNGValue("q_multiplepo", j, 0);
                            if (ponumber.equalsIgnoreCase(listvalue)) {
                                rowexists = true;
                                break;
                            }
                        }
                        if (rowexists) {
                            throw new ValidatorException(new FacesMessage("This PO Number has been already added"));
                        } else {
                            System.out.println("inside else");
//                            String AccessToken = new CallAccessTokenService().getAccessToken();
                            new CallPurchaseOrderService().GetSetPurchaseOrder("", "Service", formObject.getNGValue("ponumber"), "Service");
                            formObject.setNGValue("PurchaseOrderNo", formObject.getNGValue("ponumber"));
                            formObject.setNGValue("VendorName", formObject.getNGValue("suppliername"));
                            formObject.setNGValue("VendorCode", formObject.getNGValue("suppliercode"));
                            if (formObject.getNGValue("proctype").equalsIgnoreCase("CLMS")) {
                                new CallCLMSService().GetSetCLMSS(formObject.getNGValue("ponumber"), formObject.getNGValue("invoicenumber"));
                            }
                            formObject.setNGValue("ponumber", "");
                        }
                        // formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails_2");
                        break;

                    case "Btn_addtoinvoice":
                        boolean rowexist = false;
                        ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_invoicedetails");
                        int rowCount = formObject.getLVWRowCount("q_invoicedetails");
                        System.out.println("Row count 123123: " + rowCount);
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
                            formObject.RaiseEvent("WFSave");
                        }
                        break;
                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
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
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        System.out.println("form Loaded called : 20/05/2019");
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
        System.out.println("----------------------Intiation Workstep Loaded from form populated........---------------------------");
        formObject.clear("filestatus");
        objGeneral = new General();
//        formObject.setEnabled("state", true);

        if (!formObject.getNGValue("previousactivity").equalsIgnoreCase("Approver")
                && !formObject.getNGValue("previousactivity").equalsIgnoreCase("Accounts")) {
            formObject.addComboItem("filestatus", "Initiate", "Initiate");
            formObject.addComboItem("filestatus", "Discard", "Discard");
            formObject.addComboItem("filestatus", "Exception", "Exception");

        } else {
            formObject.addComboItem("filestatus", "Hold", "Hold");
            formObject.addComboItem("filestatus", "Query Cleared", "Query Cleared");
            formObject.addComboItem("filestatus", "Discard", "Discard");
            formObject.addComboItem("filestatus", "Exception", "Exception");
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

//        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));

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
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        String levelflag = formObject.getNGValue("levelflag");
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        objGeneral.checkDuplicateInvoice(
                formObject.getNGValue("suppliercode"),
                formObject.getNGValue("invoicenumber"),
                formObject.getNGValue("fiscalyear"),
                processInstanceId
        );

        objGeneral.checkServicePoDoAUser(levelflag, "");
        formObject.setNGValue("FilterDoA_Department", formObject.getNGValue("department"));
        formObject.setNGValue("FilterDoA_Head", formObject.getNGValue("proctype"));
        formObject.setNGValue("FilterDoA_Site", formObject.getNGValue("site"));
        formObject.setNGValue("FilterDoA_StateName", formObject.getNGValue("state"));
        formObject.setNGValue("previousactivity", activityName);
        objAccountsGeneral.setFinancialDimension("q_financialdimension", processInstanceId);
        objGeneral.maintainHistory(
                userName,
                activityName,
                formObject.getNGValue("filestatus"),
                "",
                formObject.getNGValue("Text15"),
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
}
