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
import com.newgen.json.JSONObject;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.IRepeater;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.json.JSONArray;
import org.json.JSONException;

public class StoreUser implements FormListener {

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
        String SerialBatchRegistrationLV = "q_serialbatchregistration";
        switch (pEvent.getType().name()) {

            case "VALUE_CHANGED":

                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "Btn_SplitLine":
                        System.out.println("Indside Button Click Btn_SplitLine");
                        String Resgistrationtype_temp = formObject.getNGValue("Resgistrationtype_temp");
                        int SplitLineInto = Integer.parseInt(formObject.getNGValue("SplitLineInto"));
                        System.out.println("SplitLineInto " + SplitLineInto);
                        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent(GateEntryLineLV);
                        int selectrow = ListViewq_gateentrylines.getSelectedRowIndex();
                        String SelectedItemId = formObject.getNGValue(GateEntryLineLV, selectrow, 0);
                        System.out.println("SelectedItemId : " + SelectedItemId);
                        RepeaterControlFrame5.clear();
                        for (int i = 0; i < SplitLineInto; i++) {
                            int countbefore = RepeaterControlFrame5.getRepeaterRowCount();
                            RepeaterControlFrame5.addRow();
                            System.out.println(countbefore);
                            RepeaterControlFrame5.setValue(countbefore, "type_temp", Resgistrationtype_temp);
                            RepeaterControlFrame5.setValue(countbefore, "itemid_temp", SelectedItemId);
                            RepeaterControlFrame5.setValue(countbefore, "quantity_temp", "");
                            RepeaterControlFrame5.setValue(countbefore, "registrationno_temp", "");
                            RepeaterControlFrame5.setValue(countbefore, "mfgdate_temp", "");
                            RepeaterControlFrame5.setValue(countbefore, "expirydt_temp", "");
                        }
                        formObject.RaiseEvent("WFSave");
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
                        System.out.println("Inside click q_gateentrylines");
                        ListView ListViewq_gateentrylines_1 = (ListView) formObject.getComponent(GateEntryLineLV);
                        int selectedRowIndex = ListViewq_gateentrylines_1.getSelectedRowIndex();
                        String ItemId = formObject.getNGValue("GateEntryLineLV", selectedRowIndex, 1);

                        Query = "select itemtrackingdimension from cmplx_poline where pinstanceid = '" + processInstanceId + "'";
                        System.out.println("Item Tracking Dimension Query : " + Query);
                        result = formObject.getDataFromDataSource(Query);
                        if (result.size() > 0) {
                            String itemtrackingdimension = result.get(0).get(0);
                            System.out.println("Item Tracking Dimension Value : " + itemtrackingdimension);
                            if ("SN".equalsIgnoreCase(itemtrackingdimension)) {
                                formObject.setNGValue("Resgistrationtype_temp", "Serial No");
                                formObject.setEnabled("Frame4", true);
                            } else if ("BN".equalsIgnoreCase(itemtrackingdimension)) {
                                formObject.setNGValue("Resgistrationtype_temp", "Batch No");
                                formObject.setEnabled("Frame4", true);
                            } else {
                                formObject.setNGValue("Resgistrationtype_temp", "");
                                formObject.setEnabled("Frame4", false);
                                throw new ValidatorException(new FacesMessage("Serial Or Batch is not required for this line", ""));
                            }
                        }
                        break;

                    case "Btn_GenerateGRN":

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
                            System.out.println("pehla for loop");
                            valueatitemid = formObject.getNGValue("q_gateentrylines", j, 0);
                            valueatGRN = Integer.parseInt(formObject.getNGValue("q_gateentrylines", j, 3));
                            System.out.println("valueatGRN : " + valueatGRN);
                            System.out.println("valueatitemid : " + valueatitemid);

                            for (int a = 0; a < RowCountq_gateentrylines; a++) {
                                System.out.println("Doosra for loop");
                                if (valueatitemid.equalsIgnoreCase(formObject.getNGValue("q_polines", a, 1))) {
                                    System.out.println("If ke ander");
                                    unitPrice = Float.parseFloat(formObject.getNGValue("q_polines", a, 6));
                                    System.out.println("valueatGRN*unitPrice : " + (valueatGRN * unitPrice));
                                    sumofUnitPrice = sumofUnitPrice + (valueatGRN * unitPrice);

                                }
                            }
                        }
                        System.out.println("sumofUnitPrice :: " + sumofUnitPrice);
                        inviceAmount = Float.parseFloat(formObject.getNGValue("invoiceamount"));
                        System.out.println("invoiceamount :: " + inviceAmount);
                        if(inviceAmount==sumofUnitPrice){
                            System.out.println("amount u");
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
                        }else{
                            System.out.println("Invoice amount mismatch");
                            
                            //show alert
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
        formObject.setSheetEnable("Tab2", 0, false);
        formObject.setSelectedSheet("Tab2", 1);
        IRepeater RepeaterControlFrame5 = formObject.getRepeaterControl("Frame5");
        List<String> HeaderNames = new ArrayList<String>();
        HeaderNames.add("Registration Type");
        HeaderNames.add("Item Id");
        HeaderNames.add("Quantity");
        HeaderNames.add("Registration Number");
        HeaderNames.add("Manufacturing Date");
        HeaderNames.add("Expiry Date");
        RepeaterControlFrame5.setRepeaterHeaders(HeaderNames);
        System.out.println("Set headers");
        RepeaterControlFrame5.setRepeaterLinkColor(Color.white);

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
        System.out.println("******Inside Submit form Started****");

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
