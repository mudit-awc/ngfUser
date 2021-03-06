/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.RABill;

import com.newgen.Webservice.CallAccessTokenService;
import com.newgen.Webservice.CallGetStockDetailsService;
import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.common.AccountsGeneral;
import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.PicklistListenerHandler;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author V_AWC
 */
public class ScanningUser implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    List<List<String>> result;
    AccountsGeneral objAccountsGeneral = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    PickList objPicklist;
    General objGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;
    CallGetStockDetailsService objCallGetStockDetails = null;

    @Override
    public void formLoaded(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {
            engineName = formConfig.getConfigElement("EngineName");
            sessionId = formConfig.getConfigElement("DMSSessionId");
            folderId = formConfig.getConfigElement("FolderId");
            serverUrl = formConfig.getConfigElement("ServletPath");
            activityName = formObject.getWFActivityName();
            processInstanceId = formConfig.getConfigElement("ProcessInstanceId");
            workItemId = formConfig.getConfigElement("WorkitemId");
            userName = formConfig.getConfigElement("UserName");
            processDefId = formConfig.getConfigElement("ProcessDefId");

        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent fe) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        System.out.println("inside form populated of indexer");
        formObject.clear("filestatus");
        Query = "select StateName from StateMaster order by StateCode asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("state", result.get(i).get(0), result.get(i).get(0));
        }
        //for Location
        Query = "select WMSLocationId from WarehouseLocationMaster where WarehouseCode='" + formObject.getNGValue("warehouse") + "'";
        System.out.println("query" + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("location", result.get(i).get(0).toString(), result.get(i).get(0).toString());
        }

        String[] arr2 = processInstanceId.split("-");
        String st = arr2[1];
        String str = "";
        for (int i = 0; i < st.length(); i++) {
            if (st.charAt(i) == '0') {

            } else {
                str = str + st.charAt(i);
            }
        }
        System.out.println("var " + str);
        formObject.setNGValue("jointmeasurementcode", str);

        formObject.addComboItem("filestatus", "Initiate", "Initiate");
        formObject.addComboItem("filestatus", "Discard", "Discard");
        formObject.addComboItem("filestatus", "Exception", "Exception");

        objGeneral.setInvoiceExtractedData("purchaseorder", "invoicenumber", "invoiceamount", "invoicedate");
    }

    public void saveFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    public void saveFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    public void submitFormStarted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        String levelflag = formObject.getNGValue("levelflag");
        System.out.println("Level Flag" + levelflag);
        Query = "select count(*) from ext_rabill ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Discard' , 'End') "
                + "and wf.ProcessInstanceID <> '" + processInstanceId + "'"
                + "and ext.purchaseorder = '" + formObject.getNGValue("purchaseorder") + "'";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {

            objGeneral.checkDuplicateInvoice(
                    formObject.getNGValue("contractor"),
                    formObject.getNGValue("invoicenumber"),
                    formObject.getNGValue("fiscalyear"),
                    processInstanceId
            );
            if ((!formObject.getNGValue("filestatus").equalsIgnoreCase("Discard"))
                    && (!formObject.getNGValue("filestatus").equalsIgnoreCase("Exception"))) {
                objGeneral.checkRABillDoAUser(levelflag, "");
            }
            formObject.setNGValue("FilterDoA_Department", formObject.getNGValue("department"));
            formObject.setNGValue("FilterDoA_Site", formObject.getNGValue("site"));
            formObject.setNGValue("FilterDoA_StateName", formObject.getNGValue("state"));

            formObject.setNGValue("previousactivity", activityName);
            objAccountsGeneral.getsetRABILLSummary(processInstanceId);
            objGeneral.maintainHistory(
                    userName,
                    activityName,
                    formObject.getNGValue("filestatus"),
                    "",
                    formObject.getNGValue("Text69"),
                    "q_transactionhistory"
            );

        } else {
            if ((!formObject.getNGValue("filestatus").equalsIgnoreCase("Discard"))
                    && (!formObject.getNGValue("filestatus").equalsIgnoreCase("Exception"))) {
                throw new ValidatorException(new FacesMessage("RABill with same purchase order number is already in process"));
            }
        }
        formObject.setNGValue("PuchaseOrderNo", formObject.getNGValue("purchaseorder"));
        formObject.setNGValue("VendorCode", formObject.getNGValue("contractor"));
        formObject.setNGValue("VendorName", formObject.getNGValue("contractorname"));
    }

    public void submitFormCompleted(FormEvent fe) throws ValidatorException {
        formObject = FormContext.getCurrentInstance().getFormReference();
    }

    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        objAccountsGeneral = new AccountsGeneral();
        objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "invoicedate":
                        objGeneral.setFiscalYear(formObject.getNGValue("invoicedate"), "fiscalyear");
                        break;

                    case "qpo_remainingqty":
                        System.out.println("inside value change qpo_remainingqty");
                        BigDecimal remainingqty = new BigDecimal(formObject.getNGValue("qpo_remainingqty"));
                        System.out.println("Rqty: " + remainingqty);
                        if (remainingqty.compareTo(BigDecimal.ZERO) == 0) {
                            formObject.setNGValue("qpo_quantity", remainingqty);
                            formObject.setNGValue("qpo_remainingoverdeliveryqty", remainingqty);
                            formObject.setNGValue("qpo_netamount", remainingqty);
                            formObject.setVisible("Btn_Add_AbstractSheet", false);
                            throw new ValidatorException(new FacesMessage("Remaining quantity is zero", ""));
                        } else {
                            System.out.println("inside else of qpo_remainingqty");
                            BigDecimal remqty = objGeneral.getRABillRemainingQty(remainingqty);

                            System.out.println("Remainig qty: " + remqty);
                            String overdeliveryqty = objCalculations.calculatePercentAmount(
                                    remqty.toString(),
                                    formObject.getNGValue("qpo_overdeliverypercent")
                            );
                            System.out.println("Over delivery qty: " + overdeliveryqty);
                            System.out.println("Over delivery total :" + remqty.add(new BigDecimal(overdeliveryqty)));
                            formObject.setNGValue("qpo_remainingoverdeliveryqty", remqty.add(new BigDecimal(overdeliveryqty)));

                            formObject.setNGValue("qpo_remainingqty2", remqty);
                            formObject.setNGValue("qpo_currentquantity", remqty);
                            if (remqty.compareTo(BigDecimal.ZERO) == 0) {
                                formObject.setEnabled("qpo_currentquantity", false);
                                formObject.setVisible("Btn_Add_AbstractSheet", false);
                                throw new ValidatorException(new FacesMessage("Remaining quantity is zero", ""));
                            } else {
                                formObject.setEnabled("qpo_currentquantity", true);
                                formObject.setVisible("Btn_Add_AbstractSheet", true);
                            }
                        }
                        break;

                    case "qpo_currentquantity":
                        BigDecimal qpo_currentquantity = new BigDecimal(formObject.getNGValue("qpo_currentquantity"));
                        BigDecimal qpo_remainingoverdeliveryqty = new BigDecimal(formObject.getNGValue("qpo_remainingoverdeliveryqty"));

                        if (qpo_currentquantity.compareTo(BigDecimal.ZERO) == 0) {
                            formObject.setVisible("Btn_Add_AbstractSheet", false);
                            throw new ValidatorException(new FacesMessage("Quantity can not be zero", ""));
                        }
                        if (qpo_currentquantity.compareTo(qpo_remainingoverdeliveryqty) > 0) {
                            formObject.setVisible("Btn_Add_AbstractSheet", false);
                            throw new ValidatorException(new FacesMessage("Quantity can not be greater than over delivery quantity", ""));
                        } else {
                            System.out.println("else of errorflag");
                            System.out.println("unit price : " + formObject.getNGValue("qpo_unitprice"));
                            BigDecimal remnetamount = qpo_currentquantity.multiply(
                                    new BigDecimal(formObject.getNGValue("qpo_unitprice"))
                            ).setScale(2, BigDecimal.ROUND_FLOOR);
                            formObject.setNGValue("qpo_netamount", remnetamount);
                            formObject.setNGValue("qpo_assessableamount", remnetamount);
                            formObject.setVisible("Btn_Add_AbstractSheet", true);
                        }
                        break;

                    case "location":
                        String location = formObject.getNGValue("location");
                        System.out.println("location " + location);
                        ListView ListViewq_raitemjournal = (ListView) formObject.getComponent("q_raitemjournal");
                        int rowCount = ListViewq_raitemjournal.getRowCount();
                        if (rowCount > 0) {
                            for (int i = 0; i < rowCount; i++) {
                                formObject.setNGValue("q_raitemjournal", i, 10, location);
                            }
                        }

                        if (location.equalsIgnoreCase("--Select--")) {
                            formObject.setNGValue("abs_location", "");
                        } else {
                            formObject.setNGValue("abs_location", location);
                        }
                        break;

                    case "filestatus":
                        String filestatus = formObject.getNGValue("filestatus");
                        if (filestatus.equalsIgnoreCase("Exception")) {
                            formObject.setVisible("Label58", true);
                            formObject.setVisible("Combo1", true);
                            Query = "select ExceptionName from ExceptionMaster";
                            System.out.println("query is :" + Query);
                            result = formObject.getDataFromDataSource(Query);
                            for (int i = 0; i < result.size(); i++) {
                                formObject.addComboItem("Combo1", result.get(i).get(0), result.get(i).get(0));
                            }

                        } else {
                            formObject.clear("Combo1");
                            formObject.setVisible("Label58", false);
                            formObject.setVisible("Combo1", false);
                        }

                        break;

                    case "ij_projectcode":
                        if (!formObject.getNGValue("ij_projectcode").equalsIgnoreCase("")) {
                            Query = "select remainingqty from cmplx_raabstractsheet "
                                    + "where pinstanceid = '" + processInstanceId + "'";
                            result = formObject.getDataFromDataSource(Query);
                            formObject.setNGValue("ij_quantity", "0.0");
                            formObject.setNGValue("ij_location", formObject.getNGValue("location"));
                            formObject.setNGValue("ij_sitecode", formObject.getNGValue("site"));
                            formObject.setNGValue("ij_warehousecode", formObject.getNGValue("warehouse"));
                            Query = "select concat(sitecode,'_',sitename) from SiteMaster "
                                    + "where SiteCode = '" + formObject.getNGValue("ij_sitecode") + "'";
                            result = formObject.getDataFromDataSource(Query);
                            formObject.setNGValue("ij_site", result.get(0).get(0));

                            Query = "select concat(cast(warehousecode as varchar),'_',warehousename) "
                                    + "from WarehouseMaster where WarehouseCode = '" + formObject.getNGValue("ij_warehousecode") + "'";
                            result = formObject.getDataFromDataSource(Query);
                            formObject.setNGValue("ij_warehouse", result.get(0).get(0));

                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_fetchPO":
                        System.out.println("Inside button click fetch PO");
                        //   String AccessToken = new CallAccessTokenService().getAccessToken();
                        new CallPurchaseOrderService().GetSetPurchaseOrder("", "Service", formObject.getNGValue("purchaseorder"), "RABill");

                        System.out.println(formObject.getNGValue("purchaseorder"));
                        formObject.setNGValue("PurchaseOrderNo", formObject.getNGValue("purchaseorder"));
                        System.out.println(formObject.getNGValue("contractorname"));
                        formObject.setNGValue("VendorName", formObject.getNGValue("contractorname"));
                        System.out.println(formObject.getNGValue("contractor"));
                        formObject.setNGValue("VendorCode", formObject.getNGValue("contractor"));
                        Query = "select WMSLocationId from WarehouseLocationMaster where WarehouseCode='" + formObject.getNGValue("warehouse") + "'";
                        result = formObject.getDataFromDataSource(Query);
                        System.out.println("result");
                        formObject.clear("location");
                        for (int i = 0; i < result.size(); i++) {
                            formObject.addComboItem("location", result.get(i).get(0).toString(), result.get(i).get(0).toString());
                        }

                        break;

                    case "Btn_Add_AbstractSheet":
                        System.out.println("inside Btn_Add_AbstractSheet");
                        String alertmsg = "";
                        boolean rowexist = false;
                        ListView ListViewq_raabstractsheet = (ListView) formObject.getComponent("q_raabstractsheet");
                        int RowCountq_raabstractsheet = ListViewq_raabstractsheet.getRowCount();
                        for (int j = 0; j < RowCountq_raabstractsheet; j++) {

                            if ((formObject.getNGValue("qpo_itemnumber").equalsIgnoreCase(formObject.getNGValue("q_raabstractsheet", j, 1)))
                                    && (formObject.getNGValue("qpo_structurecode").equalsIgnoreCase(formObject.getNGValue("q_raabstractsheet", j, 2)))) {
                                rowexist = true;
                                alertmsg = "Same Line No already added";
                                break;
                            }
                        }

                        if (rowexist) {
                            throw new ValidatorException(new FacesMessage(alertmsg, ""));
                        } else {
                            BigDecimal netamount = new BigDecimal(formObject.getNGValue("qpo_netamount")).setScale(2, BigDecimal.ROUND_FLOOR);
                            System.out.println("netamount : " + netamount);
                            BigDecimal unitprice = new BigDecimal(formObject.getNGValue("qpo_unitprice")).setScale(2, BigDecimal.ROUND_FLOOR);
                            System.out.println("unitprice : " + unitprice);
                            BigDecimal receivedqty = new BigDecimal(formObject.getNGValue("qpo_recievedqty")).setScale(2, BigDecimal.ROUND_FLOOR);
                            System.out.println("receivedqty : " + receivedqty);
                            BigDecimal deliveredValue = unitprice.multiply(receivedqty).setScale(2, BigDecimal.ROUND_FLOOR);
                            System.out.println("deliveredValue : " + deliveredValue);
                            String AbstractsheetXML = "";
                            AbstractsheetXML = (new StringBuilder()).append(AbstractsheetXML).
                                    append("<ListItem><SubItem>").append(formObject.getNGValue("qpo_linenumber")). //Line Number
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_itemnumber")). //Item Number
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_structurecode")). //Structure Code
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_structurename")). //Structre Name
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_linenumber")). //Item Selection
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_unitprice")). //Unit Price
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_quantity")). //Total Quantity
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_recievedqty")). //Delivered Quantity
                                    append("</SubItem><SubItem>").append(deliveredValue). //Delivered Amount
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_remainingqty")). //Remaining Quantity
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_currentquantity")). //Current Quantity
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_netamount")). //Net Amount
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("qpo_assessableamount")). //Assessable Amount   
                                    append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorder")). //po number  
                                    append("</SubItem></ListItem>").toString();

                            System.out.println("Abstract Sheet XML " + AbstractsheetXML);
                            formObject.NGAddListItem("q_raabstractsheet", AbstractsheetXML);
                            formObject.setNGValue("qpo_structurename", "");
                            formObject.setNGValue("qpo_structurecode", "");
                            formObject.setNGValue("qpo_netamount", "");
                            formObject.setNGValue("qpo_currentquantity", "");
                            formObject.RaiseEvent("WFSave");

                            //by farman
                            System.out.println("code by farman");
                            BigDecimal bookedamount = BigDecimal.ZERO;
                            System.out.println("bookedamount : " + bookedamount);
                            Query = "select SUM(cast(netamount as numeric(38,2))) from cmplx_raabstractsheet "
                                    + "where pinstanceid = '" + processInstanceId + "'";
                            System.out.println("Query :" + Query);
                            result = formObject.getDataFromDataSource(Query);
                            System.out.println("Result -> " + result.get(0).get(0));
                            if (null != result.get(0).get(0)) {
                                bookedamount = new BigDecimal(result.get(0).get(0)).add(netamount);
                            } else {
                                bookedamount = netamount;
                            }
                            System.out.println("bookedamount : " + bookedamount);
                            formObject.setNGValue("bookedamount", bookedamount);
                        }
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Delete_AbstractSheet":

                        BigDecimal currentbookedamount = new BigDecimal(formObject.getNGValue("bookedamount"));
                        BigDecimal linenetamount = new BigDecimal(formObject.getNGValue("qab_netamount"));
                        BigDecimal bookedamount = currentbookedamount.subtract(linenetamount);
                        formObject.setNGValue("bookedamount", bookedamount);
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_raabstractsheet");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Add_Itemjournal":
                        if (formObject.getNGValue("ij_quantity").equals("")
                                || formObject.getNGValue("ij_quantity").equals("0")
                                || formObject.getNGValue("ij_quantity").equals("0.0")
                                || formObject.getNGValue("ij_quantity").equals("0.00")) {
                            throw new ValidatorException(new FacesMessage("Quantity can not be zero"));
                        }

                        Query = "select count(*) from cmplx_raitemjournal where pinstanceid = '" + processInstanceId + "' "
                                + "and projectcode = '" + formObject.getNGValue("ij_projectcode") + "' "
                                + "and itemnumber = '" + formObject.getNGValue("ij_itemno") + "' "
                                + "and configuration = '" + formObject.getNGValue("ij_configuration") + "'";
                        System.out.println("Query : " + Query);
                        if ("0".equalsIgnoreCase(formObject.getDataFromDataSource(Query).get(0).get(0))) {
                            formObject.ExecuteExternalCommand("NGAddRow", "q_raitemjournal");
                            formObject.RaiseEvent("WFSave");
                        } else {
                            throw new ValidatorException(new FacesMessage("Line with same Project code, Item Number and Configuration exists"));
                        }
                        break;

                    case "Btn_Delete_Itemjournal":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_raitemjournal");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Modify_Itemjournal":
                        if (formObject.getNGValue("ij_quantity").equals("")
                                || formObject.getNGValue("ij_quantity").equals("0")
                                || formObject.getNGValue("ij_quantity").equals("0.0")
                                || formObject.getNGValue("ij_quantity").equals("0.00")) {
                            throw new ValidatorException(new FacesMessage("Quantity can not be zero"));
                        }

                        Query = "select count(*) from cmplx_raitemjournal where pinstanceid = '" + processInstanceId + "' "
                                + "and projectcode = '" + formObject.getNGValue("ij_projectcode") + "' "
                                + "and itemnumber = '" + formObject.getNGValue("ij_itemno") + "' "
                                + "and configuration = '" + formObject.getNGValue("ij_configuration") + "'";
                        System.out.println("Query : " + Query);
                        if ("0".equalsIgnoreCase(formObject.getDataFromDataSource(Query).get(0).get(0))) {
                            formObject.ExecuteExternalCommand("NGModifyRow", "q_raitemjournal");
                            formObject.RaiseEvent("WFSave");
                        } else {
                            throw new ValidatorException(new FacesMessage("Line with same Project code, Item Number and Configuration exists"));
                        }

                        break;

                    case "Btn_Validate_Itemjournal":
                        System.out.println("inside Btn_Validate_Itemjournal");
                        String AccessToken_1 = new CallAccessTokenService().getAccessToken();
                        new CallGetStockDetailsService().GetSetStockDetails(AccessToken_1, processInstanceId);
                        break;

                    case "Pick_structurename":
                        Query = "select ProjectCode,ProjectDesc from ProjectMaster";
                        objPicklistListenerHandler.openPickList("qpo_structurename", "ProjectCode,ProjectDesc", "Structure Master", 70, 70, Query);
                        break;

                    case "Pick_configuration":
                        if (formObject.getNGValue("ij_itemno").equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly select the Item Number", ""));
                        }
                        Query = "select ItemCode,CofigurationCode from ItemConfigurationMaster where ItemCode ='" + formObject.getNGValue("ij_itemno") + "'";
                        objPicklistListenerHandler.openPickList("ij_configuration", "Item Code,Configuration Code", "Configuration Master", 70, 70, Query);
                        break;

                    case "Pick_site":
                        Query = "select SiteCode,SiteName from SiteMaster order by SiteCode asc";
                        objPicklistListenerHandler.openPickList("ij_site", "Site Code,Site Name", "Site Master", 70, 70, Query);
                        break;

                    case "Pick_itemno":
                        Query = "select ItemCode,ItemShortDesc from RABillItemMaster order by ItemCode asc";
                        objPicklistListenerHandler.openPickList("ij_itemdesc", "Item Code,Item Description", "Item Number Master", 70, 70, Query);
                        break;

                    case "Pick_projectcategory":
                        Query = "select ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster order by ProjCategoryDesc asc";
                        objPicklistListenerHandler.openPickList("ij_projectcategory", "Code,Category", "Project Category Master", 70, 70, Query);
                        break;

                    case "Btn_Export_AbstractSheet":
                        objGeneral.openbamreport("AbstractSheet");
                        break;

                    case "Btn_Export_ItemJournal":
                        objGeneral.openbamreport("ItemJournal");
                        break;
                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 2: {
                                addProjectCode();
                            }
                            break;
                        }
                        break;
                }
                break;
        }
    }

    public void continueExecution(String string, HashMap<String, String> hm
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String encrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String decrypt(String string
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void addProjectCode() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formObject.clear("ij_projectcode");
        Query = "select distinct structurecode from cmplx_raabstractsheet "
                + "where pinstanceid = '" + processInstanceId + "' and structurecode is not null";
        result = formObject.getDataFromDataSource(Query);
        for (int j = 0; j < result.size(); j++) {
            formObject.addComboItem(
                    "ij_projectcode",
                    result.get(j).get(0),
                    result.get(j).get(0));
        }
    }
}
