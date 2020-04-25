
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.common.AccountsGeneral;
import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Admin
 */
public class PurchaseUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null;
    String Query = null;
    General objGeneral = null;
    List<List<String>> result = null;

    @Override
    public void formLoaded(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {

            activityName = formObject.getWFActivityName();
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent fe) {
        System.out.println("inside purchase user form populated");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        formObject.setNGValue("purchasestatus", null);
        formObject.setNGValue("purchaseremarks", null);
        formObject.setNGValue("previousstatus", null);
        formObject.setNGValue("returnpo", null);
    }

    @Override
    public void saveFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
    }

    @Override
    public void saveFormCompleted(FormEvent fe) throws ValidatorException {
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void submitFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
//        objAccountsGeneral = new AccountsGeneral()
        // Code for Mandatory document started
        String prevActivity = formObject.getNGValue("previousactivity");
        String storeStatus = formObject.getNGValue("storestatus");
        String purchaseStatus = formObject.getNGValue("purchasestatus");
        int RGPCounter = 0, NRGPCounter = 0;

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
            if (result.get(i).get(0).equalsIgnoreCase("RGP")) {
                System.out.println("RGP counter m aagya");
                RGPCounter++;
            } else if (result.get(i).get(0).equalsIgnoreCase("NRGP")) {
                System.out.println("NRGP counter m aagya");
                NRGPCounter++;
            } else {

            }
        }
        System.out.println("activityName :" + activityName);
        System.out.println("prevActivity :" + prevActivity);
        System.out.println("purchaseStatus A:" + purchaseStatus);

        if (activityName.equalsIgnoreCase("PurchaseUser") && (prevActivity.equalsIgnoreCase("QualityUser") || prevActivity.equalsIgnoreCase("StoreUser")) && purchaseStatus.equalsIgnoreCase("Replacement/Exchange")) {
            System.out.println("Replacement/Exchange m aagya EQUAL WALE");
            formObject.setNGValue("purchasestatuschecker", "Replacement/Exchange");
            if (RGPCounter <= 0) {
                System.out.println("exception m aagyaEQUAL WALE");
                throw new ValidatorException(new FacesMessage("Kindly attach RGP Document", ""));
            }
        }

        if (activityName.equalsIgnoreCase("PurchaseUser") && (prevActivity.equalsIgnoreCase("QualityUser") || prevActivity.equalsIgnoreCase("StoreUser")) && purchaseStatus.equalsIgnoreCase("Purchase Return")) {
            System.out.println("Replacement/Exchange m aagya ");
            formObject.setNGValue("purchasestatuschecker", "Purchase Return");
            if (NRGPCounter <= 0) {
                System.out.println("exception m aagya 2");
                throw new ValidatorException(new FacesMessage("Kindly attach NRGP Document", ""));
            }
        }
        // Code for Mandatory document Ended here    

        String username = formObject.getUserName();
        // String purchaseuserStatus2 = formObject.getNGValue("purchasestatus");
        String returnpo = "";
        if (purchaseStatus.equalsIgnoreCase("Purchase Return")) {
            returnpo = formObject.getNGValue("returnpo");
        }
        System.out.println("returnpo : " + returnpo);

        ListView ListViewq_history = (ListView) formObject.getComponent("q_transactionhistory");
        int RowCountq_history = ListViewq_history.getRowCount();
        System.out.println("RowCountq_history : " + RowCountq_history);
        String purchaseRemarks = formObject.getNGValue("purchaseremarks");
//        String historylistview = "<ListItem>"
//                + "<SubItem>" + username + "</SubItem>"
//                + "<SubItem>" + activityName + "</SubItem>"
//                + "<SubItem>" + currentdate + "</SubItem>"
//                + "<SubItem>" + currenttime + "</SubItem>"
//                + "<SubItem>" + purchaseStatus + "</SubItem>"
//                + "<SubItem>" + returnpo + "</SubItem>"
//                + "<SubItem>" + purchaseRemarks + "</SubItem>"
//                + "</ListItem>";
//        formObject.NGAddListItem("q_history", historylistview);
        objGeneral.maintainHistory(username, activityName, purchaseStatus, returnpo, purchaseRemarks, "q_transactionhistory");

        formObject.setNGValue("previousactivity", activityName);
        System.out.println("Previous Activity :" + formObject.getNGValue("previousactivity"));
        System.out.println("purchase status in submit started " + purchaseStatus);
        formObject.setNGValue("previousstatus", purchaseStatus);
    }

    @Override
    public void submitFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        String chaman = formObject.getNGValue("purchasestatus");
        System.out.println("purchase status in submit completed " + chaman);
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {

                    case "purchasestatus":
                        System.out.println("inside value change");
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
