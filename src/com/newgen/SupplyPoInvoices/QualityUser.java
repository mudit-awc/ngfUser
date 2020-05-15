
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallGateentryService;
import com.newgen.Webservice.PostGRN;
import com.newgen.common.AccountsGeneral;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;
import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import javax.faces.application.FacesMessage;

public class QualityUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    CallGateentryService objGetSetGateEntryData = null;
    PostGRN objPostGRN = null;

    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, returnvalue = null, Query;
    List<List<String>> result;
    String Query1 = null;
    List<List<String>> result1;
    List<List<String>> result2;
    private String webserviceStatus;

    AccountsGeneral objAccountsGeneral = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        objGeneral = new General();
        objGetSetGateEntryData = new CallGateentryService();
        objReadProperty = new ReadProperty();
        objAccountsGeneral = new AccountsGeneral();
//        formObject.setNGValue("processid", processInstanceId);
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "Quality_linenumber":
                        formObject.setNGValue("Q_acceptedquantity", "");
                        formObject.setNGValue("Q_rejectedquantity", "");
                        formObject.setNGValue("Q_acceptedremarks", "");
                        formObject.setNGValue("Q_rejectedremarks", "");
                        formObject.setNGValue("Quality_itemselect", "");
                        String linenoinchange = formObject.getNGValue("Quality_linenumber");
                        Query = "select itemid from cmplx_gateentryline where "
                                + "pinstanceid='" + processInstanceId + "' "
                                + "and linenumber='" + linenoinchange + "'";
                        result = formObject.getDataFromDataSource(Query);
                        System.out.println("Query : " + Query);
                        formObject.setNGValue("Quality_itemselect", result.get(0).get(0));
                        ListView ListViewq_quarantinemanagement = (ListView) formObject.getComponent("q_quarantinemanagement");
                        int RowCountq_quarantinemanagement = ListViewq_quarantinemanagement.getRowCount();
                        for (int m = 0; m < RowCountq_quarantinemanagement; m++) {
                            if (linenoinchange.equalsIgnoreCase(formObject.getNGValue("q_quarantinemanagement", m, 5))) {
                                formObject.setNGValue("Q_acceptedquantity", formObject.getNGValue("q_quarantinemanagement", m, 1));
                                formObject.setNGValue("Q_acceptedremarks", formObject.getNGValue("q_quarantinemanagement", m, 2));
                                formObject.setNGValue("Q_rejectedquantity", formObject.getNGValue("q_quarantinemanagement", m, 3));
                                formObject.setNGValue("Q_rejectedremarks", formObject.getNGValue("q_quarantinemanagement", m, 4));
//                                formObject.setNGValue("Quality_itemselect", formObject.getNGValue("q_quarantinemanagement", m, 1));
                                break;
                            } else {
                                formObject.setNGValue("Q_acceptedquantity", "");
                                formObject.setNGValue("Q_acceptedremarks", "");
                                formObject.setNGValue("Q_rejectedquantity", "");
                                formObject.setNGValue("Q_rejectedremarks", "");
                            }
                        }
                        break;

                    case "Q_acceptedquantity": {
                        String linenumber = formObject.getNGValue("Quality_linenumber");
                        String s_acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String s_rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        float acceptedQty = Float.parseFloat(s_acceptedQty);
                        float GRNqty = 0;

                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                        System.out.println("RowCountq_gateentrylines + " + RowCountq_gateentrylines);
                        for (int j = 0; j < RowCountq_gateentrylines; j++) {
                            System.out.println("inside loooooop1");
                            if (linenumber.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                System.out.println("value same h");
                                GRNqty = Float.parseFloat(formObject.getNGValue("q_gateentrylines", j, 4));
                            }
                        }
                        if (acceptedQty > GRNqty) {
                            System.out.println("inside if");
                            formObject.setNGValue("Q_acceptedquantity", "");
                            throw new ValidatorException(new FacesMessage("Quarantine accepted quantity entered is exceeding the GRN quantity", ""));
                        } else {
                            System.out.println("Inside else");
                            float newrejectedqty = GRNqty - acceptedQty;
                            formObject.setNGValue("Q_rejectedquantity", newrejectedqty);
                        }
                    }
                    break;

                    case "Q_rejectedquantity": {
                        String linenumber = formObject.getNGValue("Quality_linenumber");
                        String s_acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String s_rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        float rejectedQty = Float.parseFloat(s_rejectedQty),
                                GRNqty = 0;
                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                        for (int j = 0; j < RowCountq_gateentrylines; j++) {
                            if (linenumber.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                GRNqty = Float.parseFloat(formObject.getNGValue("q_gateentrylines", j, 4));
                            }
                        }
                        if (rejectedQty > GRNqty) {
                            formObject.setNGValue("Q_rejectedquantity", "");
                            throw new ValidatorException(new FacesMessage("Quarantine rejected quantity entered is exceeding the GRN quantity", ""));
                        } else {
                            float newacceptedqty = GRNqty - rejectedQty;
                            formObject.setNGValue("Q_acceptedquantity", newacceptedqty);
                        }
                    }
                    break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Btn_updateQuaratinedetails":
                        String acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        if ((acceptedQty == null || "".equals(acceptedQty)) && (rejectedQty == null || "".equals(rejectedQty))) {
                            throw new ValidatorException(new FacesMessage("Kindly fill all the Quarantine details", ""));
                        } else {
                            float sumofAccepReject,
                                    GRNqty = 0;
                            int rowExistIndex = 0;
                            boolean rowExist = false;
                            String linenumber = formObject.getNGValue("Quality_linenumber");
                            ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                            int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                            for (int j = 0; j < RowCountq_gateentrylines; j++) {
                                if (linenumber.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                    GRNqty = Float.parseFloat(formObject.getNGValue("q_gateentrylines", j, 4));
                                }
                            }
                            sumofAccepReject = Float.parseFloat(acceptedQty) + Float.parseFloat(rejectedQty);
                            if (GRNqty == sumofAccepReject) {
                                ListView ListViewq_quarantinemanagement = (ListView) formObject.getComponent("q_quarantinemanagement");
                                int RowCountq_quarantinemanagement = ListViewq_quarantinemanagement.getRowCount();
                                if (RowCountq_quarantinemanagement > 0) {
                                    for (int i = 0; i <= RowCountq_quarantinemanagement; i++) {
                                        if (linenumber.equalsIgnoreCase(formObject.getNGValue("q_quarantinemanagement", i, 5))) {
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
                        formObject.RaiseEvent("WFSave");
                        break;
                }
                break;

//            case "TAB_CLICKED":
//                switch (pEvent.getSource().getName()) {
//                    case "Tab2":
//                        System.out.println("inside tab2 click");
//                        switch (pEvent.getSource().getName()) {
//                            case "Sheet1":
//                                System.out.println("inside sheet 1 click");
//                                break;
//                            case "Sheet3":
//                                System.out.println("inside sheet 3 click");
//                                break;
//                        }
//
//                        break;
//                }
//                break;
        }
    }

    @Override
    public void formLoaded(FormEvent arg0) {
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
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");
        formObject.setSelectedSheet("Tab2", 3);
        formObject.setNGValue("qualitystatus", "");
        formObject.setNGValue("qualityremarks", "");
        String itemtypeflag = formObject.getNGValue("itemtypeflag");
        if (itemtypeflag.equalsIgnoreCase("Quarantine")
                || itemtypeflag.equalsIgnoreCase("None")) {
            Query = "select linenumber from cmplx_invoiceline where pinstanceid ='" + processInstanceId + "'";
            result = formObject.getDataFromDataSource(Query);
            System.out.println("Query : " + Query);
            for (int i = 0; i < result.size(); i++) {
                formObject.addComboItem("Quality_linenumber", result.get(i).get(0), result.get(i).get(0));
            }
        }

        if (itemtypeflag.equalsIgnoreCase("PP Bags")) {
            formObject.clear("qualitystatus");
            formObject.addComboItem("qualitystatus", "Accepted", "Accepted");
            formObject.addComboItem("qualitystatus", "Hold", "Hold");
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
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
//        System.out.print("-------------------save form completed---------");
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
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        String sQuery = "";
        String qualitystatus = formObject.getNGValue("qualitystatus");
        String itemtypeflag = formObject.getNGValue("itemtypeflag");

        if (qualitystatus.equalsIgnoreCase("Accepted")) {
            if (itemtypeflag.equalsIgnoreCase("PP Bags")) {
                Query = "select count(*) from PDBDocument where name = 'PPBags' "
                        + "and DocumentIndex in (select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                        + "(select itemindex from ext_supplypoinvoices where processid ='" + processInstanceId + "'))";
                System.out.println("second query : " + Query);
                result = formObject.getDataFromDataSource(Query);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("Kindly attach PPBags Document", ""));
                }
                objGeneral.checkSupplyPoDoAUser("AccountsMaker");
            }

            // code for Quarantine table 
            if ("Quarantine".equalsIgnoreCase(itemtypeflag)
                    || "None".equalsIgnoreCase(itemtypeflag)) {
                Query = "select count(*) from cmplx_gateentryline where pinstanceid ='" + processInstanceId + "'";
                result = formObject.getDataFromDataSource(Query);

                Query1 = "select count(*) from cmplx_quarantinemanagement where pinstanceid ='" + processInstanceId + "'";
                result1 = formObject.getDataFromDataSource(Query1);

                if (result.equals(result1)) {
                    System.out.println("barabar h pehle wala");
                } else {
                    System.out.println("inside else barabar nhi h ");
                    throw new ValidatorException(new FacesMessage("Kindly Fill Quarantine Data", ""));
                }

                if (activityName.equalsIgnoreCase("QualityMaker")) {
                    objGeneral.checkSupplyPoDoAUser("QualityChecker");
                } else if (activityName.equalsIgnoreCase("QualityChecker")) {
                    objGeneral.checkSupplyPoDoAUser("AccountsMaker");
                }
            }
        }
        objGeneral.maintainHistory(
                userName,
                activityName,
                qualitystatus,
                "",
                formObject.getNGValue("qualityremarks"),
                "q_transactionhistory"
        );
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
