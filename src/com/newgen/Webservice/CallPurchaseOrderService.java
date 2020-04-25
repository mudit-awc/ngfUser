/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.Calculations;
import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author V_AWC
 */
public class CallPurchaseOrderService {

    FormReference formObject;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    ServiceConnection objServiceConnection = null;
    String webserviceStatus;

    public void GetSetPurchaseOrder(String AccessToken, String POType, String PONumber, String ProcessName) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objServiceConnection = new ServiceConnection();
        try {
            System.out.println("inside po class ");
            //System.out.println("Po AccessToken : " + AccessToken);
            System.out.println("Po POType : " + POType);
            System.out.println("Po PONumber : " + PONumber);
            objReadProperty = new ReadProperty();
            JSONObject request_json = new JSONObject();
            try {
                request_json.put("_POType", POType);
                request_json.put("_PONumber", PONumber);
                JSONArray parmPOParmList = new JSONArray();
                //
                if (POType.equalsIgnoreCase("Supply")) {
                    System.out.println("inside if of po");
                    ListView ListViewq_invoicedetails = (ListView) formObject.getComponent("q_gateentrylines");
                    int rowCount = ListViewq_invoicedetails.getRowCount();
                    System.out.println("Row count : " + rowCount);
                    if (rowCount > 0) {
                        for (int i = 0; i < rowCount; i++) {
                            JSONObject parmPOParmListinput = new JSONObject();
                            System.out.println("inside testing loop");
                            System.out.println("line num === " + formObject.getNGValue("q_gateentrylines", i, 0));
                            System.out.println("item num === " + formObject.getNGValue("q_gateentrylines", i, 1));
                            parmPOParmListinput.put("lineNumber", formObject.getNGValue("q_gateentrylines", i, 0));
                            parmPOParmListinput.put("itemNumber", formObject.getNGValue("q_gateentrylines", i, 1));

                            System.out.println("parmPOParmListinput > " + parmPOParmListinput);
                            parmPOParmList.put(parmPOParmListinput);
                            System.out.println("parmPOParmList after loop> " + parmPOParmList);
                        }
                    }
                }
                System.out.println("parmPOParmList > " + parmPOParmList);
                JSONObject _parmContract = new JSONObject();
                _parmContract.put("parmPOParmList", parmPOParmList);
                request_json.put("_parmContract", _parmContract);
            } catch (JSONException ex) {
                Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
            }

            String outputJSON = objServiceConnection.callBasiAuthWebService(
                    "U1AuUkVRVUVTVEVSOkFCQzEyMw==",
                    objReadProperty.getValue("getPOData"),
                    request_json.toString().trim()
            );

            if (ProcessName.equalsIgnoreCase("Supply")) {
                webserviceStatus = parseSupplyPoOutputJSON(outputJSON);
                addToSypplyInvoice();
            }
            if (ProcessName.equalsIgnoreCase("Service")) {
                webserviceStatus = parseServicePoOutputJSON(outputJSON);
            }

            if (ProcessName.equalsIgnoreCase("RABill")) {
                webserviceStatus = parseRABillOutputJSON(outputJSON);
            }
            System.out.println("IsStatus return :call purchase order : " + webserviceStatus);
        } catch (JSONException ex) {
            Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    public String parseSupplyPoOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();

        String POLineContractXML = "";
        String VendorTaxInformation = "", VendorInvoiceAddress = "", vendorInvoiceLocation = "";
        String compositionScheme = "", compositionScheme2 = "", state = "", companyAddress = "", companyTaxInformation = "";
        JSONObject objJSON = new JSONObject(content);
        JSONObject objJSONObject = objJSON.getJSONObject("d");
        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :call purchase order :" + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {

            //Set Header Details
            System.out.println("Accounting date: " + objJSONObject.optString("AccountingDate"));
            formObject.setNGValue("currency", objJSONObject.optString("Currency"));
            formObject.setNGValue("suppliercode", objJSONObject.optString("VendorCode"));
            formObject.setNGValue("suppliername", objJSONObject.optString("VendorName"));
            formObject.setNGValue("loadingcity", objJSONObject.optString("LoadingCity"));
            formObject.setNGValue("site", objJSONObject.optString("Site"));
            formObject.setNGValue("gi_purchasestatus", objJSONObject.optString("PurchStatus"));

            String formatDate = objJSONObject.optString("AccountingDate");
            String podate = formatDate.replace('-', '/');
            System.out.println("after replace date : " + podate);
            formObject.setNGValue("purchaseorderdate", podate);

            formObject.setNGValue("businessunit", objJSONObject.optString("BusinessUnit"));
            formObject.setNGValue("msmestatus", objJSONObject.optString("MSMEStatus"));
            formObject.setNGValue("deliveryterm", objJSONObject.optString("DeliveryTerm"));
            formObject.setNGValue("POnumber", formObject.getNGValue("purchaseorderno"));
            formObject.setNGValue("VendorName", objJSONObject.optString("VendorName"));
            formObject.setNGValue("VendorCode", objJSONObject.optString("VendorCode"));

            compositionScheme = objJSONObject.optString("CompositionScheme");
            System.out.println("compositionScheme");
            if (compositionScheme.equalsIgnoreCase("1")) {
                System.out.println("inside 1 One");
                compositionScheme2 = "Yes";
            } else if (compositionScheme.equalsIgnoreCase("0")) {
                System.out.println("inside 0 zero");
                compositionScheme2 = "NO";
            }
            formObject.setNGValue("compositescheme", compositionScheme2);

            //Set Line Details
            JSONArray objJSONArray_poLineList = objJSONObject.getJSONArray("poLineList");
            for (int i = 0; i < objJSONArray_poLineList.length(); i++) {
                VendorTaxInformation = objJSONArray_poLineList.getJSONObject(0).optString("VendorTaxInformation");
                VendorInvoiceAddress = objJSONArray_poLineList.getJSONObject(0).optString("VendorInvoiceAddress");
                vendorInvoiceLocation = objJSONArray_poLineList.getJSONObject(0).optString("vendorInvoiceLocation");
                companyAddress = objJSONArray_poLineList.getJSONObject(0).optString("DeliveryAddress");
                companyTaxInformation = objJSONArray_poLineList.getJSONObject(0).optString("TaxInformation");

                formObject.setNGValue("vendortaxinformation", VendorTaxInformation);
                formObject.setNGValue("vendoraddress", VendorInvoiceAddress);
                formObject.setNGValue("vendorlocation", vendorInvoiceLocation);
                formObject.setNGValue("state", "");
                formObject.setNGValue("companyaddress", companyAddress);
                formObject.setNGValue("companytaxinformation", companyTaxInformation);

                POLineContractXML = (new StringBuilder()).append(POLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LineNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProductName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemModelGroupID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("purchQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Unit")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("UnitPrice")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountPercentage")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventorySite")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("BusinessUnit")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostCenter")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostCenterGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Department")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GLA")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GstinGdiUid")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorGSTIN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTrackingDimension")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("QuarantineManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("hlManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("rmManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TEFRID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FixedAssetGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("fixedAssetNumbe")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("")).
                        append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("linePurchStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("tcsGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("vendorInvoiceLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LedgerAccount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Address")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("AssessableValue")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryAddress")).
                        //append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryDate")).
                        append("</SubItem><SubItem>").append("").
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Exempt")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FormType")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("HSN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryConfiguration")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryWarehouse")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventTransId")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NatureofAssessee")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NonBusinessUsagePercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("OverDeliveryPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ppBagManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProdDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectCategory")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("PurchaseStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("RegistrationNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SAC")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("StorageDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TANNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxInformation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TINNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorInvoiceAddress")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorTaxInformation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("nonGST")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxRateType")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryRemainder")). //Remaining Quantity
                        append("</SubItem></ListItem>").toString();
            }

            formObject.clear("q_polines");
            formObject.NGAddListItem("q_polines", POLineContractXML);
            try {
                String businessunit = "", department = "", costcenter = "", costcentergroup = "", gla = "", poitemid = "";
                ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
                int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
                System.out.println("RowCountq_gateentrylines + " + RowCountq_gateentrylines);

                ListView ListViewq_polines = (ListView) formObject.getComponent("q_polines");
                int RowCountq_polines = ListViewq_polines.getRowCount();
                System.out.println("RowCountq_gateentrylines + " + RowCountq_polines);
                for (int j = 0; j < RowCountq_polines; j++) {
                    poitemid = formObject.getNGValue("q_polines", j, 1);
                    for (int i = 0; i < RowCountq_gateentrylines; i++) {
                        if (poitemid.equalsIgnoreCase(formObject.getNGValue("q_gateentrylines", i, 1))) {
                            System.out.println("congrates ****");
                            formObject.setNGValue("q_gateentrylines", i, 10, (formObject.getNGValue("q_polines", j, 12)));
                            formObject.setNGValue("q_gateentrylines", i, 13, (formObject.getNGValue("q_polines", j, 13)));
                            formObject.setNGValue("q_gateentrylines", i, 12, (formObject.getNGValue("q_polines", j, 14)));
                            formObject.setNGValue("q_gateentrylines", i, 14, (formObject.getNGValue("q_polines", j, 15)));
                            formObject.setNGValue("q_gateentrylines", i, 15, (formObject.getNGValue("q_polines", j, 16)));
                            System.out.println("congrates exit****");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in adding line item :" + e);
            }
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }

    public String parseServicePoOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();

        String POLineContractXML = "";
        String POLineChargesXML = "";
        String poNumber = formObject.getNGValue("ponumber");
        System.out.println("poNumber :" + poNumber);

        JSONObject objJSON = new JSONObject(content);
        JSONObject objJSONObject = objJSON.getJSONObject("d");
        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :ErrorMessage :call purchase order : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            System.out.println("at the correct position");
            //Set Multiple PO's
            String multiplepolistview = "<ListItem>"
                    + "<SubItem>" + formObject.getNGValue("ponumber") + "</SubItem>"
                    + "<SubItem>" + objJSONObject.optString("AccountingDate") + "</SubItem>"
                    + "</ListItem>";
            formObject.NGAddListItem("q_multiplepo", multiplepolistview);

            //Set Header Details
            formObject.setNGValue("currency", objJSONObject.optString("Currency"));
            formObject.setNGValue("suppliercode", objJSONObject.optString("VendorCode"));
            formObject.setNGValue("suppliername", objJSONObject.optString("VendorName"));
            formObject.setNGValue("loadingcity", objJSONObject.optString("LoadingCity"));
            formObject.setNGValue("businessunit", objJSONObject.optString("BusinessUnit"));
            formObject.setNGValue("site", objJSONObject.optString("Site"));
            formObject.setNGValue("deliveryterm", objJSONObject.optString("DeliveryTerm"));
            formObject.setNGValue("paymentterm", objJSONObject.optString("PaymentTermId"));
            formObject.setNGValue("msmestatus", objJSONObject.optString("MSMEStatus"));
            formObject.setNGValue("purchasestatus", objJSONObject.optString("PurchStatus"));
            String compositionScheme = objJSONObject.optString("CompositionScheme");
            if (compositionScheme.equalsIgnoreCase("1")) {
                formObject.setNGValue("compositescheme", "Yes");
            } else if (compositionScheme.equalsIgnoreCase("0")) {
                formObject.setNGValue("compositescheme", "No");
            }

            //Set Line Details
            JSONArray objJSONArray_poLineList = objJSONObject.getJSONArray("poLineList");
            formObject.setNGValue("vendortaxinformation", objJSONArray_poLineList.getJSONObject(0).optString("VendorTaxInformation"));
            formObject.setNGValue("vendoraddress", objJSONArray_poLineList.getJSONObject(0).optString("VendorInvoiceAddress"));
            formObject.setNGValue("companyaddress", objJSONArray_poLineList.getJSONObject(0).optString("DeliveryAddress"));
            formObject.setNGValue("companytaxinformation", objJSONArray_poLineList.getJSONObject(0).optString("TaxInformation"));

            for (int i = 0; i < objJSONArray_poLineList.length(); i++) {
                POLineContractXML = (new StringBuilder()).append(POLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LineNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProductName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("purchQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Unit")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("UnitPrice")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountPercentage")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventorySite")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("BusinessUnit")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostCenter")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostCenterGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Department")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GLA")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GstinGdiUid")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorGSTIN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LedgerAccount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Address")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("AssessableValue")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryAddress")).
                        //append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryDate")).
                        append("</SubItem><SubItem>").append("").
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Exempt")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FixedAssetGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("fixedAssetNumbe")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FormType")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("hlManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("HSN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryConfiguration")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryWarehouse")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventTransId")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemModelGroupID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTrackingDimension")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("linePurchStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NatureofAssessee")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NonBusinessUsagePercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("OverDeliveryPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ppBagManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProdDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectCategory")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("PurchaseStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("QuarantineManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("RegistrationNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("rmManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SAC")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("StorageDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TANNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxInformation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("tcsGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TEFRID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TINNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorInvoiceAddress")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("vendorInvoiceLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorTaxInformation")).
                        append("</SubItem><SubItem>").append(poNumber).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("nonGST")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxRateType")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryRemainder")). //Remaining Quantity
                        append("</SubItem></ListItem>").toString();

                JSONArray objJSONArray_POLineCharges = objJSONArray_poLineList.getJSONObject(i).getJSONArray("POLineCharges");
                for (int j = 0; j < objJSONArray_POLineCharges.length(); j++) {
                    POLineChargesXML = (new StringBuilder()).append(POLineChargesXML).
                            append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LineNumber")).
                            append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemNumber")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("calculatedAmount")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("currency")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("assesableValue")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("categoryENUM")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("categoryDescription")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("chargesValue")).
                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("chargesCode")).
                            append("</SubItem><SubItem>").append(poNumber).
                            append("</SubItem></ListItem>").toString();
                }
            }
//            formObject.clear("q_polinedetails");
//            formObject.clear("q_linechargesdetails");
            try {
                System.out.println("Po Lien charges : " + POLineChargesXML);
                formObject.NGAddListItem("q_polinedetails", POLineContractXML);
                formObject.NGAddListItem("q_linechargesdetails", POLineChargesXML);
            } catch (Exception e) {
                System.out.println("Exception in adding line item :" + e);
            }
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }

    public String parseRABillOutputJSON(String content) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();

        String POLineContractXML = "";
        String POLineChargesXML = "";
        JSONObject objJSON = new JSONObject(content);
          JSONObject objJSONObject =objJSON.getJSONObject("d");

        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage :ErrorMessage :call purchase order : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            System.out.println("at the correct position");
            //Set Header Details
//            formObject.setNGValue("currency", objJSONObject.optString("Currency"));
//            formObject.setNGValue("suppliercode", objJSONObject.optString("VendorCode"));
//            formObject.setNGValue("suppliername", objJSONObject.optString("VendorName"));
//            formObject.setNGValue("loadingcity", objJSONObject.optString("LoadingCity"));
//            formObject.setNGValue("businessunit", objJSONObject.optString("BusinessUnit"));
//            formObject.setNGValue("site", objJSONObject.optString("Site"));
//            formObject.setNGValue("deliveryterm", objJSONObject.optString("DeliveryTerm"));
//            formObject.setNGValue("paymentterm", objJSONObject.optString("PaymentTermDescription"));
//            formObject.setNGValue("paymenttermid", objJSONObject.optString("PaymentTermId"));
//            formObject.setNGValue("msmestatus", objJSONObject.optString("MSMEStatus"));jointmeasurementcode

            //formObject.setNGValue("jointmeasurementcode", objJSONObject.optString(""));
            formObject.setNGValue("contractor", objJSONObject.optString("VendorCode"));
            formObject.setNGValue("contractorname", objJSONObject.optString("VendorName"));
            formObject.setNGValue("site", objJSONObject.optString("Site"));
            String site = formObject.getNGValue("site");
            formObject.setNGValue("journalname", site + "RA");
            formObject.setNGValue("currency", objJSONObject.optString("Currency"));
            formObject.setNGValue("vendorcredit", objJSONObject.optString("VendorCode"));
            formObject.setNGValue("paymenttermid", objJSONObject.optString("PaymentTermId"));

            //Set Line Details
            JSONArray objJSONArray_poLineList = objJSONObject.getJSONArray("poLineList");
            formObject.setNGValue("warehouse", objJSONArray_poLineList.getJSONObject(0).optString("InventoryWarehouse"));
            formObject.setNGValue("abs_warehouse", objJSONArray_poLineList.getJSONObject(0).optString("InventoryWarehouse"));
            formObject.setNGValue("department", objJSONArray_poLineList.getJSONObject(0).optString("Department"));

            formObject.setNGValue("vendortaxinformation", objJSONArray_poLineList.getJSONObject(0).optString("VendorTaxInformation"));
            formObject.setNGValue("vendoraddress", objJSONArray_poLineList.getJSONObject(0).optString("VendorInvoiceAddress"));
            formObject.setNGValue("companyaddress", objJSONArray_poLineList.getJSONObject(0).optString("DeliveryAddress"));
            formObject.setNGValue("companytaxinformation", objJSONArray_poLineList.getJSONObject(0).optString("TaxInformation"));

            for (int i = 0; i < objJSONArray_poLineList.length(); i++) {
                System.out.println("Big decimal value: " + BigDecimal.valueOf(objJSONArray_poLineList.getJSONObject(i).getDouble("UnitPrice")));
                POLineContractXML = (new StringBuilder()).append(POLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LineNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProductName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("purchQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Unit")).
                        append("</SubItem><SubItem>").append(BigDecimal.valueOf(objJSONArray_poLineList.getJSONObject(i).getDouble("UnitPrice"))).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountPercentage")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DiscountAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventorySite")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("BusinessUnit")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostCenter")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CostElement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Department")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GLA")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("GstinGdiUid")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorGSTIN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LedgerAccount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Address")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("AssessableValue")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("CGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryAddress")).
                        //append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryDate")).
                        append("</SubItem><SubItem>").append("").
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryName")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("Exempt")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FixedAssetGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("fixedAssetNumbe")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("FormType")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("hlManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("HSN")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("IGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryConfiguration")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventoryWarehouse")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("InventTransId")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemModelGroupID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemTrackingDimension")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("linePurchStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NatureofAssessee")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("NonBusinessUsagePercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("OverDeliveryPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ppBagManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProdDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectCategory")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ProjectID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("PurchaseStatus")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("QuarantineManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("RegistrationNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("rmManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SAC")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("SGSTTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("StorageDimensionGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TANNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TaxInformation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("tcsGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TCSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TDSPercent")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TEFRID")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("TINNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATRate")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VATTaxAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorInvoiceAddress")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("vendorInvoiceLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("VendorTaxInformation")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ReceivedQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("DeliveryRemainder")). //Remaining Quantity
                        append("</SubItem></ListItem>").toString();

//                JSONArray objJSONArray_POLineCharges = objJSONArray_poLineList.getJSONObject(i).getJSONArray("POLineCharges");
//                for (int j = 0; j < objJSONArray_POLineCharges.length(); j++) {
//                    POLineChargesXML = (new StringBuilder()).append(POLineChargesXML).
//                            append("<ListItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("LineNumber")).
//                            append("</SubItem><SubItem>").append(objJSONArray_poLineList.getJSONObject(i).optString("ItemNumber")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("calculatedAmount")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("currency")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("assesableValue")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("categoryENUM")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("categoryDescription")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("chargesValue")).
//                            append("</SubItem><SubItem>").append(objJSONArray_POLineCharges.getJSONObject(j).optString("TaxComponent")).
//                            append("</SubItem></ListItem>").toString();
//                }
            }
            formObject.clear("q_polinedetails");
            formObject.clear("q_raabstractsheet");
            formObject.clear("q_raitemjournal");
            // formObject.clear("q_linechargesdetails");
            try {
                formObject.NGAddListItem("q_polinedetails", POLineContractXML);

            } catch (Exception e) {
                System.out.println("Exception in adding line item :" + e);
            }
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }

    public void addToSypplyInvoice() {
        Calculations objCalculations = new Calculations();
        System.out.println("inside addToSypplyInvoice");
        String getentry_lineno = "", getentry_itemno = "", invoice_lineno = "", invoice_itemno = "";
        boolean invoicelinechecker = true;
        String InvoiceLineContractXML = "", unitprice_poline = "", taxgroup_poline = "", discount_percent = "", discount_amount = "";

        formObject.clear("q_invoiceline");
        ListView ListViewq_polines = (ListView) formObject.getComponent("q_polines");
        int RowCountq_polines = ListViewq_polines.getRowCount();
        System.out.println("RowCountq_polines : " + RowCountq_polines);

        ListView ListViewq_invoiceline = (ListView) formObject.getComponent("q_invoiceline");
        int RowCountq_invoiceline = ListViewq_invoiceline.getRowCount();
        System.out.println("RowCountq_invoiceline : " + RowCountq_invoiceline);

        ListView ListViewq_gateentrylines = (ListView) formObject.getComponent("q_gateentrylines");
        int RowCountq_gateentrylines = ListViewq_gateentrylines.getRowCount();
        System.out.println("RowCountq_gateentrylines : " + RowCountq_gateentrylines);

        for (int i = 0; i < RowCountq_gateentrylines; i++) {
            System.out.println("inside for loop$");
            getentry_lineno = formObject.getNGValue("q_gateentrylines", i, 0);
            getentry_itemno = formObject.getNGValue("q_gateentrylines", i, 1);
            for (int j = 0; j <= RowCountq_invoiceline; j++) {
                System.out.println("inside inner for loop$");
                invoice_lineno = formObject.getNGValue("q_invoiceline", i, 0);
                invoice_itemno = formObject.getNGValue("q_invoiceline", i, 1);
                if (getentry_lineno.equalsIgnoreCase(invoice_itemno) && getentry_itemno.equalsIgnoreCase(invoice_itemno)) {
                    invoicelinechecker = false;
                    System.out.println("false hohya");
                }
            }

            for (int t = 0; t < RowCountq_polines; t++) {
                System.out.println("insideeeeeeeeor loop$");

                if (getentry_lineno.equalsIgnoreCase(formObject.getNGValue("q_polines", t, 0)) && getentry_itemno.equalsIgnoreCase(formObject.getNGValue("q_polines", t, 1))) {
                    unitprice_poline = formObject.getNGValue("q_polines", t, 6);
                    taxgroup_poline = formObject.getNGValue("q_polines", t, 17);

                    discount_percent = formObject.getNGValue("q_polines", i, 7);
                    discount_amount = formObject.getNGValue("q_polines", i, 8);

                }
            }
            System.out.println("1 :" + formObject.getNGValue("q_gateentrylines", i, 4));
            System.out.println("2unitprice_poline : " + unitprice_poline);
            System.out.println("3 taxgroup_poline : " + taxgroup_poline);
            String calculatedvalues[] = objCalculations.calculateLineTotalWithTax(formObject.getNGValue("q_gateentrylines", i, 4), unitprice_poline, taxgroup_poline).split("/");
            System.out.println("Calculatevalues" + calculatedvalues);
            if (invoicelinechecker == true) {
                String discountamount = "0";
                if (!discount_percent.equalsIgnoreCase("")
                        || !discount_percent.equalsIgnoreCase("0.0")
                        || !discount_percent.equalsIgnoreCase("0")) {
                    System.out.println("Inside qpo_discountpercent : " + discount_percent);
                    discountamount = objCalculations.calculatePercentAmount(calculatedvalues[0], discount_percent);
                } else {
                    System.out.println("Inside else qpo_discountpercent");
                    discountamount = discount_amount;
                }
                System.out.println("Discout Amount :" + discountamount);
                BigDecimal assessableamount = objCalculations.calculateDifference(calculatedvalues[0], discountamount);
                System.out.println("Assessable amount :" + assessableamount);
                System.out.println("po number -------:  " + formObject.getNGValue("purchaseorderno"));
                InvoiceLineContractXML = (new StringBuilder()).append(InvoiceLineContractXML).
                        append("<ListItem><SubItem>").append(formObject.getNGValue("q_gateentrylines", i, 0)). // line number 
                        append("</SubItem><SubItem>").append(formObject.getNGValue("q_gateentrylines", i, 1)). // Item id
                        append("</SubItem><SubItem>").append(formObject.getNGValue("q_gateentrylines", i, 2)). // Item Name
                        append("</SubItem><SubItem>").append(formObject.getNGValue("q_gateentrylines", i, 4)). // Quantity
                        append("</SubItem><SubItem>").append(unitprice_poline). // Rate
                        append("</SubItem><SubItem>").append(calculatedvalues[1]). // line total 
                        append("</SubItem><SubItem>").append(calculatedvalues[3]). // tax % 
                        append("</SubItem><SubItem>").append(calculatedvalues[2]). // tax amount
                        append("</SubItem><SubItem>").append(calculatedvalues[0]). // total amount
                        append("</SubItem><SubItem>").append(assessableamount). // Assasable Amount
                        append("</SubItem><SubItem>").append(assessableamount). //New Assasable Amount
                        append("</SubItem><SubItem>").append(formObject.getNGValue("purchaseorderno")). //po number
                        append("</SubItem></ListItem>").toString();
            }
        }
        System.out.println("InvoiceLineContractXML--->outside for loop : " + InvoiceLineContractXML);
        formObject.NGAddListItem("q_invoiceline", InvoiceLineContractXML);
    }
}