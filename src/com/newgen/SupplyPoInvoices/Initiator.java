package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.Webservice.CallGateentryService;
import com.newgen.common.AccountsGeneral;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;
import com.newgen.common.General;
import com.newgen.common.Calculations;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.Date;
import javax.faces.application.FacesMessage;

public class Initiator implements FormListener {
    
    FormReference formObject = null;
    FormConfig formConfig = null;
    General objGeneral = null;
    Calculations objCalculations = null;
    AccountsGeneral objAccountsGeneral = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, returnvalue = null, Query;
    List<List<String>> result;
    
    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
    }
    
    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;
                    
                    case "currency":
                        objCalculations.exronCurrencyChange("currency", "invoiceamount", "newbaseamount", "exchangerate");
                        break;
                    
                    case "multiplegrn":
                        System.out.println("Inside multiple grn");
                        if (formObject.getNGValue("multiplegrn").equalsIgnoreCase("True")) {
                            formObject.setVisible("Pick_MultipleGRNPo", true);
                            formObject.setVisible("btn_fetchpogedetails", false);
                            formObject.setLocked("purchaseorderno", true);
                            formObject.setVisible("Frame11", true);
                            formObject.setVisible("Frame9", false);
                        } else {
                            formObject.setVisible("btn_fetchpogedetails", true);
                            formObject.setVisible("Pick_MultipleGRNPo", false);
                            formObject.setLocked("purchaseorderno", false);
                            formObject.setVisible("Frame11", false);
                            formObject.setVisible("Frame9", true);
                            
                            formObject.setNGValue("purchaseorderdate", "");
                            formObject.setNGValue("suppliercode", "");
                            formObject.setNGValue("suppliername", "");
                            formObject.setNGValue("businessunit", "");
                            formObject.setNGValue("site", "");
                            formObject.setNGValue("state", "");
                            formObject.setNGValue("department", "");
                            formObject.setNGValue("currency", "");
                            formObject.setNGValue("deliveryterm", "");
                            formObject.setNGValue("msmestatus", "");
                            formObject.setNGValue("paymentterm", "");
                            formObject.setNGValue("compositescheme", "");
                            formObject.setNGValue("purchasestatus", "");
                            
                        }
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
                        String purchaseorderno = formObject.getNGValue("purchaseorderno");
                        new CallGateentryService().GetSetGateEntry(
                                purchaseorderno,
                                formObject.getNGValue("invoiceno")
                        );
                        //  String AccessToken = new CallAccessTokenService().getAccessToken();
                        new CallPurchaseOrderService().GetSetPurchaseOrder(
                                "",
                                "Supply",
                                purchaseorderno,
                                "Supply"
                        );
                        break;
                    
                    case "Pick_MultipleGRNPo":
                        Query = "select distinct ext.purchaseorderno from ext_supplypoinvoices ext, WFINSTRUMENTTABLE wf "
                                + "where wf.ProcessInstanceID = ext.processid "
                                + "and wf.ActivityName='HoldMultipleGRN' "
//                                + "and ext.multiplegrn='False' "
                                + "and ext.purchaseorderno is not null";
                        System.out.println("Query: " + Query);
                        objPicklistListenerHandler.openPickList(
                                "purchaseorderno",
                                "Purchase Order Number",
                                "Purchase Order",
                                70,
                                70,
                                Query
                        );
                        break;
                }
                break;
        }
    }
    
    @Override
    public void formLoaded(FormEvent arg0) {
//        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
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

//            System.out.println("ProcessInstanceId===== " + processInstanceId);
//            System.out.println("Activityname=====" + activityName);
//            System.out.println("CabinetName====" + engineName);
//            System.out.println("sessionId====" + sessionId);
//            System.out.println("Username====" + userName);
//            System.out.println("workItemId====" + workItemId);
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }
    
    @Override
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        
        formObject.setNGValue("initiatorstatus", "");
        formObject.setNGValue("initiatorremarks", "");
        formObject.setNGValue("multiplegrn", "False");
        
        formObject.clear("proctype");
        Query = "select HeadName from supplypoheadmaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }        
        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
    }
    
    @Override
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }
    
    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }
    
    @Override
    public void submitFormCompleted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }
    
    @Override
    public void submitFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        
        String initiatorexception = "";
        String initiatorStatus = formObject.getNGValue("initiatorstatus");
        if (initiatorStatus.equalsIgnoreCase("Initiate")) {
            if (formObject.getNGValue("multiplegrn").equalsIgnoreCase("False")) {
                ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                int RowCount_gateentrylines = ListViewq_gateentrylines.getRowCount();
                if (RowCount_gateentrylines == 0) {
                    throw new ValidatorException(new FacesMessage("Kindly fetch the gate entry details", ""));
                }
            }
            objGeneral.checkSupplyPoDoAUser("StoreMaker");
        }
        if (initiatorStatus.equalsIgnoreCase("Exception")) {
            initiatorexception = ": " + formObject.getNGValue("initiatorexception");
        }
        
        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        objGeneral.maintainHistory(
                userName,
                activityName,
                initiatorStatus + initiatorexception,
                "",
                formObject.getNGValue("initiatorremarks"),
                "q_transactionhistory"
        );
        formObject.setNGValue("FilterDoA_ApproverLevel", "StoreMaker");
        formObject.setNGValue("FilterDoA_Department", formObject.getNGValue("department"));
        formObject.setNGValue("FilterDoA_Head", formObject.getNGValue("proctype"));
        formObject.setNGValue("FilterDoA_Site", formObject.getNGValue("site"));
        formObject.setNGValue("FilterDoA_StateName", formObject.getNGValue("state"));
        formObject.setNGValue("previousactivity", activityName);
        
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
