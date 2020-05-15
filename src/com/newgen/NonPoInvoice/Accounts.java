/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.NonPoInvoice;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Accounts implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null, Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    AccountsGeneral objAccountsGeneral = null;
    Calculations objCalculations = null;
    PicklistListenerHandler objPicklistListenerHandler = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        objCalculations = new Calculations();
        objPicklistListenerHandler = new PicklistListenerHandler();
        objCalculations = new Calculations();
        //objGeneral = new General();
        objAccountsGeneral = new AccountsGeneral();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {

                    case "invoiceamount":
                    case "exchangerateotherthaninr":
                        objCalculations.exronBaseamountandExchangerateChange(
                                "currency",
                                "invoiceamount",
                                "newbaseamount",
                                "exchangerateotherthaninr"
                        );
                        String invoiceamount = formObject.getNGValue("invoiceamount");
                        System.out.println("got the base amount " + invoiceamount);
                        break;

                    case "currency":
                        objCalculations.exronCurrencyChange(
                                "currency",
                                "invoiceamount",
                                "newbaseamount",
                                "exchangerateotherthaninr"
                        );
                        break;

                    case "q_ledgeradjustedoriginamount":
                        BigDecimal bq_ledgeramount = new BigDecimal(formObject.getNGValue("q_ledgeramount"));
                        BigDecimal badjustedoriginamount = new BigDecimal(formObject.getNGValue("q_ledgeradjustedoriginamount"));
                        System.out.println("bq_ledgeramount: " + bq_ledgeramount);
                        System.out.println("badjustedoriginamount :" + badjustedoriginamount);
                        if (badjustedoriginamount.compareTo(bq_ledgeramount) > 0) {
                            throw new ValidatorException(new FacesMessage("The Adjusted Origin Amount can not be greater than Ledger Amount "));
                        } else {
                            System.out.println("inside else");
                            String calculatedValue = objCalculations.calculatePercentAmount(
                                    formObject.getNGValue("q_ledgeradjustedoriginamount"),
                                    formObject.getNGValue("q_ledgertdspercent")
                            );
                            formObject.setNGValue("q_ledgertdsamount", calculatedValue);
//                            formObject.setNGValue("q_ledgeradjustmenttdsamount", calculatedValue);
                            formObject.setNGValue("q_ledgeradjustmenttdsamount", new BigDecimal(calculatedValue).setScale(0, BigDecimal.ROUND_HALF_UP));
                        }
                        break;

//                    case "q_ledgeradjustmenttdsamount":
//                        BigDecimal q_ledgeradjustmenttdsamount = new BigDecimal(formObject.getNGValue("q_ledgeradjustmenttdsamount"));
//                        q_ledgeradjustmenttdsamount = q_ledgeradjustmenttdsamount.setScale(0, BigDecimal.ROUND_HALF_UP);
//                        formObject.setNGValue("q_ledgeradjustmenttdsamount", q_ledgeradjustmenttdsamount);
//                        break;
                    case "tdsadjustedbaseamount":
                        String calculatedTDSValue = objCalculations.calculatePercentAmount(
                                formObject.getNGValue("tdsadjustedbaseamount"),
                                formObject.getNGValue("tdspercent")
                        );
                        formObject.setNGValue("tdsamount", calculatedTDSValue);
                        formObject.setNGValue("tdsadjustedamount", calculatedTDSValue);
                        break;

                    case "qtd_taxamountadjustment":
                        String rcmamount = objCalculations.calculatePercentAmount(
                                formObject.getNGValue("qtd_taxamountadjustment"),
                                formObject.getNGValue("qtd_reversechargepercent")
                        );
                        System.out.println("RCM amount: " + rcmamount);
                        formObject.setNGValue("qtd_reversechargeamount", rcmamount);
                        break;

                    case "qtd_gstratetype":
                        if (formObject.getNGValue("qtd_gstratetype").equalsIgnoreCase("RCM")) {
                            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                                    formObject.getNGValue("qtd_hsnsactype"),
                                    formObject.getNGValue("qtd_hsnsaccode"),
                                    formObject.getNGValue("qtd_taxcomponent"),
                                    formObject.getNGValue("accounttype"),
                                    formObject.getNGValue("accountcode")
                            );
                            String reversechargeamount = objCalculations.calculatePercentAmount(
                                    formObject.getNGValue("qtd_taxamount"),
                                    reversechargerate
                            );
                            formObject.setNGValue("qtd_reversechargepercent", reversechargerate);
                            formObject.setNGValue("qtd_reversechargeamount", reversechargeamount);
                        } else {
                            formObject.setNGValue("qtd_reversechargepercent", "0.00");
                            formObject.setNGValue("qtd_reversechargeamount", "0.00");
                        }
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Add_LedgerLines":
                        if (formObject.getNGValue("q_ledgeraccount").equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly fill the Ledger account"));
                        }
                        if (formObject.getNGValue("q_ledgeramount").equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly fill the Amount"));
                        }
                        if (formObject.getNGValue("q_ledgerbusinessunit").equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly fill the Business unit"));
                        }
                        if (formObject.getNGValue("q_ledgerstate").equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly fill the State"));
                        }
//                        if (formObject.getNGValue("q_ledgercostcentergroup").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the Cost center group"));
//                        }
//                        if (formObject.getNGValue("q_ledgercostcenter").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the Cost center"));
//                        }
//                        if (formObject.getNGValue("q_ledgerdepartment").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the Department"));
//                        }
//                        if (formObject.getNGValue("q_ledgergla").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the GLA"));
//                        }
//                        if (formObject.getNGValue("q_ledgerwarehouse").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the Warehouse"));
//                        }
//                        if (formObject.getNGValue("q_ledgerrso").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the RSO"));
//                        }
//                        if (formObject.getNGValue("q_ledgertdsgroup").equalsIgnoreCase("")) {
//                            throw new ValidatorException(new FacesMessage("Kindly fill the TDS group"));
//                        }
                        System.out.println("Adj origin amount :" + formObject.getNGValue("q_ledgeradjustedoriginamount"));
                        if (!"".equalsIgnoreCase(formObject.getNGValue("q_ledgeradjustedoriginamount"))) {
                            if (new BigDecimal(formObject.getNGValue("q_ledgeradjustedoriginamount")).
                                    compareTo(new BigDecimal(formObject.getNGValue("q_ledgeramount"))) > 0) {
                                throw new ValidatorException(new FacesMessage("The Adjusted Origin Amount can not be greater than Ledger Amount"));
                            }
                        }

                        boolean rowexist = false;
                        ListView listview1 = (ListView) formObject.getComponent("q_ledgerlinedetails");
                        int rowcount1 = listview1.getRowCount();
                        for (int j = 0; j <= rowcount1; j++) {
                            if (formObject.getNGValue("q_ledgeraccount").
                                    equalsIgnoreCase(formObject.getNGValue("q_ledgerlinedetails", j, 0))) {
                                rowexist = true;
                                break;
                            }
                        }
                        if (rowexist) {
                            throw new ValidatorException(new FacesMessage("This Ledger Account has been already added to the listview"));
                        } else {
                            updateTDSvalue(); /*Add code to update tds value in case of change*/

                            formObject.ExecuteExternalCommand("NGAddRow", "q_ledgerlinedetails");
//                            objAccountsGeneral.refreshTaxDocument(processInstanceId);
                            formObject.RaiseEvent("WFSave");
                        }
                        break;

                    case "Btn_Modify_LedgerLines":
                        updateTDSvalue();
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_ledgerlinedetails");
                        objAccountsGeneral.refreshTaxDocument(processInstanceId);

                        formObject.setSelectedSheet("Tab1", 3);
                        formObject.RaiseEvent("WFSave");
                        throw new ValidatorException(new FacesMessage("Please check tax document, as tax document does get refreshed"));
                    // break;

                    case "Btn_Delete_LedgerLines":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_ledgerlinedetails");
                        objAccountsGeneral.refreshTaxDocument(processInstanceId);
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Modify_Taxdocument":
                        System.out.println("inside modify of tax document");

                        sgst_cgst(formObject.getSelectedIndex("q_taxdocument"), formObject.getNGValue("qtd_ledgeraccount"),
                                formObject.getNGValue("qtd_taxcomponent"));
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_taxdocument");
                        System.out.println("Modified method called");
                        formObject.RaiseEvent("WFSave");
                        break;

                    case "Btn_Applytoall":
                        ListView ListViewq_taxdocument = (ListView) formObject.getComponent("q_taxdocument");
                        int rowcount = ListViewq_taxdocument.getRowCount();
                        for (int i = 0; i < rowcount; i++) {
                            applyTaxToAll(
                                    i,
                                    formObject.getNGValue("q_taxdocument", i, 1),
                                    formObject.getNGValue("q_taxdocument", i, 6));
                        }
                        break;

                    case "Btn_Resolve":
                        objAccountsGeneral.setResolveAXException();
                        break;

//                    case "Pick_journalname":
//                        System.out.println("Inside pick journal");
//                        Query = "select code,Description from JournalNamemaster order by code asc";
//                        System.out.println("Query :" + Query);
//                        objPicklistListenerHandler.openPickList("journalname", "Code,Description", "Journal Master", 70, 70, Query);
//                        break;
//                    case "Pick_paymentterm":
//                        Query = "select PaymentTermCode,PaymentTermDesc from PaymentTermMaster";
//                        objPicklistListenerHandler.openPickList("paymentterm", "Code,Description", "Payment Term Master", 70, 70, Query);
//                        break;
                    case "Pick_ledgeraccount":
                        Query = "select AccountId,Description from LedgerACMaster order by AccountId asc";
                        objPicklistListenerHandler.openPickList("q_ledgeraccountdesc", "Account ID,Description", "Ledger Account Master", 70, 70, Query);
                        setTDSdata();
                        BigDecimal linesum = getLedgerLineAmount();
                        if (linesum.compareTo(BigDecimal.valueOf(0)) > 0
                                || null != linesum) {
                            System.out.println("Line sum :" + linesum);
                            System.out.println("Base : " + new BigDecimal(formObject.getNGValue("baseamount")));
                            BigDecimal diff = new BigDecimal(formObject.getNGValue("baseamount")).subtract(linesum);
                            System.out.println("inside difference of two values" + diff);
                            formObject.setNGValue("q_ledgeramount", diff);
                        } else {
                            formObject.setNGValue("q_ledgeramount", formObject.getNGValue("baseamount"));
                        }

                        //set header business unit, state and department
                        System.out.println("departmentdsc" + formObject.getNGValue("departmentdsc"));
                        formObject.setNGValue("q_ledgerdepartment", formObject.getNGValue("departmentdsc"));
                        formObject.setNGValue("q_ledgerdepartmentvalue", formObject.getNGValue("department"));

                        String site = formObject.getNGValue("site");
                        Query = "select SiteName from SiteMaster where SiteCode =  '" + site + "'";
                        System.out.println("Query" + Query);
                        result = formObject.getDataFromDataSource(Query);
                        if (result.size() > 0) {
                            formObject.setNGValue("q_ledgerbusinessunit", site + "-" + result.get(0).get(0));
                            formObject.setNGValue("q_ledgerbusinessunitcode", site);
                        }

                        if (site.equalsIgnoreCase("104")) {
                            formObject.setEnabled("q_ledgerstate", true);
                        } else {
                            formObject.setEnabled("q_ledgerstate", false);
                        }

                        formObject.setNGValue("q_ledgerstate", formObject.getNGValue("state"));

                        break;

                    case "Pick_ledgerbusinessunit":
                        System.out.println("inside if");
                        Query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
                        objPicklistListenerHandler.openPickList("q_ledgerbusinessunit", "Code, Name", "Business Unit Master", 70, 70, Query);

                        break;

                    case "Pick_ledgerstate":
                        String q_ledgerbusinessunit = formObject.getNGValue("q_ledgerbusinessunit");
                        if (q_ledgerbusinessunit.equalsIgnoreCase("")) {
                            throw new ValidatorException(new FacesMessage("Kindly select the business unit value"));
                        } else {
                            Query = "select statecode,statename from statemaster where businessunitcode = '" + q_ledgerbusinessunit + "' order by statecode asc";
                            objPicklistListenerHandler.openPickList("q_ledgerstate", "Code, Name", "Business Unit Master", 70, 70, Query);
                        }
                        break;

                    case "Pick_ledgercostcenter":
                        Query = "select value, Description from CostCenter order by value asc";
                        objPicklistListenerHandler.openPickList("q_ledgercostcenter", "Value,Description", "Cost Center Master", 70, 70, Query);
                        break;

                    case "Pick_ledgerdepartment":
                        Query = "select Value,Description from Department order by Value asc";
                        objPicklistListenerHandler.openPickList("q_ledgerdepartment", "Value,Description", "Department Master", 70, 70, Query);
                        break;

                    case "Pick_ledgergla":
                        Query = "select Value,Description from GLAMaster order by Value asc";
                        objPicklistListenerHandler.openPickList("q_ledgergla", "Value,Description", "GLA Master", 70, 70, Query);
                        break;

                    case "Pick_ledgertdsgroup":
                        boolean tdsapplicable = true;
                        if (formObject.getNGValue("accounttype").equalsIgnoreCase("Vendor")) {
                            Query = "select calculatewithholdingtax from VendorMaster where VendorCode = '" + formObject.getNGValue("accountcode") + "'";
                            if (formObject.getDataFromDataSource(Query).get(0).get(0).equalsIgnoreCase("0")) {
                                formObject.setEnabled("q_ledgeradjustedoriginamount", false);
                                formObject.setEnabled("q_ledgeradjustmenttdsamount", false);
                                formObject.setEnabled("Pick_ledgertdsgroup", false);
                                tdsapplicable = false;
                            } else {
                                formObject.setEnabled("q_ledgeradjustedoriginamount", true);
                                formObject.setEnabled("q_ledgeradjustmenttdsamount", true);
                                formObject.setEnabled("Pick_ledgertdsgroup", true);
                                tdsapplicable = true;
                            }
                        }

                        if (tdsapplicable) {
                            Query = "select Code,Description from TDSMaster order by Code asc";
                            objPicklistListenerHandler.openPickList("q_ledgertdsgroup", "Code,Description", "TDS Group Master", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("TDS is not applicable for the selected account"));
                        }
                        break;

                    case "Pick_withholdingtds":
                        Query = "select Code,Description from TDSMaster order by Code asc";
                        objPicklistListenerHandler.openPickList("tdsgroup", "Code,Description", "TDS Group Master", 70, 70, Query);
                        break;

                    case "Pick_ledgerwarehouse":
                        Query = "select warehousecode,warehousename from warehousemaster order by warehousecode asc";
                        objPicklistListenerHandler.openPickList("q_ledgerwarehouse", "Code,Name", "Warehouse Master", 70, 70, Query);
                        break;

                    case "Pick_ledgerrso":
                        Query = "select rsocode, rsoname from rsomaster order by rsocode asc";
                        objPicklistListenerHandler.openPickList("q_ledgerrso", "Code,Name", "RSO Master", 70, 70, Query);
                        break;

                    case "Pick_hsnsac":
                        String hsnsaccodetype = formObject.getNGValue("qtd_hsnsactype");
                        if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
                            Query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                            objPicklistListenerHandler.openPickList("qtd_hsnsacdescription", "Code,Description", "HSN Master", 70, 70, Query);
                        } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
                            Query = "select SACCode,Description from SACMaster order by SACCode asc";
                            objPicklistListenerHandler.openPickList("qtd_hsnsacdescription", "Code,Description", "SAC Master", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the type value"));
                        }
                        break;

                    case "Pick_companylocation":
                        Query = "select StateName,AddressId,Address,AddressName  from AddressMaster "
                                + "where AddressType = 'Company'";
                        objPicklistListenerHandler.openPickList("customerlocation", "State,Address Id,Address,Address Name", "Address Master", 70, 70, Query);
                        break;

                    case "Pick_vendorlocation":
                        Query = "select StateName, AddressId, GSTINNumber,Address,AddressName from AddressMaster "
                                + "where AddressType = 'Vendor' and PartyCode = '" + formObject.getNGValue("accountcode") + "'";
                        objPicklistListenerHandler.openPickList("vendorlocation", "State,Address Id,GSTIN Number,Address,Address Name", "Address Master", 70, 70, Query);
                        break;

                    case "Pick_department":
                        System.out.println("inside Pick_department");
                        Query = "select value,description from department order by description asc";
                        objPicklistListenerHandler.openPickList("departmentdsc", "Code,Description", "Department Master", 35, 35, Query);
                        break;

                    case "Pick_account":
                        System.out.println("inside Pick_account");
                        String accounttype = formObject.getNGValue("accounttype");
                        if (accounttype.equalsIgnoreCase("Vendor")) {
                            Query = "select VendorCode,VendorName from VendorMaster order by VendorCode asc";
                            objPicklistListenerHandler.openPickList("account", "Code,Name", "Vendor Master", 70, 70, Query);
                        } else if (accounttype.equalsIgnoreCase("Customer")) {
                            Query = "select Code,Description from CustomerMaster order by Code asc";
                            objPicklistListenerHandler.openPickList("account", "Code,Name", "Customer Master", 70, 70, Query);
                        } else {
                            throw new ValidatorException(new FacesMessage("Kindly select the account type value"));
                        }
                        break;

                    case "Pick_journalname":
                        System.out.println("Inside pick journal");
                        Query = "select code,Description from JournalNamemaster order by code asc";
                        System.out.println("Query :" + Query);
                        objPicklistListenerHandler.openPickList("journalname", "Code,Description", "Journal Master", 70, 70, Query);
                        break;

                    case "Pick_paymentterm":
                        System.out.println("inside Pick_paymentterm");
                        Query = "select PaymentTermCode,PaymentTermDesc from PaymentTermMaster";
                        objPicklistListenerHandler.openPickList("paymentterm", "Code,Description", "Payment Term Master", 70, 70, Query);
                        break;

                    case "Clear_ledgerdepartment":
                        formObject.clear("q_ledgerdepartment");
                        formObject.clear("q_ledgerdepartmentvalue");
                        break;

                    case "Clear_ledgergla":
                        formObject.clear("q_ledgergla");
                        formObject.clear("q_ledgerglavalue");
                        break;

                    case "Clear_ledgerwarehouse":
                        formObject.clear("q_ledgerwarehouse");
                        formObject.clear("q_ledgerwarehousecode");
                        break;

                    case "Clear_ledgerrso":
                        formObject.clear("q_ledgerrso");
                        formObject.clear("q_ledgerrsocode");
                        break;

                    case "Clear_ledgertdsgroup":
                        formObject.clear("q_ledgertdsgroup");
                        formObject.clear("q_ledgertdsgroupcode");
                        break;

                    case "qtd_exempt":
                        String exempt = formObject.getNGValue("qtd_exempt");
                        if (exempt.equalsIgnoreCase("true")) {
                            formObject.setNGValue("qtd_taxamount", "0");
                            formObject.setNGValue("qtd_taxamountadjustment", "0");
                        } else {
                            String Query = "select amount from cmplx_ledgerlinedetails where "
                                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                                    + "and ledgeraccount = '" + formObject.getNGValue("qtd_ledgeraccount") + "'";
                            System.out.println("Query :" + Query);
                            String taxamount = objCalculations.calculatePercentAmount(
                                    formObject.getDataFromDataSource(Query).get(0).get(0),
                                    formObject.getNGValue("qtd_taxrate")
                            );
                            formObject.setNGValue("qtd_taxamount", taxamount);
                            formObject.setNGValue("qtd_taxamountadjustment", taxamount);

                        }
                        break;
                }
                break;

            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Tab1":
                        switch (formObject.getSelectedSheet("Tab1")) {
                            case 3: {
                                String vendorstate = formObject.getNGValue("vendorstate");
                                String customerstate = formObject.getNGValue("customerstate");
                                if (!vendorstate.equalsIgnoreCase("") && !customerstate.equalsIgnoreCase("")) {
                                    String taxcomponent = "", taxrate = "", taxamount = "", reversechargerate = "", reversechargeamount = "", TaxDocumentXML = "";
                                    Query = "select ledgeraccount from cmplx_ledgerlinedetails where pinstanceid = '" + processInstanceId + "'";
                                    System.out.println("Query :" + Query);
                                    result = formObject.getDataFromDataSource(Query);
                                    if (result.size() > 0) {
                                        for (int j = 0; j < result.size(); j++) {
                                            Query = "select count(*) from cmplx_taxdocument where pinstanceid = '" + processInstanceId + "' "
                                                    + "and itemnumber = '" + result.get(j).get(0) + "'";
                                            System.out.println("Query :" + Query);
                                            List<List<String>> resultcount = formObject.getDataFromDataSource(Query);
                                            if (resultcount.get(0).get(0).equals("0")) {
                                                if (vendorstate.equalsIgnoreCase(customerstate)) {
                                                    for (int i = 0; i < 2; i++) {
                                                        System.out.println("before tax component");
                                                        if (i == 0) {
                                                            System.out.println("Inside 0");
                                                            taxcomponent = "SGST";
                                                        } else {
                                                            System.out.println("Inside else 0");
                                                            taxcomponent = "CGST";
                                                        }

                                                        TaxDocumentXML = (new StringBuilder()).append(TaxDocumentXML).
                                                                append("<ListItem><SubItem>").append(""). //line number
                                                                append("</SubItem><SubItem>").append(result.get(j).get(0)). //item number
                                                                append("</SubItem><SubItem>").append(formObject.getNGValue("vendorgstingdiuid")). //gstingdiuid
                                                                append("</SubItem><SubItem>").append(""). //hsnsac type
                                                                append("</SubItem><SubItem>").append(""). //hsnsac code
                                                                append("</SubItem><SubItem>").append(""). //hsnsac description
                                                                append("</SubItem><SubItem>").append(taxcomponent). //tax component
                                                                append("</SubItem><SubItem>").append(taxrate). //rate
                                                                append("</SubItem><SubItem>").append(taxamount). //tax amount
                                                                append("</SubItem><SubItem>").append(taxamount). //adjustment tax amount
                                                                append("</SubItem><SubItem>").append(""). //non business usage %
                                                                append("</SubItem><SubItem>").append(reversechargerate). //reverse charge %
                                                                append("</SubItem><SubItem>").append(reversechargeamount). //reverse charge amount
                                                                append("</SubItem><SubItem>").append(""). //GST Rate Type
                                                                append("</SubItem><SubItem>").append("false"). //exempt
                                                                append("</SubItem></ListItem>").toString();
                                                    }

                                                } else {
                                                    taxcomponent = "IGST";
                                                    TaxDocumentXML = (new StringBuilder()).append(TaxDocumentXML).
                                                            append("<ListItem><SubItem>").append(""). //line number
                                                            append("</SubItem><SubItem>").append(result.get(j).get(0)). //item number
                                                            append("</SubItem><SubItem>").append(formObject.getNGValue("vendorgstingdiuid")). //gstingdiuid
                                                            append("</SubItem><SubItem>").append(""). //hsnsac type
                                                            append("</SubItem><SubItem>").append(""). //hsnsac code
                                                            append("</SubItem><SubItem>").append(""). //hsnsac description
                                                            append("</SubItem><SubItem>").append(taxcomponent). //tax component
                                                            append("</SubItem><SubItem>").append(taxrate). //rate
                                                            append("</SubItem><SubItem>").append(taxamount). //tax amount
                                                            append("</SubItem><SubItem>").append(taxamount). //adjustment tax amount
                                                            append("</SubItem><SubItem>").append(""). //non business usage %
                                                            append("</SubItem><SubItem>").append(reversechargerate). //reverse charge %
                                                            append("</SubItem><SubItem>").append(reversechargeamount). //reverse charge amount
                                                            append("</SubItem><SubItem>").append(""). //GST Rate Type
                                                            append("</SubItem><SubItem>").append("false"). //exempt
                                                            append("</SubItem></ListItem>").toString();
                                                }
                                            }
                                        }
                                        System.out.println("Tax Document XML " + TaxDocumentXML);
                                        formObject.NGAddListItem("q_taxdocument", TaxDocumentXML);
                                        formObject.RaiseEvent("WFSave");
                                    }
                                } else {
                                    formObject.setSelectedSheet("Tab1", 0);
                                    throw new ValidatorException(new FacesMessage("Kindly fill company and vendor tax information"));
                                }
//                                ListView ListViewq_taxdocument = (ListView) formObject.getComponent("q_taxdocument");
//                                int rowCount = ListViewq_taxdocument.getRowCount();
//                                if (rowCount == 0) {
//                                }
                            }
                            break;
                            case 2: {
                                String vendorloc = formObject.getNGValue("vendorlocation");
                                System.out.println("Vendor Loc: " + vendorloc);
                                Query = "select AddressId, StateName, Address, GSTINNumber, AddressName "
                                        + "from AddressMaster where PartyCode ='" + formObject.getNGValue("accountcode") + "'";
                                System.out.println("Query for Vendor" + Query);
                                result = formObject.getDataFromDataSource(Query);
                                System.out.println("result for vendor is " + result);
                                if (result.size() > 0) {
                                    formObject.setNGValue("vendorlocation", result.get(0).get(0));
                                    formObject.setNGValue("vendorstate", result.get(0).get(1));
                                    formObject.setNGValue("vendoraddress", result.get(0).get(2));
                                    formObject.setNGValue("vendorgstingdiuid", result.get(0).get(3));
                                    formObject.setNGValue("vendortaxinformation", result.get(0).get(4));
                                } else {
                                    formObject.setNGValue("vendorlocation", "");
                                    formObject.setNGValue("vendorstate", "");
                                    formObject.setNGValue("vendoraddress", "");
                                    formObject.setNGValue("vendorgstingdiuid", "");
                                    formObject.setNGValue("vendortaxinformation", "");
                                }
                            }
                            break;

                        }
                        break;

                    case "Tab2":
                        switch (formObject.getSelectedSheet("Tab2")) {
                            case 3: {
                                objAccountsGeneral.getsetServiceNonPoSummary(processInstanceId);
                            }
                        }
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
            engineName = formConfig.getConfigElement("EngineName");
            sessionId = formConfig.getConfigElement("DMSSessionId");
            folderId = formConfig.getConfigElement("FolderId");
            serverUrl = formConfig.getConfigElement("ServletPath");
            activityName = formObject.getWFActivityName();
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
            System.out.println("processDefId====" + processDefId);
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("----------------------Intiation Workstep Loaded from form populated........---------------------------");
        objGeneral = new General();

        formObject.clear("filestatus");
        formObject.setNGValue("filestatus", "");
        formObject.addComboItem("filestatus", "Approve", "Approve");
        formObject.addComboItem("filestatus", "Reject", "Reject");
        formObject.addComboItem("filestatus", "Query Raise", "Query Raise");
        formObject.addComboItem("filestatus", "Discard", "Discard");

        String currentdate = objGeneral.getCurrentDate();
        if ("".equalsIgnoreCase(formObject.getNGValue("postingdate"))) {
            formObject.setNGValue("postingdate", currentdate);
        }
        if ("".equalsIgnoreCase(formObject.getNGValue("duedate"))) {
            formObject.setNGValue("duedate", currentdate);
        }

        Query = "select HeadName from ServiceNonPoHeadMaster order by HeadName asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("proctype", result.get(i).get(0), result.get(i).get(0));
        }

        Query = "select sitecode from sitemaster order by sitecode asc";
        System.out.println("Query is " + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < result.size(); i++) {
            formObject.addComboItem("site", result.get(i).get(0), result.get(i).get(0));
        }

        formObject.setNGDateRange("invoicedate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("postingdate", null, new Date(objGeneral.getCurrDateForRange()));
        formObject.setNGDateRange("duedate", new Date(objGeneral.getCurrDateForRange()), null);
    }

    @Override
    public void saveFormCompleted(FormEvent arg0
    ) throws ValidatorException {
        // TODO Auto-generated method stub
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0
    ) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormCompleted(FormEvent arg0
    ) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormStarted(FormEvent arg0
    ) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        objGeneral.compareDate(formObject.getNGValue("invoicedate"), formObject.getNGValue("postingdate"));
        objAccountsGeneral.getsetServiceNonPoSummary(processInstanceId);
        BigDecimal baseamount = new BigDecimal(formObject.getNGValue("baseamount")).setScale(2, BigDecimal.ROUND_FLOOR);
        BigDecimal linesum = getLedgerLineAmount();
        System.out.println("Baseamount " + baseamount);
        System.out.println("Line sum " + linesum);
        if (linesum.compareTo(baseamount) == 0) {
            String filestatus = formObject.getNGValue("filestatus");
            if (activityName.equalsIgnoreCase("AccountsMaker")) {
                if (filestatus.equalsIgnoreCase("Approve")) {
                    objGeneral.checkServiceNonPoDoAUser("AccountsChecker", "");
                } else if (filestatus.equalsIgnoreCase("Reject")) {
                    Query = "select top 1 ApproverLevel from ServiceNonPOApproverMaster "
                            + "where head = '" + formObject.getNGValue("proctype") + "' "
                            + "and site = '" + formObject.getNGValue("site") + "' "
                            + "and state = '" + formObject.getNGValue("state") + "' "
                            + "and department = '" + formObject.getNGValue("department") + "'"
                            + "and ApproverLevel not in ('AccountsMaker','AccountsChecker') order by ApproverLevel desc";
                    System.out.println("Query :" + Query);
                    result = formObject.getDataFromDataSource(Query);
                    formObject.setNGValue("FilterDoA_ApproverLevel", result.get(0).get(0));
                    formObject.setNGValue("levelflag", result.get(0).get(0));
                }
            } else if (activityName.equalsIgnoreCase("AccountsChecker")) {
                if (filestatus.equalsIgnoreCase("Reject")) {
                    formObject.setNGValue("FilterDoA_ApproverLevel", "AccountsMaker");
                    formObject.setNGValue("levelflag", "AccountsMaker");
                }
                formObject.setNGValue("accountschecker", userName);
            } else if (activityName.equalsIgnoreCase("AXSyncException")) {
                Query = "select count(*) from cmplx_axintegration_error where "
                        + "resolve = 'False' and pinstanceid = '" + processInstanceId + "'";
                if (!formObject.getDataFromDataSource(Query).get(0).get(0).equals("0")) {
                    throw new ValidatorException(new FacesMessage("Kindly resolve all the errors to proceed further"));
                }
            }

            formObject.setNGValue("previousactivity", activityName);
            objGeneral.maintainHistory(
                    userName,
                    activityName,
                    formObject.getNGValue("filestatus"),
                    "",
                    formObject.getNGValue("Text51"),
                    "q_transactionhistory"
            );
        } else {
            throw new ValidatorException(new FacesMessage("Base amount and sum of ledger line should be equal"));
        }
    }

    BigDecimal getLedgerLineAmount() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal linesum = BigDecimal.valueOf(0);
        ListView listview = (ListView) formObject.getComponent("q_ledgerlinedetails");
        int rowcount = listview.getRowCount();
        if (rowcount > 0) {
            ArrayList<String> amount = new ArrayList<>();
            for (int i = 0; i < rowcount; i++) {
                amount.add(formObject.getNGValue("q_ledgerlinedetails", i, 2));
            }
            linesum = objCalculations.calculateSum(amount).setScale(2, BigDecimal.ROUND_FLOOR);
            System.out.println("Line sum :" + linesum);
        }
        return linesum;
    }

    void setTDSdata() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formObject.setNGValue("q_ledgertdsgroup", formObject.getNGValue("q_ledgerlinedetails", 0, 11));
        formObject.setNGValue("q_ledgertdspercent", formObject.getNGValue("q_ledgerlinedetails", 0, 12));
        formObject.setNGValue("q_ledgertdsgroupcode", formObject.getNGValue("q_ledgerlinedetails", 0, 21));
    }

    void updateTDSvalue() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        ListView LVq_ledgerlinedetails = (ListView) formObject.getComponent("q_ledgerlinedetails");
        int RCq_ledgerlinedetails = LVq_ledgerlinedetails.getRowCount();
        if (RCq_ledgerlinedetails > 0) {
            String newq_ledgertdsgroupcode = formObject.getNGValue("q_ledgertdsgroupcode");
            if (!newq_ledgertdsgroupcode.equalsIgnoreCase(formObject.getNGValue("q_ledgerlinedetails", 0, 21))) {
                System.out.println("Row count : " + RCq_ledgerlinedetails);
                String newq_ledgertdsgroup = formObject.getNGValue("q_ledgertdsgroup");
                String newq_ledgertdspercent = formObject.getNGValue("q_ledgertdspercent");

                for (int j = 0; j < RCq_ledgerlinedetails; j++) {
                    System.out.println("Int J: " + j);
                    if (!newq_ledgertdsgroupcode.equalsIgnoreCase(formObject.getNGValue("q_ledgerlinedetails", j, 21))) {
                        formObject.setNGValue("q_ledgerlinedetails", j, 11, newq_ledgertdsgroup);
                        formObject.setNGValue("q_ledgerlinedetails", j, 12, newq_ledgertdspercent);
                        formObject.setNGValue("q_ledgerlinedetails", j, 21, newq_ledgertdsgroupcode);
                        String amount = formObject.getNGValue("q_ledgerlinedetails", j, 2);
                        String calculatedTDSValue = objCalculations.calculatePercentAmount(
                                formObject.getNGValue("q_ledgerlinedetails", j, 2),
                                newq_ledgertdspercent
                        );
                        System.out.println("calculatedTDSValue :" + calculatedTDSValue);
                        formObject.setNGValue("q_ledgerlinedetails", j, 14, calculatedTDSValue);
                        formObject.setNGValue("q_ledgerlinedetails", j, 15, calculatedTDSValue);
                    }
                }
                formObject.setVisible("err_tdschange", false);
            }
        }
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String decrypt(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void applyTaxToAll(int RowIndex, String LedgerAccount, String TaxComponent) {
        String nonbusinessusagepercent = "";
        String Query = "select amount from cmplx_ledgerlinedetails where "
                + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                + "and ledgeraccount = '" + LedgerAccount + "'";

        String taxamount = objCalculations.calculatePercentAmount(
                formObject.getDataFromDataSource(Query).get(0).get(0),
                formObject.getNGValue("qtd_taxrate")
        );

        String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                formObject.getNGValue("qtd_hsnsactype"),
                formObject.getNGValue("qtd_hsnsaccode"),
                TaxComponent,
                formObject.getNGValue("accounttype"),
                formObject.getNGValue("accountcode")
        );
        String reversechargeamount = objCalculations.calculatePercentAmount(
                taxamount,
                reversechargerate
        );

        if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("HSN")) {
            Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from HSNRateMaster where hsncode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";

        } else if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("SAC")) {
            Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from SACRateMaster where saccode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";
        }
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("CGST")
                    && ("0.00".equalsIgnoreCase(result.get(0).get(0))
                    || "0.0".equalsIgnoreCase(result.get(0).get(0))
                    || "0".equalsIgnoreCase(result.get(0).get(0))
                    || null == result.get(0).get(0)
                    || "NULL".equalsIgnoreCase(result.get(0).get(0))
                    || "Null".equalsIgnoreCase(result.get(0).get(0)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }

            if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("SGST")
                    && ("0.00".equalsIgnoreCase(result.get(0).get(1))
                    || "0.0".equalsIgnoreCase(result.get(0).get(1))
                    || "0".equalsIgnoreCase(result.get(0).get(1))
                    || null == result.get(0).get(1)
                    || "NULL".equalsIgnoreCase(result.get(0).get(1))
                    || "Null".equalsIgnoreCase(result.get(0).get(1)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }

            if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("IGST")
                    && (!"0.00".equalsIgnoreCase(result.get(0).get(2))
                    || !"0.0".equalsIgnoreCase(result.get(0).get(2))
                    || !"0".equalsIgnoreCase(result.get(0).get(2))
                    || null == result.get(0).get(2)
                    || "NULL".equalsIgnoreCase(result.get(0).get(2))
                    || "Null".equalsIgnoreCase(result.get(0).get(2)))) {
                nonbusinessusagepercent = "";
            } else {
                nonbusinessusagepercent = result.get(0).get(0);
            }
        }
        formObject.setNGValue("q_taxdocument", RowIndex, 3, formObject.getNGValue("qtd_hsnsactype"));
        formObject.setNGValue("q_taxdocument", RowIndex, 4, formObject.getNGValue("qtd_hsnsaccode"));
        formObject.setNGValue("q_taxdocument", RowIndex, 7, formObject.getNGValue("qtd_taxrate"));
        formObject.setNGValue("q_taxdocument", RowIndex, 8, taxamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 9, taxamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 10, nonbusinessusagepercent);
        formObject.setNGValue("q_taxdocument", RowIndex, 11, reversechargerate);
        formObject.setNGValue("q_taxdocument", RowIndex, 12, reversechargeamount);
        formObject.setNGValue("q_taxdocument", RowIndex, 13, formObject.getNGValue("qtd_gstratetype"));
        formObject.setNGValue("q_taxdocument", RowIndex, 14, formObject.getNGValue("qtd_exempt"));

    }

    public void sgst_cgst(int rowIndex, String LedgerAccount, String TaxComponent) {
        System.out.println("Inside sgst_cgst method");
        String TaxComponent1 = "", TaxDocumentXML1 = "", Line_no = "";
        if (TaxComponent.equalsIgnoreCase("SGST") || TaxComponent.equalsIgnoreCase("CGST")) {
            if (TaxComponent.equalsIgnoreCase("SGST")) {
                TaxComponent1 = "CGST";
                Line_no = String.valueOf(rowIndex + 1);
            } else {
                TaxComponent1 = "SGST";
                Line_no = String.valueOf(rowIndex - 1);
            }
            System.out.println("Updating Listview for " + TaxComponent1 + "");
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 0, "");//line number
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 1, LedgerAccount); //item number
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 2, formObject.getNGValue("vendorgstingdiuid")); //gstingdiuid
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 3, formObject.getNGValue("qtd_hsnsactype")); //hsnsac type
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 4, formObject.getNGValue("qtd_hsnsaccode")); //hsnsac code
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 5, formObject.getNGValue("qtd_hsnsacdescription")); //hsnsac description
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 6, TaxComponent1); //tax component
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 7, formObject.getNGValue("qtd_taxrate")); //rate
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 8, formObject.getNGValue("qtd_taxamount")); //tax amount
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 9, formObject.getNGValue("qtd_taxamountadjustment")); //adjustment tax amount
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 10, formObject.getNGValue("qtd_nonbusinessusagepercent")); //non business usage %
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 11, formObject.getNGValue("qtd_reversechargepercent")); //reverse charge %
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 12, formObject.getNGValue("qtd_reversechargeamount")); //reverse charge amount
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 13, formObject.getNGValue("qtd_gstratetype")); //GST Rate Type
            formObject.setNGValue("q_taxdocument", Integer.parseInt(Line_no), 14, formObject.getNGValue("qtd_exempt"));

            System.out.println("Values updated");
        }
    }
}
