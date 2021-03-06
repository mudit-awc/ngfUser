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
import com.newgen.common.Calculations;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.IRepeater;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.excp.CustomExceptionHandler;
import com.newgen.omniforms.listener.FormListener;
import java.util.Date;
import javax.faces.application.FacesMessage;

public class StoreUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    CallGateentryService objGetSetGateEntryData = null;
    Calculations objCalculations = null;
    PostGRN objPostGRN = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, returnvalue = null, Query;
    String storestatus = "";
    List<List<String>> result;
    boolean negativeGRNchecker = false;
    private String webserviceStatus;
    AccountsGeneral objAccountsGeneral = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    MultipleGrnGeneral objMultipleGrnGeneral = null;

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
        objPicklistListenerHandler = new PicklistListenerHandler();
        objMultipleGrnGeneral = new MultipleGrnGeneral();
        objCalculations = new Calculations();

        formObject.setNGValue("processid", processInstanceId);
        IRepeater RepeaterControlFrame5 = formObject.getRepeaterControl("Frame5");
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "currency":
                        objCalculations.exronCurrencyChangePoProcess("currency", "invoiceamount", "newbaseamount", "exchangerate");
                        break;

                    case "storestatus":
                        String storestatus = formObject.getNGValue("storestatus");
                        if (storestatus.equalsIgnoreCase("Exception")) {
                            //System.out.println("inside if of value change of store status");
                            formObject.setVisible("storeexception", true);
                            Query = "select ExceptionName from ExceptionMaster";
                            System.out.println("query is :" + Query);
                            result = formObject.getDataFromDataSource(Query);
                            for (int i = 0; i < result.size(); i++) {
                                formObject.addComboItem("storeexception", result.get(i).get(0), result.get(i).get(0));
                            }
                        } else {
                            formObject.clear("storeexception");
                            formObject.setVisible("storeexception", false);
                        }
                        break;
                    case "invoiceamount":
                    case "exchangerate":
                        objCalculations.exronBaseamountandExchangerateChange("currency", "invoiceamount", "newbaseamount", "exchangerate");
                        formObject.setNGValue("summ_invoiceamount", formObject.getNGValue("invoiceamount"));
                        break;

                    case "newbaseamount":
                        formObject.setNGValue("summ_invoiceamountreporting", formObject.getNGValue("newbaseamount"));
                        break;

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
                                formObject.setVisible("q_sb_mfgdate", false);
                                formObject.setVisible("Label99", false);
                                formObject.clear("q_sb_expirydate");
                                formObject.clear("q_sb_mfgdate");
//                                formObject.setNGColumnWidth("q_serialbatchregistration", 5, 0);
                            } else if ("BN".equalsIgnoreCase(result.get(0).get(0))) {
                                formObject.setNGValue("q_sb_registrationtype", "Batch");
                                formObject.setVisible("q_sb_expirydate", true);
                                formObject.setVisible("Label100", true);
                                formObject.setVisible("q_sb_mfgdate", true);
                                formObject.setVisible("Label99", true);
//                                formObject.setNGColumnWidth("q_serialbatchregistration", 5, 128);
                            }
                            Query = "select itemid from cmplx_gateentryline where pinstanceid = '" + processInstanceId + "' and linenumber='" + linenumber + "'";
                            result = formObject.getDataFromDataSource(Query);
                            formObject.setNGValue("q_sb_itemno", result.get(0).get(0));
                        }
                        break;

                    case "grnstartdate":
                    case "grnenddate":
                        if (formObject.isFormLoadChange() == false) {
                            objMultipleGrnGeneral.grnStartEndDateChange();
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":

                switch (pEvent.getSource().getName()) {
                    case "Pick_registrationno":
                        System.out.println("inside Pick_registrationno");
                        Query = "select distinct registrationno from cmplx_serialbatchregistration "
                                + "where "
                                + "registrationtype = '" + formObject.getNGValue("q_sb_registrationtype") + "' "
                                + "and itemid = '" + formObject.getNGValue("q_sb_itemno") + "' "
                                + "order by registrationno asc";
                        System.out.println("Query ::" + Query);
                        objPicklistListenerHandler.openPickList(
                                "q_sb_registrationno",
                                "Registration Number",
                                "Registration Master",
                                70,
                                70,
                                Query
                        );

                        break;

                    case "Btn_AddLine":
                        System.out.println("inside java add line Btn_AddLine");
                        if ("".equalsIgnoreCase(formObject.getNGValue("q_sb_registrationno"))) {
                            throw new ValidatorException(new FacesMessage("Registration Number can't be Empty", ""));
                        }

                        if ("".equalsIgnoreCase(formObject.getNGValue("q_sb_quantity"))) {
                            throw new ValidatorException(new FacesMessage("Quantity can't be Empty", ""));
                        }

                        if ("Batch".equalsIgnoreCase(formObject.getNGValue("q_sb_registrationtype"))) {
                            if ("".equalsIgnoreCase(formObject.getNGValue("q_sb_mfgdate"))) {
                                throw new ValidatorException(new FacesMessage("Manufacturing date can't be Empty", ""));
                            }
                            if ("".equalsIgnoreCase(formObject.getNGValue("q_sb_expirydate"))) {
                                throw new ValidatorException(new FacesMessage("Expiry date can't be Empty", ""));
                            }
                        }

                        float sum = 0;
                        int RowCountq_serialbatchregistration = formObject.getLVWRowCount("q_serialbatchregistration");
                        System.out.println("-RowCountq_serialbatchregistration " + RowCountq_serialbatchregistration);
                        float quantityint = Float.parseFloat(formObject.getNGValue("q_sb_quantity"));
                        String linenumberdropdown = formObject.getNGValue("q_sb_linenumber");
                        String itemno = formObject.getNGValue("q_sb_itemno");
                        String registrationno = formObject.getNGValue("q_sb_registrationno");
                        Query = "select grnqty from cmplx_gateentryline where pinstanceid='" + processInstanceId + "' "
                                + "and linenumber ='" + linenumberdropdown + "'";
                        System.out.println("Query of add line: " + Query);
                        result = formObject.getDataFromDataSource(Query);
                        float grnquantity = Float.parseFloat(result.get(0).get(0));
                        System.out.println("grnquantity ^^ " + grnquantity);
                        System.out.println(" -RowCountq_serialbatchregistration " + RowCountq_serialbatchregistration);
                        for (int j = 0; j < RowCountq_serialbatchregistration; j++) {
                            if ((linenumberdropdown.equalsIgnoreCase(formObject.getNGValue("q_serialbatchregistration", j, 1)))
                                    && (itemno.equalsIgnoreCase(formObject.getNGValue("q_serialbatchregistration", j, 2)))
                                    && (registrationno.equalsIgnoreCase(formObject.getNGValue("q_serialbatchregistration", j, 4)))) {
                                System.out.println("teeno match h");
                                throw new ValidatorException(new FacesMessage("This Registration No. " + registrationno + " has been already entered for "
                                        + "line No. " + linenumberdropdown + " and Item No. " + itemno + ".\n"
                                        + "Kindly change the Registration Number", ""));
                            }
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

                            if ("".equalsIgnoreCase(formObject.getNGValue("q_sb_registrationno"))) {
                                throw new ValidatorException(new FacesMessage("Registration No. can't be Empty", ""));
                            }
                            formObject.ExecuteExternalCommand("NGAddRow", "q_serialbatchregistration");
                        } else {
                            throw new ValidatorException(new FacesMessage("Value of Quantity can't be exceeding from GRN Quantity", ""));
                        }
                        formObject.RaiseEvent("WFSave");
                        break;
                    case "Btn_DeleteLine":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_serialbatchregistration");
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

                    case "Btn_add_multplegrn":
                        objMultipleGrnGeneral.addMultipleGrnClick(processInstanceId);
                        break;

                    case "Btn_delete_multiplegrn":
                        objMultipleGrnGeneral.deleteMultipleGrnClick();
                        break;

                    case "Btn_combine":
                        objMultipleGrnGeneral.combineMultipleGrnClick();
                        break;

                    case "Btn_cancelGRN":
                        System.out.println("Inside cancel GRN button");
                        objMultipleGrnGeneral.cancelGrnClick();
                        break;

                    case "Btn_Resolve":
                        System.out.println("inside btn resolve");
                        objAccountsGeneral.setResolveAXException();
                        break;

                    case "Btn_Export_GateEntry":
                        // openGateEntryBamReport("GateEntryDetails");
                        objGeneral.openbamreport("GateEntryDetails");
                        break;

                    case "Btn_View":
                        System.out.println("inside btn view");
                        String pid = formObject.getNGValue("q_multiplegrninvoicing", formObject.getSelectedIndex("q_multiplegrninvoicing"), 0);
                        System.out.println("selectedpid: " + pid);
                        objMultipleGrnGeneral.viewwi(pid);
                        break;
                }

                break;
            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 7: { //Tax Document
                                System.out.println("Inside case 7 Tax Document ");
                                Query = "select po.linenumber,po.itemnumber,gstin_gdi_uid,COALESCE(hsn,''),COALESCE(sac,''),COALESCE(igstrate,'0'),"
                                        + "COALESCE(igsttaxamount,'0'),COALESCE(cgstrate,'0'),COALESCE(cgsttaxamount,'0'),"
                                        + "COALESCE(sgstrate,'0'),COALESCE(sgsttaxamount,'0'),COALESCE(nonbusinessusagepercent,'0'),"
                                        + "exempt,inv.newassessableamount,po.nongst,COALESCE(po.taxratetype,''),"
                                        + "COALESCE(po.vatrate,'0'),COALESCE(po.vattaxamount,'0'),po.purchaseorderno "
                                        + "from cmplx_poline po, cmplx_invoiceline inv where "
                                        + "po.pinstanceid = '" + processInstanceId + "' and po.pinstanceid = inv.pinstanceid "
                                        + "and po.linenumber = inv.linenumber and po.itemnumber = inv.itemid order by linenumber,itemnumber";
                                objAccountsGeneral.setTaxDocument(Query, "q_taxdocument", processInstanceId);

                            }
                            break;
                        }
                        break;
                }
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
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        System.out.println("----------------------Intiation Workstep Loaded from form populated.---------------------------");
        formObject.setNGValue("storestatus", "");
        formObject.setNGValue("storeremarks", "");
        formObject.setSheetEnable("Tab2", 0, false);
        formObject.setSelectedSheet("Tab2", 2);
        formObject.setEnabled("Frame4", true);

        //Add status:
        if (formObject.getNGValue("multiplegrn").equalsIgnoreCase("True")) {
            formObject.setVisible("Frame11", true);
            formObject.setVisible("Frame9", false);

            Query = "select ext.grnnumber from ext_supplypoinvoices ext,WFINSTRUMENTTABLE wf "
                    + "where wf.ProcessInstanceID = ext.processid "
                    + "and wf.ActivityName = 'HoldMultipleGRN' "
                    //                    + "and ext.grnnumber not in(select grnnumber from cmplx_multiplegrninvoicing)"
                    + "and purchaseorderno = '" + formObject.getNGValue("purchaseorderno") + "' "
                    + "and format(grnsyncdate,'dd/MM/yyyy') "
                    + "between '" + formObject.getNGValue("grnstartdate") + "' "
                    + "and '" + formObject.getNGValue("grnenddate") + "'";
            System.out.println("Query: " + Query);
            List<List<String>> grnresult = formObject.getDataFromDataSource(Query);
            if (grnresult.size() > 0) {
                for (int i = 0; i < grnresult.size(); i++) {
                    formObject.addComboItem("q_grn", grnresult.get(i).get(0), grnresult.get(i).get(0));
                }
            }
            if (activityName.equalsIgnoreCase("StoreMaker")) {
                formObject.addComboItem("storestatus", "Submit Single Invoice Multiple GRN", "Submit Single Invoice Multiple GRN");
            }
            if (activityName.equalsIgnoreCase("StoreChecker")) {
                formObject.addComboItem("storestatus", "Approve Single Invoice Multiple GRN", "Approve Single Invoice Multiple GRN");
            }
            formObject.addComboItem("storestatus", "Reject", "Reject");
        } else {
            String previousactivity = formObject.getNGValue("previousactivity");
            String previousstatus = formObject.getNGValue("previousstatus");
            if (previousactivity.equalsIgnoreCase("Initiator")
                    && activityName.equalsIgnoreCase("StoreMaker")) {
                formObject.addComboItem("storestatus", "Submit For GRN", "Submit For GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
                formObject.addComboItem("storestatus", "Reject", "Reject"); // farman
            } else if (previousactivity.equalsIgnoreCase("StoreMaker")
                    && previousstatus.equalsIgnoreCase("Submit For GRN")
                    && activityName.equalsIgnoreCase("StoreChecker")) {
                formObject.addComboItem("storestatus", "Create GRN", "Create GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
                formObject.addComboItem("storestatus", "Reject", "Reject"); // farman
            } /////// farman
            else if (previousactivity.equalsIgnoreCase("StoreChecker")
                    && previousstatus.equalsIgnoreCase("Reject")
                    && activityName.equalsIgnoreCase("StoreMaker")) {
                formObject.addComboItem("storestatus", "Submit For GRN", "Submit For GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
                formObject.addComboItem("storestatus", "Reject", "Reject"); // farman
            } //////////
            else if (previousactivity.equalsIgnoreCase("AccountsMaker")
                    && previousstatus.equalsIgnoreCase("GRN Cancellation Required")
                    && activityName.equalsIgnoreCase("StoreMaker")) {
                formObject.addComboItem("storestatus", "Submit For Reversal GRN", "Submit For Reversal GRN");
//                formObject.addComboItem("storestatus", "Reject", "Reject");
                formObject.addComboItem("storestatus", "Exception", "Exception");
            } else if (previousactivity.equalsIgnoreCase("StoreMaker")
                    && previousstatus.equalsIgnoreCase("Submit For Reversal GRN")
                    && activityName.equalsIgnoreCase("StoreChecker")) {
                formObject.addComboItem("storestatus", "Create Reversal GRN", "Create Reversal GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
            } else if (previousactivity.equalsIgnoreCase("PurchaseUser")
                    && previousstatus.equalsIgnoreCase("Purchase Return")
                    || (previousactivity.equalsIgnoreCase("StoreChecker")
                    && previousstatus.equalsIgnoreCase("Reject NRGP"))) {
                formObject.addComboItem("storestatus", "Accept NRGP", "Accept NRGP");
                formObject.addComboItem("storestatus", "Reject NRGP", "Reject NRGP");
            } else if ((previousactivity.equalsIgnoreCase("PurchaseUser")
                    && previousstatus.equalsIgnoreCase("Replacement or Exchange"))
                    || (previousactivity.equalsIgnoreCase("StoreChecker")
                    && (previousstatus.equalsIgnoreCase("Reject RGP")))) {
                formObject.addComboItem("storestatus", "Accept RGP", "Accept RGP");
                formObject.addComboItem("storestatus", "Reject RGP", "Reject RGP");
            } else if (previousactivity.equalsIgnoreCase("StoreMaker")
                    && previousstatus.equalsIgnoreCase("Accept NRGP")) {
                formObject.addComboItem("storestatus", "Accept NRGP", "Accept NRGP");
                formObject.addComboItem("storestatus", "Reject NRGP", "Reject NRGP");
            } else if (previousactivity.equalsIgnoreCase("StoreMaker")
                    && previousstatus.equalsIgnoreCase("Accept RGP")) {
                formObject.addComboItem("storestatus", "Accept RGP", "Accept RGP");
                formObject.addComboItem("storestatus", "Reject RGP", "Reject RGP");
            } else if (previousstatus.equalsIgnoreCase("Challan Accepted")) {
                formObject.addComboItem("storestatus", "Replacement or Exchange Recieved", "Replacement or Exchange Recieved");
            } else if (previousstatus.equalsIgnoreCase("Replacement or Exchange Recieved")) {
                formObject.addComboItem("storestatus", "Send For Quality Inspection", "Send For Quality Inspection");
            } else if (activityName.equalsIgnoreCase("HoldMultipleGRN")) {
                formObject.addComboItem("storestatus", "Create Reversal GRN", "Create Reversal GRN");
            } // vaibhav code for exception cleared status in store checker 03/06/2020
            else if (previousactivity.equalsIgnoreCase("PurchaseUser")
                    && previousstatus.equalsIgnoreCase("Exception Cleared")
                    && activityName.equalsIgnoreCase("StoreChecker")) {
                formObject.addComboItem("storestatus", "Create GRN", "Create GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
                formObject.addComboItem("storestatus", "Reject", "Reject");
            } else if (previousactivity.equalsIgnoreCase("PurchaseUser")
                    && previousstatus.equalsIgnoreCase("Exception Cleared")
                    && activityName.equalsIgnoreCase("StoreMaker")) {
                formObject.addComboItem("storestatus", "Submit For GRN", "Submit For GRN");
                formObject.addComboItem("storestatus", "Exception", "Exception");
                formObject.addComboItem("storestatus", "Reject", "Reject");
            }

            String Currency = formObject.getNGValue("currency");
            if (Currency.equalsIgnoreCase("INR")) {
                formObject.setEnabled("exchangerate", false);
            }

            // end of code by vaibhav 03/06/2020
            Query = "select COUNT(*) from cmplx_poline where itemtrackingdimension in ('SN','BN') and  pinstanceid = '" + processInstanceId + "'";
            result = formObject.getDataFromDataSource(Query);
            int countItemtrackingDimension = Integer.parseInt(result.get(0).get(0));
            if (countItemtrackingDimension == 0) {
                //formObject.setEnabled("Frame4", false);
                formObject.setEnabled("q_sb_linenumber", false);
                formObject.setEnabled("q_sb_itemno", false);
                formObject.setEnabled("q_sb_registrationtype", false);
            } else {
                formObject.setEnabled("q_sb_itemno", false);
                formObject.setEnabled("q_sb_registrationtype", false);
            }

            Query = "select linenumber from cmplx_gateentryline where pinstanceid = '" + processInstanceId + "'";
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    Query = "select itemtrackingdimension from cmplx_poline where pinstanceid = '" + processInstanceId + "' "
                            + "and linenumber = '" + result.get(i).get(0) + "'";
                    List<List<String>> result1 = formObject.getDataFromDataSource(Query);
                    if ("SN".equalsIgnoreCase(result1.get(0).get(0))) {
                        formObject.addComboItem("q_sb_linenumber", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                    } else if ("BN".equalsIgnoreCase(result1.get(0).get(0))) {
                        formObject.addComboItem("q_sb_linenumber", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                    }
                }
            }
        }

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
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormStarted(FormEvent arg0) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        System.out.println("******Inside Submit form Started****");
        String storeexception = "";
        storestatus = formObject.getNGValue("storestatus");
        //// FARMAN
        if (activityName.equalsIgnoreCase("StoreMaker")) {
            formObject.setNGValue("storemaker", userName);
        }
        if (activityName.equalsIgnoreCase("StoreChecker")) {
            formObject.setNGValue("storechecker", userName);
        }
        ///
        if (storestatus.equalsIgnoreCase("Exception")) {
            storeexception = ": " + formObject.getNGValue("storeexception");
        } else if (storestatus.equalsIgnoreCase("Submit For GRN")
                || storestatus.equalsIgnoreCase("Create GRN")
                || storestatus.equalsIgnoreCase("Submit Single Invoice Multiple GRN")
                || storestatus.equalsIgnoreCase("Approve Single Invoice Multiple GRN")
                || storestatus.equalsIgnoreCase("Submit For Reversal GRN")) {
            if (activityName.equalsIgnoreCase("StoreMaker")) {
                objGeneral.checkSupplyPoDoAUser("StoreChecker");
                formObject.setNGValue("FilterDoA_ApproverLevel", "StoreChecker");
            } else if (activityName.equalsIgnoreCase("StoreChecker")) {
                if (formObject.getNGValue("multiplegrn").equalsIgnoreCase("False")) {
                    objGeneral.setItemTypeFlag(processInstanceId);
                    String itemtypeflag = formObject.getNGValue("itemtypeflag");
                    if (itemtypeflag.equalsIgnoreCase("Quarantine")
                            || itemtypeflag.equalsIgnoreCase("PP Bags")
                            || itemtypeflag.equalsIgnoreCase("None")) {
                        objGeneral.checkSupplyPoDoAUser("QualityMaker");
                        formObject.setNGValue("FilterDoA_ApproverLevel", "QualityMaker");
                    } else {
                        objGeneral.checkSupplyPoDoAUser("AccountsMaker");
                        formObject.setNGValue("FilterDoA_ApproverLevel", "AccountsMaker");
                    }
                } else {
                    objGeneral.checkSupplyPoDoAUser("AccountsMaker");
                    formObject.setNGValue("FilterDoA_ApproverLevel", "AccountsMaker");
                }
            }
        } else if (storestatus.equalsIgnoreCase("Accept RGP")
                || storestatus.equalsIgnoreCase("Accept NRGP")) {
            if (activityName.equalsIgnoreCase("StoreMaker")) {
                objGeneral.checkSupplyPoDoAUser("StoreChecker");
                formObject.setNGValue("FilterDoA_ApproverLevel", "StoreChecker");
            } else if (activityName.equalsIgnoreCase("StoreChecker")) {
                objGeneral.checkSupplyPoDoAUser("AccountsMaker");
                formObject.setNGValue("FilterDoA_ApproverLevel", "AccountsMaker");
            }
        } else {
            if (activityName.equalsIgnoreCase("AXStoreSyncException")) {
                storestatus = formObject.getNGValue("previousstatus");
            }
        }

        objAccountsGeneral.getsetSupplyPoSummary(processInstanceId);
        objGeneral.maintainHistory(
                userName,
                activityName,
                storestatus + storeexception,
                "",
                formObject.getNGValue("storeremarks"),
                "q_transactionhistory"
        );
        formObject.setNGValue("previousactivity", activityName);
        formObject.setNGValue("previousstatus", storestatus);
        formObject.setNGValue("TransporterCode1", formObject.getNGValue("transportercode"));
        formObject.setNGValue("TransporterName1", formObject.getNGValue("transportername"));
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

    void openGateEntryBamReport(String Report_name) {
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        HashMap<String, String> hm = new HashMap<>();
        String UserIndex = formConfig.getConfigElement("UserIndex");
        String ip = formConfig.getConfigElement("ServletPath");
        ip = ip.split("webdesktop")[0];
        String ip1 = ip.replaceAll("//", "/");
        Query = "select REPORTID from CRREPORTTABLE where REPORTNAME = '" + Report_name + "'";
        result = formObject.getDataFromDataSource(Query);
        String Link = "" + ip + "bam/login/login.jsf?CalledFrom=EXT&UserId=" + userName + "&UserIndex=" + UserIndex + "&SessionId=" + sessionId + "&CabinetName="
                + "" + engineName + "&processInstanceId=" + processInstanceId + "&LaunchClient=RI&ReportIndex=" + result.get(0).get(0) + "&AjaxRequest=Y&OAPDomHost=" + ip1.split("/")[1] + "&ProcessInstanceId="
                + processInstanceId + "";
        System.out.println("Moving to client js ");
        throw new ValidatorException(new CustomExceptionHandler("Btn_Export_GateEntry", Link, "", hm));
    }
}
