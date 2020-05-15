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
    List<List<String>> result, result1, resultTaxDocument;
    Calculations objCalculations = null;
    General objGeneral = null;
    String invoiceLineListXML;
    private String Query;

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
                        String wht_ponumber = formObject.getNGValue(WithHoldingLvId, j, 9);

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
        String percent = "";
        Query = "select PAN,calculatewithholdingtax from VendorMaster where VendorCode = '" + formObject.getNGValue("suppliercode") + "'";
        System.out.println("Query :" + Query);
        result1 = formObject.getDataFromDataSource(Query);
        if (result1.size() > 0) {
            if ("1".equalsIgnoreCase(result1.get(0).get(1))) {
                if ("".equalsIgnoreCase(result1.get(0).get(0))) {
                    Query = "select TaxPercwithoutPAN from TDSMaster where code = '" + result.get(0).get(2) + "'";
                } else {
                    Query = "select TaxPercwithPAN from TDSMaster where code = '" + result.get(0).get(2) + "'";
                }
                System.out.println("Query :" + Query);
                List<List<String>> resulttaxperc = formObject.getDataFromDataSource(Query);
                if (resulttaxperc.size() > 0) {
                    percent = resulttaxperc.get(0).get(0);
                } else {
                    percent = "0";
                }
            } else {
                percent = "0";
            }
        }

        objCalculations = new Calculations();
        String calculatedtaxamount = objCalculations.calculatePercentAmount(
                result.get(i).get(4),
                percent
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
                append("</SubItem><SubItem>").append(percent).
                append("</SubItem><SubItem>").append(result.get(i).get(6)).
                append("</SubItem><SubItem>").append(adjustedoriginamount).
                append("</SubItem><SubItem>").append(calculatedtaxamount).
                append("</SubItem><SubItem>").append(new BigDecimal(calculatedtaxamount).setScale(0, BigDecimal.ROUND_HALF_UP)).
                append("</SubItem><SubItem>").append(result.get(i).get(2)).
                append("</SubItem><SubItem>").append(result.get(i).get(5)).
                append("</SubItem></ListItem>").toString();

        return WithholdingLineXML;
    }

    public void setTaxDocument(String Query, String TaxDocumentLvId, String processInstaceId) {
        //Tax Document
        formObject = FormContext.getCurrentInstance().getFormReference();
        objCalculations = new Calculations();

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
                            if (hsnsacdescription.contains("<")) {
                                hsnsacdescription = hsnsacdescription.replace("<", "&lt");
                            }
                            if (hsnsacdescription.contains("&")) {
                                hsnsacdescription = hsnsacdescription.replace("&", "&amp");
                            }
                            if (hsnsacdescription.contains(">")) {
                                hsnsacdescription = hsnsacdescription.replace(">", "&gt");
                            }
                            if (hsnsacdescription.contains("\"")) {
                                hsnsacdescription = hsnsacdescription.replace("\"", "&quot");
                            }
                            if (hsnsacdescription.contains("\'")) {
                                hsnsacdescription = hsnsacdescription.replace("'", "&apos");
                            }
                        }

                        String taxcomponent = null, taxrate = null, taxamount = null, reversechargerate = null, reversechargeamount = null;
                        if ("No".equalsIgnoreCase(resultTaxDocument.get(i).get(14))) {
                            if (0 == (Float.parseFloat(resultTaxDocument.get(i).get(5)))
                                    && 0 == (Float.parseFloat(resultTaxDocument.get(i).get(6)))) {
                                for (int j = 0; j < 2; j++) {
                                    System.out.println("Tax Compoonent Loop: " + j);
                                    if (j == 0) {
                                        taxcomponent = "SGST";
                                        taxrate = resultTaxDocument.get(i).get(9);
                                    } else {
                                        taxcomponent = "CGST";
                                        taxrate = resultTaxDocument.get(i).get(7);
                                    }

                                    if (resultTaxDocument.get(i).get(12).equalsIgnoreCase("0")) {
                                        taxamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), taxrate);
                                    } else {
                                        taxamount = "0";
                                    }

                                    if ("RCM".equalsIgnoreCase(resultTaxDocument.get(i).get(15))) {
                                        reversechargerate = getReverseChargeRate(hsnsactype, hsnsaccode, taxcomponent, "Vendor", formObject.getNGValue("suppliercode"));
                                    } else {
                                        reversechargerate = "0";
                                    }
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
                                if (resultTaxDocument.get(i).get(12).equalsIgnoreCase("0")) {
                                    taxamount = objCalculations.calculatePercentAmount(resultTaxDocument.get(i).get(13), taxrate);
                                } else {
                                    taxamount = "0";
                                }

                                if ("RCM".equalsIgnoreCase(resultTaxDocument.get(i).get(15))) {
                                    reversechargerate = getReverseChargeRate(hsnsactype, hsnsaccode, taxcomponent, "Vendor", formObject.getNGValue("suppliercode"));
                                } else {
                                    reversechargerate = "0";
                                }
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
                ReverseChargeRate = "0.00";
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
        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'CGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal totalcgstamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totalcgstamount", totalcgstamount);
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'SGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal totalsgstamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totalsgstamount", totalsgstamount);
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'IGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            BigDecimal totaligstamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaligstamount", totaligstamount);
        }
    }

    public void getsetServicePoSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        BigDecimal btotallineamount = BigDecimal.ZERO;
        BigDecimal btotalothercharges = BigDecimal.ZERO;
        BigDecimal btotaltaxamount = BigDecimal.ZERO;

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
            btotalothercharges = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totalmaintaincharges", btotalothercharges);
        }

        Query = "select  COALESCE(sum(taxamountadjustment),0) from cmplx_taxdocument where "
                + "pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            btotaltaxamount = new BigDecimal(result.get(0).get(0));
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            formObject.setNGValue("totalamountwithtaxes", btotallineamount.add(btotaltaxamount));
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'CGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totalcgstamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'SGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totalsgstamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'IGST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaligstamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'VAT' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totalvatamount", result.get(0).get(0));
        }

        Query = "select  COALESCE(SUM(taxamountadjustment),0) from cmplx_taxdocument where taxcomponent = 'CST' and pinstanceid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totalcstamount", result.get(0).get(0));
        }

        formObject.setNGValue("finalamount", btotallineamount.add(btotaltaxamount).add(btotalothercharges));
        formObject.setNGValue("summ_invoiceamount", formObject.getNGValue("invoiceamount"));
        formObject.setNGValue("summ_invoiceamountreporting", formObject.getNGValue("newbaseamount"));
    }

    public void getsetOutwardFreightSummary(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String Line_Amount = "", Tax_Amount = "";
        formObject.setNGValue("totallineamount", formObject.getNGValue("TotalFreight"));
        Line_Amount = formObject.getNGValue("TotalFreight");
        System.out.println("Line_Amount: " + Line_Amount);
        Query = "select COALESCE(sum(tax_amount),0) from complex_tds_document where processid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaltaxamount", result.get(0).get(0));
            Tax_Amount = result.get(0).get(0);
            System.out.println("Tax Amount: " + Tax_Amount);
        }
        Query = "select COALESCE(sum(adj_tds_amount),0) from complex_withHolding where processid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            formObject.setNGValue("totaltdsamount", result.get(0).get(0));

        }
        String TotalAmount = String.valueOf(Float.parseFloat(Line_Amount) + Float.parseFloat(Tax_Amount));
        System.out.println("TotalAmount: " + TotalAmount);
        formObject.setNGValue("Amount_withTaxes", TotalAmount);

        Query = "select count(*) from complex_transportdetail where procid = '" + processInstanceId + "'";
        result = formObject.getDataFromDataSource(Query);
        System.out.println("Total Invoice Count: " + result.get(0).get(0));
        if (result.size() > 0) {
            formObject.setNGValue("invoice_count", result.get(0).get(0));
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

    public void setFinancialDimension(String FinancialDimensionLvId, String processInstaceId) {
        Query = "select po.ledgeraccount,po.businessunit,po.state,po.costcenter,po.costcentergroup,po.gla,"
                + "po.vendortaxinformation,po.purchaseorderno,po.linenumber,po.itemnumber,po.department "
                + "from cmplx_polinedetails po, cmplx_invoicedetails inv "
                + "where po.pinstanceid = inv.pinstanceid "
                + "and inv.purchaseorderno = po.purchaseorderno "
                + "and inv.linenumber = po.linenumber "
                + "and inv.itemid = po.itemnumber "
                + "and po.pinstanceid = '" + processInstaceId + "'";
        String FinancialDimensionXML = "";
        formObject = FormContext.getCurrentInstance().getFormReference();
        List<List<String>> conactresult;
        String sitedesc = "", accountdesc = "", costcenterdesc = "", gladesc = "", costelementdesc = "", departmentdesc = "";
        System.out.println("inside setFinancialDimension" + Query);
        result = formObject.getDataFromDataSource(Query);
        for (int f = 0; f < result.size(); f++) {
            Query = "select count(*) from cmplx_financialdimension where pinstanceid = '" + processInstaceId + "' "
                    + "and purchaseorderno = '" + result.get(f).get(7) + "' "
                    + "and linenumber = '" + result.get(f).get(8) + "' "
                    + "and itemnumber = '" + result.get(f).get(9) + "'";
            System.out.println("Query:" + Query);
            List<List<String>> countresult = formObject.getDataFromDataSource(Query);
            if (countresult.get(0).get(0).equals("0")) {
                System.out.println("Line doesnt exist in financial dimension");
                Query = "select concat(AccountId,'-',Description) from LedgerACMaster where AccountId = '" + result.get(f).get(0) + "'";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    accountdesc = conactresult.get(0).get(0);
                }

                Query = "select concat(SiteCode,'-',SiteName) from SiteMaster where SiteCode = '" + result.get(f).get(1) + "'";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    sitedesc = conactresult.get(0).get(0);
                }

                Query = "select concat(value,'-',Description) from CostCenter where value = '" + result.get(f).get(3) + "'";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    costcenterdesc = conactresult.get(0).get(0);
                }

                Query = "select concat(value,'-',Description) from GLAMaster where value = '" + result.get(f).get(5) + "'";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    gladesc = conactresult.get(0).get(0);
                }

                Query = "select concat(value,'-',Description) from Department where value = '" + result.get(f).get(10) + "'";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    departmentdesc = conactresult.get(0).get(0);
                }

                Query = "select concat(value,'-',Description) from CostElement "
                        + "where AxRecId = (select CostCenterGroupRecId from CostCenter where value='" + result.get(f).get(3) + "')";
                conactresult = formObject.getDataFromDataSource(Query);
                if (conactresult.size() > 0) {
                    costelementdesc = conactresult.get(0).get(0);
                }

                FinancialDimensionXML = (new StringBuilder()).append(FinancialDimensionXML).
                        append("<ListItem><SubItem>").append(result.get(f).get(8)).//line no
                        append("</SubItem><SubItem>").append(result.get(f).get(9)).//item no
                        append("</SubItem><SubItem>").append(result.get(f).get(0)).//ledger account
                        append("</SubItem><SubItem>").append(accountdesc).//ledger account discription
                        append("</SubItem><SubItem>").append(result.get(f).get(1)).//business unit
                        append("</SubItem><SubItem>").append(sitedesc).//business unit description
                        append("</SubItem><SubItem>").append(result.get(f).get(2)).// state 
                        append("</SubItem><SubItem>").append(result.get(f).get(3)).// cost center 
                        append("</SubItem><SubItem>").append(costcenterdesc).// cost center description
                        append("</SubItem><SubItem>").append(result.get(f).get(4)).//// cost center Group
                        append("</SubItem><SubItem>").append(costelementdesc).// cost center Group description
                        append("</SubItem><SubItem>").append(result.get(f).get(5)).//gla
                        append("</SubItem><SubItem>").append(gladesc).// gla description
                        append("</SubItem><SubItem>").append(result.get(f).get(6)).//vendor
                        append("</SubItem><SubItem>").append(result.get(f).get(10)).//department
                        append("</SubItem><SubItem>").append(departmentdesc).//department description
                        append("</SubItem><SubItem>").append(result.get(f).get(7)).//po number 
                        append("</SubItem></ListItem>").toString();
            }
        }
        System.out.println("FinancialDimension XML " + FinancialDimensionXML);
        formObject.NGAddListItem(FinancialDimensionLvId, FinancialDimensionXML);
        formObject.RaiseEvent("WFSave");
    }

    public void setWithHoldingTaxLower(String Query, String WithHoldingLvId, String processInstanceId) {
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
//                String po_linenumber = result.get(i).get(0);
//                String po_itemnumber = result.get(i).get(1);
//                String po_ponumber = result.get(i).get(5);

//                if (rowCount == 0) {
                //add all
                WithholdingLineXML = getWHTListViewXmlLower(WithholdingLineXML, WithHoldingLvId, i, result);
//                } else {
//                    boolean itemexistflag = false;
//                    for (int j = 0; j < rowCount; j++) {
//                        String wht_linenumber = formObject.getNGValue(WithHoldingLvId, j, 0);
//                        String wht_itemnumber = formObject.getNGValue(WithHoldingLvId, j, 1);
//                        String wht_ponumber = formObject.getNGValue(WithHoldingLvId, j, 8);
//
//                        if (po_itemnumber.equalsIgnoreCase(wht_itemnumber)
//                                && po_linenumber.equalsIgnoreCase(wht_linenumber)
//                                && po_ponumber.equalsIgnoreCase(wht_ponumber)) {
//                            System.out.println("Item matched breaking loop");
//                            itemexistflag = true;
//                            break;
//                        }
//                    }
//                    if (itemexistflag == false) {
//                        System.out.println("Item does not matched");
//                        WithholdingLineXML = getWHTListViewXmlLower(WithholdingLineXML, WithHoldingLvId, i, result);
//                    }
//                }
            }
            System.out.println("Withholding Line XML last " + WithholdingLineXML);
            formObject.NGAddListItem(WithHoldingLvId, WithholdingLineXML);
            formObject.RaiseEvent("WFSave");
        }
        //  }
    }

    public void refreshTaxDocument(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        Query = "select count(*) from cmplx_taxdocument where pinstanceid = '" + processInstanceId + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (!result.get(0).get(0).equals("0")) {
            formObject.clear("q_taxdocument");
//            formObject.renderControl("q_taxdocument");
//            formObject.RaiseEvent("WFSave");
//            throw new ValidatorException(new FacesMessage("Tax document deatils cleared."));
        }
    }

    String getWHTListViewXmlLower(String WithholdingLineXML, String WithHoldingLvId, int i, List<List<String>> result) {
        String adjustedoriginamount = "";
        objCalculations = new Calculations();
        String calculatedtaxamount = objCalculations.calculateLowerTDS(
                result.get(i).get(4),
                result.get(i).get(3),
                result.get(i).get(6)
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
                append("</SubItem><SubItem>").append(calculatedtaxamount.split("#")[1]).
                append("</SubItem><SubItem>").append(adjustedoriginamount).
                append("</SubItem><SubItem>").append(calculatedtaxamount.split("#")[0]).
                append("</SubItem><SubItem>").append(calculatedtaxamount.split("#")[0]).
                append("</SubItem><SubItem>").append(result.get(i).get(2)).
                append("</SubItem><SubItem>").append(result.get(i).get(5)).
                append("</SubItem></ListItem>").toString();

        return WithholdingLineXML;
    }

}
