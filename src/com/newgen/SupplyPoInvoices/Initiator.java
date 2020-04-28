/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallAccessTokenService;
import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.Webservice.CallGateentryService;
import com.newgen.Webservice.PostGRN;
import com.newgen.common.AccountsGeneral;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;

import com.newgen.common.General;
import com.newgen.common.Calculations;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.common.ReadProperty;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.IRepeater;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;

public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    Calculations objCalculations = null;
    ReadProperty objReadProperty = null;
    CallGateentryService objGetSetGateEntryData = null;
    PostGRN objPostGRN = null;
    AccountsGeneral objAccountsGeneral = null;
    PicklistListenerHandler objPicklistListenerHandler = null;

    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, returnvalue = null, Query;

    List<List<String>> result;
    private String webserviceStatus;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        String PurchaseOrderLineLV = "q_polines";
        String InvoiceLineLV = "q_invoiceline";

        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;
                        
                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount", "exchangerate");
                        break;

                    case "invoiceamount":
                    case "exchangerate":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "invoiceamount", "newbaseamount", "exchangerate");
                        break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "btn_fetchpogedetails":
                        String gelinenumber = "";
                        boolean rowexist1 = false;
                        System.out.println("inside btn_fetchpogedetails");
                        String purchaseorderno = formObject.getNGValue("purchaseorderno");
                        String invoiceno = formObject.getNGValue("invoiceno");

                        new CallGateentryService().GetSetGateEntry(purchaseorderno, invoiceno);
                        //  String AccessToken = new CallAccessTokenService().getAccessToken();
                        new CallPurchaseOrderService().GetSetPurchaseOrder("", "Supply", purchaseorderno, "Supply");
                        break;

                    case "btn_addtoinvoice":
                        /*    boolean rowexist = false;
                        System.out.println("inside btn_addtoinvoice");

                        ListView ListViewq_polines = (ListView) formObject.getComponent("q_polines");
                        System.out.println("2 " + ListViewq_polines);
                        int selectrow1 = ListViewq_polines.getSelectedRowIndex();
                        System.out.println("selectrow : " + selectrow1);
                        String SelectedItemId = formObject.getNGValue(PurchaseOrderLineLV, selectrow1, 1);
                        System.out.println("SelectedItemId : " + SelectedItemId);
                        ListView ListViewq_invoiceline = (ListView) formObject.getComponent(InvoiceLineLV);
                        int RowCountq_invoiceline = ListViewq_invoiceline.getRowCount();
                        System.out.println("RowCountq_invoiceline :" + RowCountq_invoiceline);
                        for (int j = 0; j < RowCountq_invoiceline; j++) {
                            if (SelectedItemId.equalsIgnoreCase(formObject.getNGValue(InvoiceLineLV, j, 1))) {
                                rowexist = true;
                                throw new ValidatorException(new FacesMessage("Item already added", ""));
                            }
                        }
                        System.out.println("rowexist : " + rowexist);
                        if (rowexist == false) {
                            System.out.println("inside ROW NOT EXIST");
                            String rate = formObject.getNGValue("q_polines_Rate");
                            String taxGroup = formObject.getNGValue("q_polines_ItemTaxGroup");
                            Query = "select itemname,grnqty from cmplx_gateentryline where "
                                    + "pinstanceid =  '" + processInstanceId + "' and itemid = '" + SelectedItemId + "'";
                            System.out.println("Query : " + Query);
                            result = formObject.getDataFromDataSource(Query);
                            System.out.println("result :: " + result);
                            if (result.size() > 0) {
                                returnvalue = objCalculation.calculateLineTotalWithTax(result.get(0).get(1), rate, taxGroup);
                                String[] tax1 = returnvalue.split("/");
                                String TotalTaxAmount = tax1[0];
                                String LineTotal = tax1[1];
                                String TaxAmount = tax1[2];
                                System.out.println("totalTaxAmount == " + returnvalue);
                                System.out.println("TotalTaxAmount == " + TotalTaxAmount);
                                System.out.println("LineTotal == " + LineTotal);
                                String invoicelineXML = "";
                                invoicelineXML = (new StringBuilder()).append(invoicelineXML).
                                        append("<ListItem><SubItem>").append(RowCountq_invoiceline + 1).
                                        append("</SubItem><SubItem>").append(SelectedItemId).
                                        append("</SubItem><SubItem>").append(result.get(0).get(0)).
                                        append("</SubItem><SubItem>").append(result.get(0).get(1)).
                                        append("</SubItem><SubItem>").append(rate).
                                        append("</SubItem><SubItem>").append(LineTotal).
                                        append("</SubItem><SubItem>").append(taxGroup).
                                        append("</SubItem><SubItem>").append(TaxAmount).
                                        append("</SubItem><SubItem>").append(TotalTaxAmount).
                                        append("</SubItem><SubItem>").append("").
                                        append("</SubItem><SubItem>").append("").
                                        append("</SubItem></ListItem>").toString();

                                System.out.println("invoicelineXML : " + invoicelineXML);
                                System.out.println("invoicelineXML :" + invoicelineXML);
                                formObject.NGAddListItem("q_invoiceline", invoicelineXML);

                            } else {
                                throw new ValidatorException(new FacesMessage("The Gate entry has not been performed against the selected line ", ""));
                            }
                        } */
                        break;

                    case "Pick_chargescode":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    //Query = "";
                    //objPicklistListenerHandler.openPickList("journalname", "Code,Description", "Journal Master", 70, 70, Query);
                    //break;

                    case "Pick_category":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    //  break;

                    case "Pick_min":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    // break;

                    case "Pick_max":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    // break;

                    case "Pick_companylocation":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    //  break;

                    case "Pick_hsnsacvalue":
                        String hsnsaccodetype = formObject.getNGValue("hsnsactype");
                        if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
                            Query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                            objPicklistListenerHandler.openPickList("hsnsacvalue", "Code,Description", "HSN Master", 70, 70, Query);
                        } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
                            Query = "select SACCode,Description from SACMaster order by SACCode asc";
                            objPicklistListenerHandler.openPickList("hsnsacvalue", "Code,Description", "SAC Master", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the type value"));
                        }
                        break;

                    case "Pick_vendorlocation":
                        throw new ValidatorException(new FacesMessage("Master Not Found"));
                    // break;

                    case "Pick_tdsgroup":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("tdsgroup", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;
                }

                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }

                break;
        }
    }

    @Override
    public void formLoaded(FormEvent arg0
    ) {
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {

            activityName = formObject.getWFActivityName();
            engineName = formConfig.getConfigElement("EngineName");
            sessionId = formConfig.getConfigElement("DMSSessionId");
            folderId = formConfig.getConfigElement("FolderId");
            serverUrl = formConfig.getConfigElement("ServletPath");
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
    public void formPopulated(FormEvent arg0
    ) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");
        formObject.setNGValue("initiatorstatus", null);
        formObject.setNGValue("initiatorremarks", null);
        
        formObject.setNGValue("multiplegrn", "False");

    }

    @Override
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

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
        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
        int RowCount_gateentrylines = ListViewq_gateentrylines.getRowCount();
        if (RowCount_gateentrylines == 0) {
            throw new ValidatorException(new FacesMessage("Kindly fetch the gate entry details", ""));
        }

        String username = formObject.getUserName();
        String initiatorStatus = formObject.getNGValue("initiatorstatus");
        String initiatorexception = "";
        if (initiatorStatus.equalsIgnoreCase("Exception")) {
            initiatorexception = formObject.getNGValue("initiatorexception");
        }

        ListView ListViewq_history = (ListView) formObject.getComponent("q_transactionhistory");
        int RowCountq_history = ListViewq_history.getRowCount();
        System.out.println("RowCountq_history : " + RowCountq_history);
        String initiatorRemarks = formObject.getNGValue("initiatorremarks");
        objGeneral.maintainHistory(username, activityName, initiatorStatus, initiatorexception, initiatorRemarks, "q_transactionhistory");

        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        formObject.setNGValue("previousactivity", activityName);
        System.out.println("Previous Activity :" + formObject.getNGValue("previousactivity"));
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String encrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String decrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
