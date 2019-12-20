/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.GetSetGateEntryData;
import com.newgen.Webservice.GetSetPurchaseOrderData;
import com.newgen.Webservice.PostGRN;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;
import com.newgen.common.General;
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
import static javafx.beans.binding.Bindings.and;
import static javafx.beans.binding.Bindings.select;
import java.util.*;
import java.lang.*;
import javax.faces.application.FacesMessage;

public class QualityUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    GetSetPurchaseOrderData objGetSetPurchaseOrderData = null;
    GetSetGateEntryData objGetSetGateEntryData = null;
    PostGRN objPostGRN = null;

    String activityName = null;
    String engineName = null;
    String sessionId = null;
    String folderId = null;
    String FILE = null;
    String serverUrl = null;
    String processInstanceId = null;
    String workItemId = null;
    String userName = null;
    String processDefId = null;
    String Query = null;
    List<List<String>> result;
    List<List<String>> result1;
    List<List<String>> result2;
    private String webserviceStatus;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        objGeneral = new General();
        objGetSetPurchaseOrderData = new GetSetPurchaseOrderData();
        objGetSetGateEntryData = new GetSetGateEntryData();
        objReadProperty = new ReadProperty();
        formObject.setNGValue("processid", processInstanceId);
        IRepeater RepeaterControlFrame5 = formObject.getRepeaterControl("Frame5");
        String GateEntryLineLV = "q_gateentrylines";
        String PoLineLV = "q_polines";
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
                    case "Quality_itemselect":
                        System.out.println("inside Quality_itemselect value changed");
                        String itemidinchange = formObject.getNGValue("Quality_itemselect");
                        String accept = "",
                         reject = "",
                         accepremarks = "",
                         rejectremarks = "";
                        ListView ListViewq_quarantinemanagement = (ListView) formObject.getComponent("q_quarantinemanagement");
                        int RowCountq_quarantinemanagement = ListViewq_quarantinemanagement.getRowCount();
                        System.out.println("RowCount_q_quarantine_OLD " + RowCountq_quarantinemanagement);
                        for (int j = 0; j < RowCountq_quarantinemanagement; j++) {
                            System.out.println("inside change for loop");
                            if (itemidinchange.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                System.out.println("inside change if");
                                accept = formObject.getNGValue("q_quarantinemanagement", j, 1);
                                accepremarks = formObject.getNGValue("q_quarantinemanagement", j, 2);
                                reject = formObject.getNGValue("q_quarantinemanagement", j, 3);
                                rejectremarks = formObject.getNGValue("q_quarantinemanagement", j, 4);
                                formObject.setNGValue("Q_acceptedquantity", accept);
                                formObject.setNGValue("Q_acceptedremarks", accepremarks);
                                formObject.setNGValue("Q_rejectedquantity", reject);
                                formObject.setNGValue("Q_rejectedremarks", rejectremarks);
                                break;
                            }
                            else{
                                formObject.setNGValue("Q_acceptedquantity", "");
                                formObject.setNGValue("Q_acceptedremarks", "");
                                formObject.setNGValue("Q_rejectedquantity", "");
                                formObject.setNGValue("Q_rejectedremarks", "");
                                
                            }
                        }
                        break;
                    case "Q_acceptedquantity": {
                        String itemid = formObject.getNGValue("Quality_itemselect");
                        String s_acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String s_rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        int acceptedQty = Integer.parseInt(s_acceptedQty),
                                GRNqty = 0;
                        /*  if (!"".equalsIgnoreCase(s_acceptedQty)
                         || null != s_acceptedQty) {
                         acceptedQty = Integer.parseInt(s_acceptedQty);
                         }*/
                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                        for (int j = 0; j < RowCountq_gateentrylines; j++) {
                            if (itemid.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                GRNqty = Integer.parseInt(formObject.getNGValue("q_gateentrylines", j, 3));
                            }
                        }
                        if (acceptedQty > GRNqty) {
                            formObject.setNGValue("Q_acceptedquantity", "");
                            throw new ValidatorException(new FacesMessage("Quarantine accepted quantity entered is exceeding the GRN quantity", ""));
                        } else {
                            if ("".equalsIgnoreCase(s_rejectedQty)
                                    || null == s_rejectedQty) {
                                int newrejectedqty = GRNqty - acceptedQty;
                                formObject.setNGValue("Q_rejectedquantity", newrejectedqty);
                            }
                        }
                        break;
                    }

                    case "Q_rejectedquantity": {
                        String itemid = formObject.getNGValue("Quality_itemselect");
                        String s_acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String s_rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        int rejectedQty = Integer.parseInt(s_rejectedQty),
                                GRNqty = 0;
                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                        for (int j = 0; j < RowCountq_gateentrylines; j++) {
                            if (itemid.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                GRNqty = Integer.parseInt(formObject.getNGValue("q_gateentrylines", j, 3));
                            }
                        }
                        if (rejectedQty > GRNqty) {
                            formObject.setNGValue("Q_rejectedquantity", "");
                            throw new ValidatorException(new FacesMessage("Quarantine rejected quantity entered is exceeding the GRN quantity", ""));
                        } else {
                            if ("".equalsIgnoreCase(s_acceptedQty)
                                    || null == s_acceptedQty) {
                                int newacceptedqty = GRNqty - rejectedQty;
                                formObject.setNGValue("Q_acceptedquantity", newacceptedqty);
                            }
                        }
                        break;
                    }
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "Btn_updateQuaratinedetails":
                        String acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        if ((acceptedQty == null || "".equals(acceptedQty)) && (rejectedQty == null || "".equals(rejectedQty))) {
                            throw new ValidatorException(new FacesMessage("Kindly fill all the Quarantine details", ""));
                        } else {
                            int sumofAccepReject,
                                    rowExistIndex = 0,
                                    GRNqty = 0;
                            boolean rowExist = false;
                            String itemid = formObject.getNGValue("Quality_itemselect");
                            ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                            int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                            for (int j = 0; j < RowCountq_gateentrylines; j++) {
                                if (itemid.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                    GRNqty = Integer.parseInt(formObject.getNGValue("q_gateentrylines", j, 3));
                                }
                            }
                            sumofAccepReject = Integer.parseInt(acceptedQty) + Integer.parseInt(rejectedQty);
                            if (GRNqty == sumofAccepReject) {
                                ListView ListViewq_quarantinemanagement = (ListView) formObject.getComponent("q_quarantinemanagement");
                                int RowCountq_quarantinemanagement = ListViewq_quarantinemanagement.getRowCount();
                                if (RowCountq_quarantinemanagement > 0) {
                                    for (int i = 0; i <= RowCountq_quarantinemanagement; i++) {
                                        if (itemid.equalsIgnoreCase(formObject.getNGValue("q_quarantinemanagement", i, 0))) {
                                            rowExist = true;
                                            rowExistIndex = i;
                                            break;
                                        }
                                    }
                                }

                                if (rowExist) {
                                    ListViewq_quarantinemanagement.setSelectedRowIndex(rowExistIndex);
                                    formObject.ExecuteExternalCommand("NGModifyRow", "q_quarantinemanagement");
                                } else {
                                    formObject.ExecuteExternalCommand("NGAddRow", "q_quarantinemanagement");
                                }
                            } else {
                                throw new ValidatorException(new FacesMessage("Sum of Accepted Qty & Rejected Qty is exceeding by GRN Qty", ""));
                            }
                        }
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
    public void formLoaded(FormEvent arg0) {
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        formObject.setSelectedSheet("Tab2", 2);

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
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");
        formObject.setSheetEnable("Tab2", 0, false);
        formObject.setSheetEnable("Tab2", 1, false);
        System.out.println("farman");
        if (formObject.getNGValue("itemtypeflag").equalsIgnoreCase("Quarantine")) {
            System.out.println("farman2");
            String Query = "select itemnumber,quarantinemanagement from cmplx_poline where "
                    + "pinstanceid ='" + processInstanceId + "' and "
                    + "itemnumber in (select itemid from cmplx_gateentryline where pinstanceid ='" + processInstanceId + "')";
            result = formObject.getDataFromDataSource(Query);

            for (int i = 0; i < result.size(); i++) {
                String quarantine = result.get(0).get(1).toString();

                if ("1".equalsIgnoreCase(quarantine)) {
                    formObject.addComboItem("Quality_itemselect", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                }
            }
        } else {
            System.out.println("false kerdiya");
            formObject.setVisible("Frame7", false);
        }
        //---------- code for qatestresultmapping
        if (formObject.getNGValue("itemtypeflag").equalsIgnoreCase("Raw Material")) {
            System.out.println("inside qatestresultmapping ");
            formObject.setVisible("Frame6", false);
            formObject.setVisible("Frame7", true);

        }

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

        System.out.println("******activityName****" + activityName);
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
