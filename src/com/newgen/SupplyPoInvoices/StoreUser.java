/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallGateentryService;
import com.newgen.Webservice.PostGRN;
import java.util.HashMap;
import java.util.List;
import javax.faces.validator.ValidatorException;

import com.newgen.common.General;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.common.ReadProperty;
import com.newgen.common.AccountsGeneral;

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

public class StoreUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    Initiator objInitiator = null;
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
    String storestatus = null;

    boolean negativeGRNchecker = false;
    List<List<String>> result;
    List<List<String>> result1;
    private String webserviceStatus;
    AccountsGeneral objAccountsGeneral = null;
    PicklistListenerHandler objPicklistListenerHandler = null;

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
        objInitiator = new Initiator();
        objAccountsGeneral = new AccountsGeneral();
        objPicklistListenerHandler = new PicklistListenerHandler();
        formObject.setNGValue("processid", processInstanceId);
        IRepeater RepeaterControlFrame5 = formObject.getRepeaterControl("Frame5");
        String GateEntryLineLV = "q_gateentrylines";
        String SerialBatchRegistrationLV = "q_serialbatchregistration";
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
                    case "q_sb_linenumber":
                        String linenumber = formObject.getNGValue("q_sb_linenumber");
                        if ("".equalsIgnoreCase(linenumber)) {
                            formObject.setNGValue("q_sb_registrationtype", "");
                        } else {
                            Query = "select itemtrackingdimension from cmplx_poline where pinstanceid = '" + processInstanceId + "' and linenumber= '" + linenumber + "'";
                            result = formObject.getDataFromDataSource(Query);
                            System.out.println("query : " + Query);
                            System.out.println("result iiii " + result);
                            if ("SN".equalsIgnoreCase(result.get(0).get(0))) {
                                formObject.setNGValue("q_sb_registrationtype", "Serial");
                                formObject.setVisible("q_sb_expirydate", false);
                                formObject.setVisible("Label100", false);
                                formObject.setNGColumnWidth("q_serialbatchregistration", 5, 0);
                            } else if ("BN".equalsIgnoreCase(result.get(0).get(0))) {
                                formObject.setNGValue("q_sb_registrationtype", "Batch");
                                formObject.setVisible("q_sb_expirydate", true);
                                formObject.setVisible("Label100", true);
                                formObject.setNGColumnWidth("q_serialbatchregistration", 5, 128);
                            }
                            Query = "select itemid from cmplx_gateentryline where pinstanceid = '" + processInstanceId + "' and linenumber='" + linenumber + "'";
                            result = formObject.getDataFromDataSource(Query);
                            formObject.setNGValue("q_sb_itemno", result.get(0).get(0));
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {

                    case "btn_fetchpogedetails":
                        System.out.println("inside btn_fetchpogedetails");
                        String purchaseorderno = formObject.getNGValue("purchaseorderno");
                        String invoiceno = formObject.getNGValue("invoiceno");
//                        objInitiator.populatePurchaseOrder(purchaseorderno);
//                        objInitiator.populateGateEntry(purchaseorderno, invoiceno);
                        formObject.setNGValue("purchaseorderno", "");
                        break;

                    case "Btn_Resolve":
                        System.out.println("inside btn resolve");
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Pick_registrationno":
                        System.out.println("inside Pick_registrationno");
                        Query = "select distinct registrationno from cmplx_serialbatchregistration order by registrationno asc";
                        objPicklistListenerHandler.openPickList("q_sb_registrationno", "Registration Number", "Registration Master", 70, 70, Query);

                        break;

                    case "Btn_AddLine":
                        System.out.println("inside java add line Btn_AddLine");
                        ListView ListViewq_serialbatchregistration = (ListView) formObject.getComponent("q_serialbatchregistration");
                        float sum = 0;
                        int RowCountq_serialbatchregistration = ListViewq_serialbatchregistration.getRowCount();
                        System.out.println("-RowCountq_serialbatchregistration " + RowCountq_serialbatchregistration);
                        float quantityint = Float.parseFloat(formObject.getNGValue("q_sb_quantity"));
                        String linenumberdropdown = formObject.getNGValue("q_sb_linenumber");
                        Query = "select grnqty from cmplx_gateentryline where pinstanceid='" + processInstanceId + "' "
                                + "and linenumber ='" + linenumberdropdown + "'";
                        System.out.println("Query of add line: " + Query);
                        result = formObject.getDataFromDataSource(Query);
                        float grnquantity = Float.parseFloat(result.get(0).get(0));
                        System.out.println("grnquantity ^^ " + grnquantity);
                        System.out.println(" -RowCountq_serialbatchregistration " + RowCountq_serialbatchregistration);
                        for (int j = 0; j < RowCountq_serialbatchregistration; j++) {
                            if (linenumberdropdown.equalsIgnoreCase(formObject.getNGValue("q_serialbatchregistration", j, 1))) {
                                System.out.println("inside if line no dropdown");
                                String grnsum = formObject.getNGValue("q_serialbatchregistration", j, 3);
                                System.out.println("grnsum " + grnsum);
                                sum = sum + Float.parseFloat(grnsum);
                            }
                        }
                        System.out.println("value of sum " + sum);
                        float finalsum = quantityint + sum;
                        System.out.println("finalsum " + finalsum);

                        if (finalsum <= grnquantity) {
                            if (quantityint == 0 || quantityint == 0.0 || quantityint == 0.00) {
                                throw new ValidatorException(new FacesMessage("Value of Quantity can't be Zero", ""));
                            }
                            formObject.ExecuteExternalCommand("NGAddRow", "q_serialbatchregistration");
                        } else {
                            throw new ValidatorException(new FacesMessage("Value of Quantity can't be exceeding from GRN Quantity", ""));
                        }

                        break;
                    case "Btn_DeleteLine":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_serialbatchregistration");
                        break;
                    case "Btn_SplitLine":
//                        System.out.println("Indside Button Click Btn_SplitLine");
//                        String Resgistrationtype_temp = formObject.getNGValue("Resgistrationtype_temp");
//                        int SplitLineInto = Integer.parseInt(formObject.getNGValue("SplitLineInto"));
//                        System.out.println("SplitLineInto " + SplitLineInto);
//                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent(GateEntryLineLV);
//                        int selectrow = ListViewq_gateentrylines.getSelectedRowIndex();
//                        String SelectedItemId = formObject.getNGValue(GateEntryLineLV, selectrow, 1);
//                        System.out.println("SelectedItemId : " + SelectedItemId);
//                        RepeaterControlFrame5.clear();
//                        for (int i = 0; i < SplitLineInto; i++) {
//                            int countbefore = RepeaterControlFrame5.getRepeaterRowCount();
//                            RepeaterControlFrame5.addRow();
//                            System.out.println(countbefore);
//                            RepeaterControlFrame5.setValue(countbefore, "type_temp", Resgistrationtype_temp);
//                            RepeaterControlFrame5.setValue(countbefore, "itemid_temp", SelectedItemId);
//                            RepeaterControlFrame5.setValue(countbefore, "quantity_temp", "");
//                            RepeaterControlFrame5.setValue(countbefore, "registrationno_temp", "");
//                            RepeaterControlFrame5.setValue(countbefore, "mfgdate_temp", "");
//                            RepeaterControlFrame5.setValue(countbefore, "expirydt_temp", "");
//                        }
//                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_updateregistration":
                        String SerialBatchRegistrationXML = "";
                        int rowCountRepeaterControlFrame5 = RepeaterControlFrame5.getRepeaterRowCount();
                        System.out.println("Row Count rowCountRepeaterControlFrame5: " + rowCountRepeaterControlFrame5);

                        for (int i = 0; i < rowCountRepeaterControlFrame5; i++) {
                            SerialBatchRegistrationXML = (new StringBuilder()).append(SerialBatchRegistrationXML).
                                    append("<ListItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "type_temp")).
                                    append("</SubItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "itemid_temp")).
                                    append("</SubItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "quantity_temp")).
                                    append("</SubItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "registrationno_temp")).
                                    append("</SubItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "mfgdate_temp")).
                                    append("</SubItem><SubItem>").append(RepeaterControlFrame5.getValue(i, "expirydt_temp")).
                                    append("</SubItem></ListItem>").toString();
                        }
                        formObject.clear("q_serialbatchregistration");
                        formObject.NGAddListItem("q_serialbatchregistration", SerialBatchRegistrationXML);
                        RepeaterControlFrame5.clear();
                        formObject.setNGValue("SplitLineInto", "");
                        break;

                    case "q_gateentrylines":
//                        System.out.println("Inside click q_gateentrylines");
//                        ListView ListViewq_gateentrylines_1 = (ListView) formObject.getComponent(GateEntryLineLV);
//                        int selectedRowIndex = ListViewq_gateentrylines_1.getSelectedRowIndex();
//                        String ItemId = formObject.getNGValue(GateEntryLineLV, selectedRowIndex, 1);
//
//                        Query = "select itemtrackingdimension from cmplx_poline where pinstanceid = '" + processInstanceId + "'and itemnumber ='" + ItemId + "'";
//                        System.out.println("Item Tracking Dimension Query : " + Query);
//                        result = formObject.getDataFromDataSource(Query);
//                        if (result.size() > 0) {
//                            String itemtrackingdimension = result.get(0).get(0);
//                            System.out.println("Item Tracking Dimension Value : " + itemtrackingdimension);
//                            if ("SN".equalsIgnoreCase(itemtrackingdimension)) {
//                                formObject.setNGValue("Resgistrationtype_temp", "Serial No");
//                                formObject.setEnabled("Frame4", true);
//                            } else if ("BN".equalsIgnoreCase(itemtrackingdimension)) {
//                                formObject.setNGValue("Resgistrationtype_temp", "Batch No");
//                                formObject.setEnabled("Frame4", true);
//                            } else {
//                                formObject.setNGValue("Resgistrationtype_temp", "");
//                                formObject.setEnabled("Frame4", false);
//                                throw new ValidatorException(new FacesMessage("Serial Or Batch is not required for this line", ""));
//                            }
//                        }
                        break;

                    /*   case "Btn_GenerateGRN":

                     String itemid = formObject.getNGValue("Quality_itemselect");
                     int valueatGRN = 0;
                     String valueatitemid = "";
                     float unitPrice = 0,
                     sumofUnitPrice = 0,
                     inviceAmount = 0;

                     ListView ListViewq_polinesStore = (ListView) formObject.getComponent("q_polines");
                     int RowCountq_polines = ListViewq_polinesStore.getRowCount();
                     System.out.println("RowCountq_polines " + RowCountq_polines);

                     ListView ListViewq_gateentrylinesStore = (ListView) formObject.getComponent("q_gateentrylines");
                     int RowCountq_gateentrylines = ListViewq_gateentrylinesStore.getRowCount();
                     System.out.println("RowCountq_gateentrylines " + RowCountq_gateentrylines);

                     for (int j = 0; j < RowCountq_gateentrylines; j++) {
                     valueatitemid = formObject.getNGValue("q_gateentrylines", j, 1);
                     valueatGRN = Integer.parseInt(formObject.getNGValue("q_gateentrylines", j, 4));
                     System.out.println("valueatGRN : " + valueatGRN);
                     System.out.println("valueatitemid : " + valueatitemid);

                     for (int a = 0; a < RowCountq_polines; a++) {
                     if (valueatitemid.equalsIgnoreCase(formObject.getNGValue("q_polines", a, 1))) {
                     unitPrice = Float.parseFloat(formObject.getNGValue("q_polines", a, 6));
                     System.out.println("valueatGRN*unitPrice : " + (valueatGRN * unitPrice));
                     sumofUnitPrice = sumofUnitPrice + (valueatGRN * unitPrice);

                     }
                     }
                     }
                     System.out.println("sumofUnitPrice :: " + sumofUnitPrice);
                     inviceAmount = Float.parseFloat(formObject.getNGValue("invoiceamount"));
                     System.out.println("invoiceamount :: " + inviceAmount);
                     if (inviceAmount == sumofUnitPrice) {
                     try {
                     System.out.println("Inside button click Btn_GenerateGRN");
                     JSONObject request_json = new JSONObject();
                     JSONArray grnlinearray = new JSONArray();
                     request_json.put("PONumber", formObject.getNGValue("purchaseorderno"));
                     request_json.put("PackinSlipId", formObject.getNGValue("invoiceno"));
                     request_json.put("PackingSlipDate", formObject.getNGValue("invoicedate"));
                     request_json.put("EntryID", formObject.getNGValue("gateentryid"));
                     request_json.put("Remark", formObject.getNGValue("storeremarks"));

                     ListView ListViewq_poline = (ListView) formObject.getComponent(SerialBatchRegistrationLV);
                     int rowcount_poline = ListViewq_poline.getRowCount();
                     for (int i = 0; i < rowcount_poline; i++) {
                     String registrationtype = formObject.getNGValue(SerialBatchRegistrationLV, i, 0);
                     String item_id = formObject.getNGValue(SerialBatchRegistrationLV, i, 1);
                     String serialno = "", batchno = "";
                     System.out.println("registration type : " + registrationtype);
                     JSONObject serialbatchregistration = new JSONObject();
                     serialbatchregistration.put("ItemId", item_id);
                     serialbatchregistration.put("Quantity", formObject.getNGValue(SerialBatchRegistrationLV, i, 2));
                     serialbatchregistration.put("MfgDate", formObject.getNGValue(SerialBatchRegistrationLV, i, 4));
                     serialbatchregistration.put("ExpDate", formObject.getNGValue(SerialBatchRegistrationLV, i, 5));
                     if (registrationtype.equalsIgnoreCase("Serial No")) {
                     serialno = formObject.getNGValue(SerialBatchRegistrationLV, i, i);
                     batchno = "";
                     } else if (registrationtype.equalsIgnoreCase("Batch No")) {
                     serialno = "";
                     batchno = formObject.getNGValue(SerialBatchRegistrationLV, i, i);
                     }
                     serialbatchregistration.put("BatchNumber", batchno);
                     serialbatchregistration.put("SerialNumber", serialno);

                     Query = "select linenumber from cmplx_poline where "
                     + "pinstanceid = '" + processInstanceId + "' "
                     + "and itemnumber = '" + item_id + "' ";
                     result = formObject.getDataFromDataSource(Query);
                     serialbatchregistration.put("LineNum", result.get(0).get(0));
                     grnlinearray.put(serialbatchregistration);
                     }
                     request_json.put("GRNLines", grnlinearray);
                     System.out.println("Input Json : " + request_json.toString());
                     String outputJSON = objGeneral.callWebService(
                     objReadProperty.getValue("postGRN"),
                     request_json.toString()
                     );
                     webserviceStatus = new PostGRN().parseGRNOutputJSON(outputJSON);
                     } catch (JSONException ex) {
                     Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
                     }

                     if (!webserviceStatus.equalsIgnoreCase("true")) {
                     throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
                     }
                     formObject.setEnabled("invoiceamount", false);
                     } else {
                     System.out.println("Invoice amount mismatch");
                     throw new ValidatorException(new FacesMessage("Invoice amount mismatch", ""));

                     }

                     break;

                     case "Btn_CancelGRN":
                     JSONObject request_json = new JSONObject();
                     request_json.put("PONumber", formObject.getNGValue("purchaseorderno"));
                     request_json.put("PackingSlipId", formObject.getNGValue("invoiceno"));
                     String outputJSON = objGeneral.callWebService(
                     objReadProperty.getValue("grncancellation"),
                     request_json.toString()
                     );
                     try {
                     webserviceStatus = new PostGRN().parseGRNCancellationOutputJSON(outputJSON);
                     } catch (JSONException ex) {
                     Logger.getLogger(StoreUser.class.getName()).log(Level.SEVERE, null, ex);
                     }

                     if (!webserviceStatus.equalsIgnoreCase("true")) {
                     throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
                     }

                     break; */
                    case "Btn_NegativeGRN":
                        storestatus = "Negative GRN Generated";
                        System.out.println("value of negativeGRN :: " + storestatus);
                        formObject.setNGValue("storestatus", "Negative GRN Generated");
                        formObject.setNGValue("previousstatus", "NegativeGRNGenerated");
                        negativeGRNchecker = true;
                        System.out.println("negativeGRNchecker : " + negativeGRNchecker);
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
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");
        formObject.setNGValue("storestatus", null);
        formObject.setNGValue("storeremarks", null);
        formObject.setSheetEnable("Tab2", 0, false);
        formObject.setSelectedSheet("Tab2", 2);
        formObject.setEnabled("Frame4", true);
        ///
        Query = "select COUNT(*) from cmplx_poline where itemtrackingdimension in ('SN','BN') and  pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        int countItemtrackingDimension = Integer.parseInt(result.get(0).get(0));
        System.out.println("countItemtrackingDimension : " + countItemtrackingDimension);
        if (countItemtrackingDimension == 0) {
            System.out.println("disabled serial batch fram");
            formObject.setEnabled("Frame4", false);
        }

///
        Query = "select linenumber from cmplx_gateentryline where pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            System.out.println("inside if farman");
            for (int i = 0; i < result.size(); i++) {
                System.out.println("i" + i);
                Query1 = "select itemtrackingdimension from cmplx_poline where pinstanceid = '" + processInstanceId + "' and linenumber = '" + result.get(i).get(0) + "'";
                System.out.println("Qyery :" + Query1);
                result1 = formObject.getDataFromDataSource(Query1);
                System.out.println("result1 store :: " + result1);
                System.out.println("result1.get(i).get(0) == " + result1.get(0).get(0));
                if ("SN".equalsIgnoreCase(result1.get(0).get(0))) {
                    System.out.println("inside SN");
                    formObject.addComboItem("q_sb_linenumber", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                    System.out.println("after SN");
                } else if ("BN".equalsIgnoreCase(result1.get(0).get(0))) {
                    System.out.println("inside BN");
                    formObject.addComboItem("q_sb_linenumber", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                    System.out.println("after BN");
                }
            }
//             if(result1.isEmpty()){
//                    formObject.setEnabled("Frame4", false);
//                    System.out.println("sadgfahgsfgasfDJhgfasuydgjyasgdjg");
//                }
            System.out.println("inside if code runing");
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
        System.out.println("******Inside Submit form Started****");

        // Code for Mandatory document started
        String prevActivity = formObject.getNGValue("previousactivity");
        System.out.println("negativeGRNchecker in submit form started : " + negativeGRNchecker);
        if (negativeGRNchecker == false) {
            storestatus = formObject.getNGValue("storestatus");
        }
        int GateOutPassCounter = 0;
        Query = "select Name from PDBDocument where DocumentIndex in \n"
                + "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = "
                + "(select itemindex from ext_supplypoinvoices where processid ='" + processInstanceId + "'))";
        result = formObject.getDataFromDataSource(Query);
        System.out.println("second query : " + Query);
        System.out.println("second result : " + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside query loop");
            if (result.get(i).get(0).equalsIgnoreCase("GateOutPass")) {
                System.out.println("GateOutPass counter m aagya");
                GateOutPassCounter++;
            }
        }

        if ((activityName.equalsIgnoreCase("StoreUser") && prevActivity.equalsIgnoreCase("PurchaseUser") && storestatus.equalsIgnoreCase("Purchase Return")) || (activityName.equalsIgnoreCase("StoreUser") && prevActivity.equalsIgnoreCase("AccountsUser") && storestatus.equalsIgnoreCase("Material Received"))) {
            System.out.println("Replacement/Exchange m aagya EQUAL WALE");
            if (GateOutPassCounter <= 0) {
                System.out.println("exception m aagyaEQUAL WALE");
                throw new ValidatorException(new FacesMessage("Kindly attach Gate Out Pass Document", ""));
            }
        }

        // Code for Mandatory document Ended here
        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
        int RowCount_gateentrylines = ListViewq_gateentrylines.getRowCount();
        Query = "select sum(cast(po.quarantinemanagement as int)) ,"
                + "sum(cast(po.ppbagmanagement as int)) ,"
                + "sum(cast(po.hlmanagement as int)) ,"
                + "sum(cast(po.rmmanagement as int)) "
                + "from cmplx_gateentryline gel, cmplx_poline po where "
                + "po.pinstanceid = gel.pinstanceid "
                + "and po.itemnumber = gel.itemid "
                + "and po.pinstanceid = '" + processInstanceId + "'"
                + "group by po.pinstanceid";
        System.out.println("Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (Integer.parseInt(result.get(0).get(0)) > 0) {
            formObject.setNGValue("itemtypeflag", "Quarantine");
        } else if (Integer.parseInt(result.get(0).get(1)) > 0) {
            formObject.setNGValue("itemtypeflag", "PP Bags");
        } else if (Integer.parseInt(result.get(0).get(2)) > 0) {
            formObject.setNGValue("itemtypeflag", "HG Limestone");
        } else if (Integer.parseInt(result.get(0).get(3)) > 0) {
            formObject.setNGValue("itemtypeflag", "Raw Material");
        } else {
            formObject.setNGValue("itemtypeflag", "None");
        }

        String gateoutpass = "";
        if (storestatus.equalsIgnoreCase("Negative GRN Generated")) {
            gateoutpass = formObject.getNGValue("gateoutpass");
        }
        ListView ListViewq_history = (ListView) formObject.getComponent("q_transactionhistory");
        int RowCountq_history = ListViewq_history.getRowCount();
        System.out.println("RowCountq_history : " + RowCountq_history);
        String storeRemarks = formObject.getNGValue("storeremarks");
        objGeneral.maintainHistory(userName, activityName, storestatus, gateoutpass, storeRemarks, "q_transactionhistory");
        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        formObject.setNGValue("previousactivity", activityName);
        System.out.println("Previous Activity store:" + formObject.getNGValue("previousactivity"));

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
