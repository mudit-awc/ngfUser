/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.common;

import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountsGeneral implements Serializable {

    FormReference formObject = null;
    List<List<String>> result, result1, result2, resultTaxDocument;
    Calculations objCalculations = null;
    General objGeneral = null;
    String invoiceLineListXML;
    private String Query, Query1, Query2;

    public void setRetention(String Query, String PaymentTermId, String RetCreditFieldId, String RetPercentFieldId, String RetAmountFieldId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objCalculations = new Calculations();
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result : " + result);
        ArrayList<String> baseamount = new ArrayList<String>();
        for (int i = 0; i < result.size(); i++) {
            baseamount.add(result.get(i).get(0));
        }
        formObject.setNGValue(RetCreditFieldId, objCalculations.calculateSum(baseamount));
        System.out.println("before second Query");
        Query = "select retentionpercent from PaymentTermMaster "
                + "where PaymentTermCode = '" + formObject.getNGValue(PaymentTermId) + "' "
                + "and retentionpercent is not null";
        System.out.println("Query2 : " + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result 2 : " + result.size());
        if (result.size() > 0) {
            System.out.println("inside if");
            formObject.setNGValue(RetPercentFieldId, result.get(0).get(0));
            formObject.setEnabled(RetAmountFieldId, false);
            System.out.println("RetPercentFieldId in if " + formObject.getNGValue(RetPercentFieldId));
        } else {
            System.out.println("inside else ");
            formObject.setNGValue(RetPercentFieldId, "0");
            formObject.setEnabled(RetAmountFieldId, true);
            System.out.println("RetPercentFieldId " + formObject.getNGValue(RetPercentFieldId));
        }
    }

    public void setWithHoldingTax(String Query, String WithHoldingLvId, String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        objCalculations = new Calculations();
        ListView ListViewq_withholdingtax = (ListView) formObject.getComponent(WithHoldingLvId);
        int rowCount = ListViewq_withholdingtax.getRowCount();
        //  if (rowCount == 0) {
        System.out.println("Query withholding tax: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            String WithholdingLineXML = "", adjustedoriginamount = "0";
            for (int i = 0; i < result.size(); i++) {
                String po_linenumber = result.get(i).get(0);
                String po_itemnumber = result.get(i).get(1);
                String po_ponumber = result.get(i).get(5);

                if (rowCount == 0) {
                    //add all
                    WithholdingLineXML = getWHTListViewXml(WithholdingLineXML, WithHoldingLvId, i, result);
                } else {
                    boolean itemexistflag = false;
                    for (int j = 0; j < rowCount; j++) {
                        String wht_linenumber = formObject.getNGValue(WithHoldingLvId, j, 0);
                        String wht_itemnumber = formObject.getNGValue(WithHoldingLvId, j, 1);
                        String wht_ponumber = formObject.getNGValue(WithHoldingLvId, j, 8);

                        if (po_itemnumber.equalsIgnoreCase(wht_itemnumber)
                                && po_linenumber.equalsIgnoreCase(wht_linenumber)
                                && po_ponumber.equalsIgnoreCase(wht_ponumber)) {
                            System.out.println("Item matched breaking loop");
                            itemexistflag = true;
                            break;
                        }
                    }
                    if (itemexistflag == false) {
                        System.out.println("Item does not matched");
                        WithholdingLineXML = getWHTListViewXml(WithholdingLineXML, WithHoldingLvId, i, result);
                    }
                }
            }
            System.out.println("Withholding Line XML last " + WithholdingLineXML);
            formObject.NGAddListItem(WithHoldingLvId, WithholdingLineXML);
            formObject.RaiseEvent("WFSave");
        }
        //  }
    }

    String getWHTListViewXml(String WithholdingLineXML, String WithHoldingLvId, int i, List<List<String>> result) {
        String adjustedoriginamount = "";
        objCalculations = new Calculations();
        String calculatedtaxamount = objCalculations.calculatePercentAmount(
                result.get(i).get(4),
                result.get(i).get(3)
        );

        if (Float.parseFloat(result.get(i).get(3)) == 0) {
            adjustedoriginamount = "0.00";
        } else {
            adjustedoriginamount = result.get(i).get(4);
        }
        WithholdingLineXML = (new StringBuilder()).append(WithholdingLineXML).
                append("<ListItem><SubItem>").append(result.get(i).get(0)).
                append("</SubItem><SubItem>").append(result.get(i).get(1)).
                append("</SubItem><SubItem>").append(result.get(i).get(2)).
                append("</SubItem><SubItem>").append(result.get(i).get(3)).
                append("</SubItem><SubItem>").append(adjustedoriginamount).
                append("</SubItem><SubItem>").append(calculatedtaxamount).
                append("</SubItem><SubItem>").append(calculatedtaxamount).
                append("</SubItem><SubItem>").append(result.get(i).get(2)).
                append("</SubItem><SubItem>").append(result.get(i).get(5)).
                append("</SubItem></ListItem>").toString();

        return WithholdingLineXML;
    }

    public void setTaxDocument(String Query, String TaxDocumentLvId, String processInstaceId) {
        //Tax Document
        formObject = FormContext.getCurrentInstance().getFormReference();
        objCalculations = new Calculations();
//        ListView ListViewq_taxdocument = (ListView) formObject.getComponent(TaxDocumentLvId);
//        int rowCount = ListViewq_taxdocument.getRowCount();
//        if (rowCount == 0) {
        try {
            System.out.println("Query testing :" + Query);
            resultTaxDocument = formObject.getDataFromDataSource(Query);
            if (resultTaxDocument.size() > 0) {
                String TaxDocumentXML = "";
                for (int i = 0; i < resultTaxDocument.size(); i++) {
                    String countQuery = "select count(*) from cmplx_taxdocument "
                            + "where linenumber = '" + resultTaxDocument.get(i).get(0) + "' "
                            + "and itemnumber = '" + resultTaxDocument.get(i).get(1) + "' "
                            + "and pinstanceid = '" + processInstaceId + "'";
                    System.out.println("Count Query : " + countQuery);
                    List<List<String>> countResult = formObject.getDataFromDataSource(countQuery);
                    if (countResult.get(0).get(0).equalsIgnoreCase("0")) {
                        //HSN or SAC
                        String hsnsactype = "", hsnsacdescription = "", hsnsaccode = "";
                        if ("NULL".equalsIgnoreCase(resultTaxDocument.get(i).get(3))
                                || null == resultTaxDocument.get(i).get(3)
                                || "".equalsIgnoreCase(resultTaxDocument.get(i).get(3))) {
                            hsnsactype = "SAC";
                            hsnsaccode = resultTaxDocument.get(i).get(4);
                            Query = "select Description from SACMaster where SACCode = '" + hsnsaccode + "'";
                        } else {
                            hsnsactype = "HSN";
                            hsnsaccode = resultTaxDocument.get(i).get(3);
                            Query = "select Description from HSNMaster where HSNCode = '" + hsnsaccode + "'";
                        }
                        System.out.println("HSNSAC Type :" + hsnsactype);
                        System.out.println("Query :" + Query);
                        List<List<String>> result_hsnsac = formObject.getDataFromDataSource(Query);
                        if (result_hsnsac.size() > 0) {
                            hsnsacdescription = result_hsnsac.get(0).get(0);
                        }

                        //Tax component
                        String taxcomponent = null, taxrate = null, taxamount = null, reversechargerate = null, reversechargeamount = null;
                        if ("No".equalsIgnoreCase(resultTaxDocument.get(i).get(14))) {
                            if ("0.0".equalsIgnoreCase(resultTaxDocument.get(i).get(5))
                                    && "0.0".equalsIgnoreCase(resultTaxDocument.get(i).get(6))) {
                                for (int j = 0; j < 2; j++) {
                                    System.out.println("Tax Compoonent Loop: " + j);
                                    if (j == 0) {
                                        taxcomponent = "SGST";
                                        taxrate = resultTaxDocument.get(i).get(9);
                                    } else {
                                        taxcomponent = "CGST";
                                        taxrate = resultTaxDocument.get(i).get(7);
                                    }
                                    taxamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), taxrate);
                                    reversechargerate = getReverseChargeRate(hsnsactype, hsnsaccode, taxcomponent, "Vendor", formObject.getNGValue("suppliercode"));
                                    System.out.println("Reverse charge rate" + reversechargerate);
                                    reversechargeamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), reversechargerate);
                                    System.out.println("Reverse Charge Amount :" + reversechargeamount);
                                    TaxDocumentXML = getTaxDocumentXml(
                                            TaxDocumentXML,
                                            resultTaxDocument.get(i).get(0), //line number
                                            resultTaxDocument.get(i).get(1), //item number
                                            resultTaxDocument.get(i).get(2), //gstingdiuid
                                            hsnsactype, //hsnsac type
                                            hsnsaccode, //hsnsac code
                                            hsnsacdescription, //hsnsac description
                                            taxcomponent, //tax component
                                            taxrate, //rate
                                            taxamount,//tax amount
                                            taxamount, //adjustment tax amount
                                            resultTaxDocument.get(i).get(11), //non business usage %
                                            reversechargerate, //reverse charge %
                                            reversechargeamount, //reverse charge amount
                                            resultTaxDocument.get(i).get(14), //Non-Gst
                                            resultTaxDocument.get(i).get(12), //exempt
                                            resultTaxDocument.get(i).get(18)); //PO Number
                                }
                            } else {
                                taxcomponent = "IGST";
                                taxrate = resultTaxDocument.get(i).get(5);
                                taxamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), taxrate);
                                reversechargerate = getReverseChargeRate(hsnsactype, hsnsaccode, taxcomponent, "Vendor", formObject.getNGValue("suppliercode"));
                                System.out.println("Reverse charge rate" + reversechargerate);
                                reversechargeamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), reversechargerate);
                                System.out.println("Reverse Charge Amount :" + reversechargeamount);
                                TaxDocumentXML = getTaxDocumentXml(
                                        TaxDocumentXML,
                                        resultTaxDocument.get(i).get(0), //line number
                                        resultTaxDocument.get(i).get(1), //item number
                                        resultTaxDocument.get(i).get(2), //gstingdiuid
                                        hsnsactype, //hsnsac type
                                        hsnsaccode, //hsnsac code
                                        hsnsacdescription, //hsnsac description
                                        taxcomponent, //tax component
                                        taxrate, //rate
                                        taxamount,//tax amount
                                        taxamount, //adjustment tax amount
                                        resultTaxDocument.get(i).get(11), //non business usage %
                                        reversechargerate, //reverse charge %
                                        reversechargeamount, //reverse charge amount
                                        resultTaxDocument.get(i).get(14), //Non-Gst
                                        resultTaxDocument.get(i).get(12), //exempt
                                        resultTaxDocument.get(i).get(18)); //PO Number
                            }
                        } else {
                            taxcomponent = resultTaxDocument.get(i).get(15);
                            taxrate = resultTaxDocument.get(i).get(16);
                            taxamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), taxrate);
                            reversechargerate = "0";
                            reversechargeamount = "0";
                            TaxDocumentXML = getTaxDocumentXml(
                                    TaxDocumentXML,
                                    resultTaxDocument.get(i).get(0), //line number
                                    resultTaxDocument.get(i).get(1), //item number
                                    resultTaxDocument.get(i).get(2), //gstingdiuid
                                    hsnsactype, //hsnsac type
                                    hsnsaccode, //hsnsac code
                                    hsnsacdescription, //hsnsac description
                                    taxcomponent, //tax component
                                    taxrate, //rate
                                    taxamount,//tax amount
                                    taxamount, //adjustment tax amount
                                    resultTaxDocument.get(i).get(11), //non business usage %
                                    reversechargerate, //reverse charge %
                                    reversechargeamount, //reverse charge amount
                                    resultTaxDocument.get(i).get(14), //Non-Gst
                                    resultTaxDocument.get(i).get(12), //exempt
                                    resultTaxDocument.get(i).get(18)); //PO Number
                        }
                    }
                }
                System.out.println("Tax Document XML " + TaxDocumentXML);
                formObject.NGAddListItem(TaxDocumentLvId, TaxDocumentXML);
                formObject.RaiseEvent("WFSave");
            }
        } catch (Exception e) {
            System.out.println("Exception :" + e.getMessage());
            e.printStackTrace();
        }
        //}
    }

    public String getReverseChargeRate(String hsnsaccodetype, String hsnsaccodevalue, String TaxComponenet, String accountype, String accountcode) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String ReverseChargeRate = "0", SelectColumn = "";
        if (TaxComponenet.equalsIgnoreCase("CGST")) {
            System.out.println("CGST");
            SelectColumn = "CGSTRCMPerc";
        } else if (TaxComponenet.equalsIgnoreCase("SGST")) {
            System.out.println("SGST");
            SelectColumn = "SGSTRCMPerc";
        } else if (TaxComponenet.equalsIgnoreCase("IGST")) {
            System.out.println("IGST");
            SelectColumn = "IGSTRCMPerc";
        }
        if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
            System.out.println("HSN");
            Query = "Select " + SelectColumn + " from HSNRateMaster where HSNCode = '" + hsnsaccodevalue + "'";
            System.out.println("Query HSN: " + Query);
        } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
            System.out.println("SAC");
            Query = "Select " + SelectColumn + " from SACRateMaster where SACCode = '" + hsnsaccodevalue + "'";
            System.out.println("Query SAC: " + Query);
        }

        result = formObject.getDataFromDataSource(Query);
        System.out.println("Result size :" + result.size());
        if (result.size() > 0) {
            if ((null == result.get(0).get(0)
                    || "NULL".equalsIgnoreCase(result.get(0).get(0)))) {
                ReverseChargeRate = "0";
//                if (accountype.equalsIgnoreCase("Customer")) {
//                    Query = "select * from CustomerMaster where code = '" + accountcode + "'";
//                } else if (accountype.equalsIgnoreCase("Vendor")) {
//                    Query = "select * from VendorMaster where VendorCode = '" + accountcode + "'";
//                }
//                result = formObject.getDataFromDataSource(Query);
//                System.out.println("Vendor Query :" + Query);
//                if (result.size() > 0) {
//                    ReverseChargeRate = "0";
//                } else {
//                    ReverseChargeRate = "100";
//                }
            } else {
                ReverseChargeRate = result.get(0).get(0);
            }
        }
        System.out.println("ReverseChargeRate :" + ReverseChargeRate);
        return ReverseChargeRate;
    }

    String getTaxDocumentXml(String TaxDocumentXML, String LineNumber, String ItemNumber, String GSTIN, String HSNSACType,
            String HSNSACCode, String HSNSACDesc, String TaxComponent, String TaxRate, String TaxAmount, String AdjustmentTaxAmount,
            String NonBusinessUsagePer, String ReverseChargePer, String ReverseChargeAmount,
            String NonGst, String Exempt, String ponumber) {
        TaxDocumentXML = (new StringBuilder()).append(TaxDocumentXML).
                append("<ListItem><SubItem>").append(LineNumber). //line number
                append("</SubItem><SubItem>").append(ItemNumber). //item number
                append("</SubItem><SubItem>").append(GSTIN). //gstingdiuid
                append("</SubItem><SubItem>").append(HSNSACType). //hsnsac type
                append("</SubItem><SubItem>").append(HSNSACCode). //hsnsac code
                append("</SubItem><SubItem>").append(HSNSACDesc). //hsnsac description
                append("</SubItem><SubItem>").append(TaxComponent). //tax component
                append("</SubItem><SubItem>").append(TaxRate). //rate
                append("</SubItem><SubItem>").append(TaxAmount). //tax amount
                append("</SubItem><SubItem>").append(AdjustmentTaxAmount). //adjustment tax amount
                append("</SubItem><SubItem>").append(NonBusinessUsagePer). //non business usage %
                append("</SubItem><SubItem>").append(ReverseChargePer). //reverse charge %
                append("</SubItem><SubItem>").append(ReverseChargeAmount). //reverse charge amount
                append("</SubItem><SubItem>").append(""). //cstvat %
                append("</SubItem><SubItem>").append(""). //cstvat amount
                append("</SubItem><SubItem>").append(NonGst). //Non-Gst
                append("</SubItem><SubItem>").append(Exempt). //exempt
                append("</SubItem><SubItem>").append(ponumber). //Po Number
                append("</SubItem></ListItem>").toString();

        return TaxDocumentXML;
    }

    public void setResolveAXException() {
        System.out.println("insidesetResolveAXException");
        formObject = FormContext.getCurrentInstance().getFormReference();
        objGeneral = new General();
        ListView ListViewq_axintegration_error = (ListView) formObject.getComponent("q_axintegration_error");
        int SIq_axintegration_error = ListViewq_axintegration_error.getSelectedRowIndex();
        formObject.setNGValue("q_axintegration_error", SIq_axintegration_error, 3, "True");
        formObject.setNGValue("q_axintegration_error", SIq_axintegration_error, 4, objGeneral.getCurrentDateTime());
        formObject.setNGValue("q_axintegration_error", SIq_axintegration_error, 5, formObject.getNGValue("qaxe_resolutionremarks"));
        formObject.RaiseEvent("WFSave");
    }

    public void getsetServiceNonPoSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal btotallineamount = null;
        Query = "select  COALESCE(sum(amount),0), COALESCE(sum(adjustmenttdsamount),0) from cmplx_ledgerlinedetails where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            btotallineamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totallineamount", result.get(0).get(0));
            formObject.setNGValue("totaltdsamount", result.get(0).get(1));
        }

        Query = "select  COALESCE(sum(taxamountadjustment),0) from cmplx_taxdocument where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal btotaltaxamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            formObject.setNGValue("totalamountwithtax", btotallineamount.add(btotaltaxamount));
        }
    }

    public void getsetServicePoSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal btotallineamount = null;
        Query = "select  COALESCE(sum(amount),0) from cmplx_invoicedetails where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            btotallineamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totallineamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(sum(adjustedtdsamount),0) from cmplx_withholdingtax where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaltdsamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(sum(calculatedamount),0) from cmplx_othercharges where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totalmaintaincharges", result.get(0).get(0));
        }

        Query = "select  COALESCE(sum(taxamountadjustment),0) from cmplx_taxdocument where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal btotaltaxamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            formObject.setNGValue("totalamountwithtaxes", btotallineamount.add(btotaltaxamount));
        }
    }

    public void getsetRABILLSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal btotallineamount = null;
        Query = "select COALESCE(sum(projectdebitamount),0) from cmplx_linejournal where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            btotallineamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totallineamount", result.get(0).get(0));
        }

        Query = "select COALESCE(sum(adjustedtdsamount),0) from cmplx_withholdingtax where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaltdsamount", result.get(0).get(0));
        }

//        Query = "select  COALESCE(sum(calculatedamount),0) from cmplx_othercharges where pinstanceid = '" + processInstanceId + "'";
//        System.out.println("Query: " + Query);
//        result = formObject.getDataFromDataSource(Query);
//        if (result.size() > 0) {
//            formObject.setNGValue("totalmaintaincharges", result.get(0).get(0));
//        }
        Query = "select  COALESCE(sum(taxamountadjustment),0) from cmplx_taxdocument where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal btotaltaxamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            formObject.setNGValue("totalamountwithtaxes", btotallineamount.add(btotaltaxamount));
        }
    }

    public void getsetSupplyPoSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal btotallineamount = null;
        Query = "select COALESCE(sum(cast(amount as float)),0) from cmplx_invoiceline where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            btotallineamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totallineamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(sum(adjustedtdsamount),0) from cmplx_withholdingtax where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaltdsamount", result.get(0).get(0));
        }

//        Query = "select  COALESCE(sum(calculatedamount),0) from cmplx_othercharges where pinstanceid = '" + processInstanceId + "'";
//        System.out.println("Query: " + Query);
//        result = formObject.getDataFromDataSource(Query);
//        if (result.size() > 0) {
//            formObject.setNGValue("totalmaintaincharges", result.get(0).get(0));
//        }
        Query = "select  COALESCE(sum(taxamountadjustment),0) from cmplx_taxdocument where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal btotaltaxamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            formObject.setNGValue("totalamountwithtaxes", btotallineamount.add(btotaltaxamount));
        }
    }

}
