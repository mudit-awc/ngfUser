
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
import com.newgen.omniforms.component.IRepeater;
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
    String Query1 = null;
    List<List<String>> result;
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
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        objGeneral = new General();
        objGetSetGateEntryData = new CallGateentryService();
        objReadProperty = new ReadProperty();
        objAccountsGeneral = new AccountsGeneral();
        formObject.setNGValue("processid", processInstanceId);
        IRepeater RepeaterControlFrame5 = formObject.getRepeaterControl("Frame5");
        String GateEntryLineLV = "q_gateentrylines";
        String PoLineLV = "q_polines";
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
                        String accept = "",
                         reject = "",
                         accepremarks = "",
                         rejectremarks = "";

                        Query = "select itemid from cmplx_gateentryline where "
                                + "pinstanceid='" + processInstanceId + "' "
                                + "and linenumber='" + linenoinchange + "'";
                        result = formObject.getDataFromDataSource(Query);
                        System.out.println("Query : " + Query);
                        formObject.setNGValue("Quality_itemselect", result.get(0).get(0));

                        ListView ListViewq_quarantinemanagement = (ListView) formObject.getComponent("q_quarantinemanagement");
                        int RowCountq_quarantinemanagement = ListViewq_quarantinemanagement.getRowCount();
                        System.out.println("RowCount_q_quarantine_OLD : " + RowCountq_quarantinemanagement);
                        for (int m = 0; m < RowCountq_quarantinemanagement; m++) {
                            System.out.println("inside change for loop > " + m);
                            System.out.println("linenoinchange : " + linenoinchange);

                            if (linenoinchange.equalsIgnoreCase(formObject.getNGValue("q_quarantinemanagement", m, 5))) {
                                System.out.println("inside change if");
                                accept = formObject.getNGValue("q_quarantinemanagement", m, 1);
                                accepremarks = formObject.getNGValue("q_quarantinemanagement", m, 2);
                                reject = formObject.getNGValue("q_quarantinemanagement", m, 3);
                                rejectremarks = formObject.getNGValue("q_quarantinemanagement", m, 4);
                                System.out.println("accept : " + accept);
                                System.out.println("accepremarks : " + accepremarks);
                                System.out.println("reject : " + reject);
                                System.out.println("rejectremarks : " + rejectremarks);

                                formObject.setNGValue("Q_acceptedquantity", accept);
                                formObject.setNGValue("Q_acceptedremarks", accepremarks);
                                formObject.setNGValue("Q_rejectedquantity", reject);
                                formObject.setNGValue("Q_rejectedremarks", rejectremarks);
//                                formObject.setNGValue("Quality_itemselect", formObject.getNGValue("q_quarantinemanagement", m, 1));
                                break;
                            } else {
                                System.out.println("else m h");
                                formObject.setNGValue("Q_acceptedquantity", "");
                                formObject.setNGValue("Q_acceptedremarks", "");
                                formObject.setNGValue("Q_rejectedquantity", "");
                                formObject.setNGValue("Q_rejectedremarks", "");
                            }
                            System.out.println("going to again loop");
                        }
                        break;
                    case "Q_acceptedquantity": {
                        String linenumber = formObject.getNGValue("Quality_linenumber");
                        String s_acceptedQty = formObject.getNGValue("Q_acceptedquantity");
                        String s_rejectedQty = formObject.getNGValue("Q_rejectedquantity");
                        float acceptedQty = Float.parseFloat(s_acceptedQty);
                        float GRNqty = 0;
                        System.out.println("acceptedQty + " + acceptedQty);
                        System.out.println("s_rejectedQty + " + s_rejectedQty);

                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                        System.out.println("RowCountq_gateentrylines + " + RowCountq_gateentrylines);
                        for (int j = 0; j < RowCountq_gateentrylines; j++) {
                            System.out.println("inside loooooop1");
                            System.out.println("formObject.getNGValue(\"q_gateentrylines\", j, 1) " + formObject.getNGValue("q_gateentrylines", j, 1));
                            if (linenumber.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", j, 0))) {
                                System.out.println("value same h");
                                System.out.println("GRNqty + " + formObject.getNGValue("q_gateentrylines", j, 4));
                                GRNqty = Float.parseFloat(formObject.getNGValue("q_gateentrylines", j, 4));
                                System.out.println("GRNqty + " + GRNqty);
                            }
                        }
                        if (acceptedQty > GRNqty) {
                            System.out.println("inside if ++++");
                            formObject.setNGValue("Q_acceptedquantity", "");
                            throw new ValidatorException(new FacesMessage("Quarantine accepted quantity entered is exceeding the GRN quantity", ""));
                        } else {
                            System.out.println("else m aagya");
                            //    if ("".equalsIgnoreCase(s_rejectedQty)
                            //           || null == s_rejectedQty) {
                            float newrejectedqty = GRNqty - acceptedQty;
                            formObject.setNGValue("Q_rejectedquantity", newrejectedqty);
                            //    }
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
                            //        if ("".equalsIgnoreCase(s_acceptedQty)
                            //               || null == s_acceptedQty) {
                            float newacceptedqty = GRNqty - rejectedQty;
                            formObject.setNGValue("Q_acceptedquantity", newacceptedqty);
                            //      }
                        }

                    }
                    break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "Btn_Resolve":
                        System.out.println("inside btn resolve");
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
                                            System.out.println("row exist");
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
            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab2":
                        System.out.println("inside tab2 click");
                        switch (pEvent.getSource().getName()) {
                            case "Sheet1":
                                System.out.println("inside sheet 1 click");
                                break;
                            case "Sheet3":
                                System.out.println("inside sheet 3 click");
                                break;
                        }

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
        formObject.setEnabled("Frame2", false);
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
        formObject.setNGValue("qualitystatus", null);
        formObject.setNGValue("qualityremarks", null);
        formObject.setSheetEnable("Tab2", 0, false);
        formObject.setSheetEnable("Tab2", 2, false);
        formObject.setSelectedSheet("Tab2", 3);
        System.out.println("farman");
        if (formObject.getNGValue("itemtypeflag").equalsIgnoreCase("Quarantine")) {
            System.out.println("farman2");
            String Query = "select linenumber,quarantinemanagement from cmplx_poline where "
                    + "pinstanceid ='" + processInstanceId + "' and "
                    + "linenumber in (select linenumber from cmplx_gateentryline where pinstanceid ='" + processInstanceId + "')";
            result = formObject.getDataFromDataSource(Query);
            System.out.println("Query : " + Query);
            System.out.println("result : " + result);

            for (int i = 0; i < result.size(); i++) {
                String quarantine = result.get(0).get(1).toString();
                System.out.println("quarantine : " + quarantine);

                if ("1".equalsIgnoreCase(quarantine)) {
                    System.out.println("inside if");
                    formObject.addComboItem("Quality_linenumber", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                }
            }
        } else {
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
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        int PPBagsCounter = 0;
        String prevActivity = formObject.getNGValue("previousactivity");
        String qualitystatus = formObject.getNGValue("qualitystatus");
        String itemtypeflag = formObject.getNGValue("itemtypeflag");
        String qualityStatus = formObject.getNGValue("qualitystatus");
        String qualityexception = "";
        if (qualityStatus.equalsIgnoreCase("Exception")) {
            qualityexception = formObject.getNGValue("qualityexception");
        }
        ListView ListViewq_history = (ListView) formObject.getComponent("q_transactionhistory");
        int RowCountq_history = ListViewq_history.getRowCount();
        System.out.println("RowCountq_history : " + RowCountq_history);
        String qualityRemarks = formObject.getNGValue("qualityremarks");
        objGeneral.maintainHistory(userName, activityName, qualityStatus, qualityexception, qualityRemarks, "q_transactionhistory");
        Query = "select Name from PDBDocument where DocumentIndex in \n"
                + "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                + "(select itemindex from ext_supplypoinvoices where processid ='" + processInstanceId + "'))";
        result = formObject.getDataFromDataSource(Query);
        System.out.println("second query : " + Query);
        System.out.println("second result : " + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside query loop");
            if (result.get(i).get(0).equalsIgnoreCase("PPBags")) {
                System.out.println("PPBags counter m aagya");
                PPBagsCounter++;
            }
        }
        // qualitystatus = Accepted    itemtypeflag = PP Bags
        if (qualitystatus.equalsIgnoreCase("Accepted") && itemtypeflag.equalsIgnoreCase("PP Bags")) {
            System.out.println("Quality PPBags m aagya ");
            if (PPBagsCounter <= 0) {
                System.out.println("exception m aagya PPBags");
                throw new ValidatorException(new FacesMessage("Kindly attach PPBags Document", ""));
            }
        }
// code for Quarantine table 
        Query = "select count(*) from cmplx_gateentryline where pinstanceid ='" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);

        Query1 = "select count(*) from cmplx_quarantinemanagement where pinstanceid ='" + processInstanceId + "'";
        result1 = formObject.getDataFromDataSource(Query1);
        System.out.println("flag value : " + formObject.getNGValue("itemtypeflag").toString());
        if ("Quarantine".equalsIgnoreCase(formObject.getNGValue("itemtypeflag").toString())) {
            if (result.equals(result1)) {
                System.out.println("barabar h pehle wala");
            } else {
                System.out.println("inside else barabar nhi h ");
                throw new ValidatorException(new FacesMessage("Kindly Fill Quarantine Data", ""));
            }
        }

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
