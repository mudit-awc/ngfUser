
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class PurchaseUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, returnvalue = null, Query;
    General objGeneral = null;
    List<List<String>> result = null;

    @Override
    public void formLoaded(FormEvent fe) {
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
    public void formPopulated(FormEvent fe) {
//        System.out.println("inside purchase user form populated");
        formObject = FormContext.getCurrentInstance().getFormReference();

        formObject.setNGValue("purchasestatus", "");
        formObject.setNGValue("purchaseremarks", "");
        formObject.setNGValue("previousstatus", "");
        formObject.setNGValue("returnpo", "");

        String previousactivity = formObject.getNGValue("previousactivity");
        if (previousactivity.equalsIgnoreCase("Initiator")
                || previousactivity.equalsIgnoreCase("StoreMaker")
                || previousactivity.equalsIgnoreCase("StoreChecker")
                || previousactivity.equalsIgnoreCase("AccountsMaker")
                || previousactivity.equalsIgnoreCase("AccountsChecker")) {
            formObject.addComboItem("purchasestatus", "Hold", "Hold");
            formObject.addComboItem("purchasestatus", "Exception Cleared", "Exception Cleared");
        }

        if (previousactivity.equalsIgnoreCase("QualityMaker")
                || previousactivity.equalsIgnoreCase("QualityChecker")) {
            formObject.addComboItem("purchasestatus", "Hold", "Hold");
            formObject.addComboItem("purchasestatus", "Replacement or Exchange", "Replacement or Exchange");
            formObject.addComboItem("purchasestatus", "Purchase Return", "Purchase Return");
        }

        formObject.clear("proctype");
        Query = "select HeadName from supplypoheadmaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }
    }

    @Override
    public void saveFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    @Override
    public void saveFormCompleted(FormEvent fe) throws ValidatorException {
//        System.out.print("-------------------save form completed---------");
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    @Override
    public void submitFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        String purchaseStatus = formObject.getNGValue("purchasestatus");

        Query = "select count(*) from PDBDocument where DocumentIndex in "
                + "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                + "(select itemindex from ext_supplypoinvoices where processid ='" + processInstanceId + "'))"
                + "and ";
        if (purchaseStatus.equalsIgnoreCase("Replacement or Exchange")) {
            Query = Query + "Name = 'RGP'";
            System.out.println("second query : " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                throw new ValidatorException(new FacesMessage("Kindly attach RGP Document", ""));
            }
        } else if (purchaseStatus.equalsIgnoreCase("Purchase Return")) {
            Query = Query + "Name = 'NRGP'";
            System.out.println("second query : " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("Result : " + result.get(0).get(0));
            if (result.get(0).get(0).equalsIgnoreCase("0")) {
                throw new ValidatorException(new FacesMessage("Kindly attach NRGP Document", ""));
            }
        }

        objGeneral.maintainHistory(
                userName,
                activityName,
                purchaseStatus,
                "",
                formObject.getNGValue("purchaseremarks"),
                "q_transactionhistory"
        );

        formObject.setNGValue("nextactivity", formObject.getNGValue("previousactivity"));
        formObject.setNGValue("previousactivity", activityName);
        formObject.setNGValue("previousstatus", purchaseStatus);
    }

    @Override
    public void submitFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "":
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

}
