/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.Webservice;

import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
public class CallPrePaymentService {

    FormReference formObject;
    FormConfig formConfig = null;
    General objGeneral = null;
    ReadProperty objReadProperty = null;
    ServiceConnection objServiceConnection = null;
    String webserviceStatus;
    private List<List<String>> result;
    private String outputJSON;
    private String Query;
    private String pid;
    ArrayList<String> rem_newgen;
    String ext_table = "", activity = "";

    public void GetSetPrePaymentLines(String AccessToken, String PONumber, String POType, String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objServiceConnection = new ServiceConnection();
        objReadProperty = new ReadProperty();
        pid = processInstanceId;

        JSONObject request_json = new JSONObject();
        JSONObject objJson2 = new JSONObject();
        JSONObject objJson3 = new JSONObject();
        JSONArray objJSONArray = new JSONArray();
        try {
            if (POType.equalsIgnoreCase("Supply")) {
                //request_json.put("_PONumber", PONumber);
                //new code Started
                objJson3.put("PONumber", PONumber);
                objJSONArray.put(objJson3);
                objJson2.put("POList", objJSONArray);
                request_json.put("_prePaymentInvoiceInputContract", objJson2);
                // new code END
                outputJSON = objServiceConnection.callBearerAuthWebService(
                        AccessToken,
                        objReadProperty.getValue("getPrePaymentLines"),
                        request_json.toString().trim());
            } else {
                Query = "select purchaseorderno from cmplx_multiplepo where pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "'";
                result = formObject.getDataFromDataSource(Query);
                if (result.size() > 0) {
                    formObject.setNGValue("po_number1", result.get(0).get(0));
                    for (int i = 0; i < result.size(); i++) {
                        //request_json.put("_PONumber", result.get(i).get(0));
                        ////new code Started

                        JSONObject objJson4 = new JSONObject();
                        objJson4.put("PONumber", result.get(i).get(0));
                        objJSONArray.put(objJson4);
                    }

                    objJson2.put("POList", objJSONArray);
                    request_json.put("_prePaymentInvoiceInputContract", objJson2);

                    ///new code END
                    outputJSON = objServiceConnection.callBearerAuthWebService(
                            AccessToken,
                            objReadProperty.getValue("getPrePaymentLines"),
                            request_json.toString().trim());
                    //  }
                }
            }
            System.out.println("outputJSON : " + outputJSON);
            webserviceStatus = parsePrePaymentOutputJSON(outputJSON, POType);
        } catch (JSONException ex) {
            Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("IsStatus return :call purchase order : " + webserviceStatus);
        if (!webserviceStatus.equalsIgnoreCase("true")) {
            throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
        }
    }

    public String parsePrePaymentOutputJSON(String content, String po_type) throws JSONException {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String invoiceLineListXML = "";
        JSONObject objJSONObject = new JSONObject(content);
        BigDecimal bg = null;
        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("isSuccess");
        String ErrorMessage = objJSONObject.optString("errorMessage");
        float totalAmount, settleamount, remainingAmount;
        JSONObject objJSONObject1 = new JSONObject();

        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            ////new code Started 
            JSONArray objJSONArray_POList = objJSONObject.getJSONArray("POList");
            System.out.println("POList ki length : " + objJSONArray_POList.length());
            for (int i = 0; i < objJSONArray_POList.length(); i++) {
                System.out.println("inside firft loop ");
                System.out.println("test 1= " + objJSONArray_POList.getJSONObject(i).optString("purchId"));//testting

                JSONArray objJSONArray_invoiceLineList = objJSONArray_POList.getJSONObject(i).getJSONArray("invoiceLineList");
                System.out.println("before second loop ");
                System.out.println("test 2= " + objJSONArray_POList.getJSONObject(i).optString("purchId"));  //test 2
                for (int j = 0; j < objJSONArray_invoiceLineList.length(); j++) {
                    System.out.println("inside second loop ");
                    //  totalAmount = Float.parseFloat(objJSONArray_invoiceLineList.getJSONObject(j).optString("totalAmount"));
                    //  settleamount = Float.parseFloat(objJSONArray_invoiceLineList.getJSONObject(j).optString("settleAmount"));
                    //  System.out.println("totalAmount : " + totalAmount);
                    //  System.out.println("settleamount : " + settleamount);
                    //  remainingAmount = totalAmount - settleamount;
                    //  System.out.println("remainingAmount : " + remainingAmount);
                    System.out.println("invoicelinelist length is : " + objJSONArray_invoiceLineList.length());
                    BigDecimal remaining_amount_newgen;
                    String Po_number = objJSONArray_POList.getJSONObject(i).optString("purchId");
                    String Invoice_no = objJSONArray_invoiceLineList.getJSONObject(j).optString("invoiceID");
                    if (po_type.equalsIgnoreCase("Supply")) {
                        ext_table = "ext_supplypoinvoices";
                        activity = "'SchedulerAccounts'," + "'AXAccountsSyncException'";
                    } else {
                        ext_table = "ext_servicepoinvoice";
                        activity = "'SchedulerAccount'," + "'AXSyncException'";
                    }
                   Query = "select remainingamountnewgen,remainingamount from cmplx_prepayment where purchaseorderno='" + Po_number + "' and prepaymentinvoicenumber = '" + Invoice_no + "'"
                    + " and pinstanceid in (select top 1 processid from " + ext_table + " where nextactivity in (" + activity + ") and postingsyncstatus != 'Success' order by processid desc);                                                 ";
                    System.out.println("Query prepayment: " + Query);
                    result = formObject.getDataFromDataSource(Query);
                    System.out.println("result: " + result);
                    if (result.size() > 0) {
                        remaining_amount_newgen = new BigDecimal(result.get(0).get(0));
                        rem_newgen = new ArrayList<>();
                        rem_newgen.add(remaining_amount_newgen + "#" + Po_number + "#" + Invoice_no);
                    } else {
                        remaining_amount_newgen = BigDecimal.valueOf(objJSONArray_invoiceLineList.getJSONObject(j).getDouble("settleAmount"));
                    }
                    invoiceLineListXML = (new StringBuilder()).append(invoiceLineListXML).
                            append("<ListItem><SubItem>").append(objJSONArray_invoiceLineList.getJSONObject(j).optString("invoiceID")).
                            append("</SubItem><SubItem>").append(new BigDecimal(objJSONArray_invoiceLineList.getJSONObject(j).getDouble("totalAmount")).setScale(2, BigDecimal.ROUND_HALF_UP)).
                            append("</SubItem><SubItem>").append("0").
                            append("</SubItem><SubItem>").append(remaining_amount_newgen.setScale(2, BigDecimal.ROUND_HALF_UP)).
                            append("</SubItem><SubItem>").append(new BigDecimal(objJSONArray_invoiceLineList.getJSONObject(j).getDouble("settleAmount")).setScale(2, BigDecimal.ROUND_HALF_UP)).//remaining amount
                            append("</SubItem><SubItem>").append(objJSONArray_POList.getJSONObject(i).optString("purchId")). //po number
                            append("</SubItem></ListItem>").toString();
                }
                System.out.println("after second loop ");
            }
            System.out.println("after first loop ");
            System.out.println("invoiceLineListXML : " + invoiceLineListXML);

            try {
                Query = "select count(*) from cmplx_prepayment where pinstanceid ='" + pid + "'";
                result = formObject.getDataFromDataSource(Query);
                if ("0".equalsIgnoreCase(result.get(0).get(0))) {
                    System.out.println("adding line");
                    formObject.clear("q_prepayment");
                    formObject.NGAddListItem("q_prepayment", invoiceLineListXML);
                    formObject.RaiseEvent("WFSave");
                } else {
                    for (int k = 0; k < rem_newgen.size(); k++) {
                        try {
                            Query = "update cmplx_prepayment set reremainingamountnewgen = '" + rem_newgen.get(k).split("#")[0] + "' where  purchaseorderno='" + rem_newgen.get(k).split("#")[1] + "' and prepaymentinvoicenumber = '" + rem_newgen.get(k).split("#")[2] + "' andpinstanceid ='" + pid + "'";
                            formObject.saveDataIntoDataSource(Query);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception occures!!: " + e.getMessage());
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

    public void doneprepaymentcheck(String po_type) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal remaining_new, remaining_old = null;
        int rowindex = formObject.getLVWRowCount("q_prepayment");
        for (int i = 0; i < rowindex; i++) {
            String Po_number = formObject.getNGValue("q_prepayment", i, 5);
            String Invoice_no = formObject.getNGValue("q_prepayment", i, 0);
            if (po_type.equalsIgnoreCase("Supply")) {
                ext_table = "ext_supplypoinvoices";
                activity = "'SchedulerAccounts'," + "'AXAccountsSyncException'";
            } else {
                ext_table = "ext_servicepoinvoice";
                activity = "'SchedulerAccount'," + "'AXSyncException'";
            }
            Query = "select remainingamountnewgen,remainingamount from cmplx_prepayment where purchaseorderno='" + Po_number + "' and prepaymentinvoicenumber = '" + Invoice_no + "'"
                    + " and pinstanceid in (select top 1 processid from " + ext_table + " where nextactivity in (" + activity + ") and postingsyncstatus != 'Success' order by processid desc);                                                  ";
            System.out.println("Query prepayment: " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result: " + result);
            if (result.size() > 0) {
                remaining_new = new BigDecimal(result.get(0).get(0));
                Double rem_old = Double.parseDouble(formObject.getNGValue("q_prepayment", i, 2)) + Double.parseDouble(formObject.getNGValue("q_prepayment", i, 3));
                remaining_old = new BigDecimal(rem_old);
                if (!(remaining_new == remaining_old)) {
                    rem_old = remaining_new.doubleValue() - Double.parseDouble(formObject.getNGValue("q_prepayment", i, 2));
                    formObject.setNGValue("q_prepayment", i, 3, String.valueOf(rem_old));
                    formObject.setNGValue("q_prepayment", i, 4, result.get(0).get(1));
                }
            }
        }
    }
}
