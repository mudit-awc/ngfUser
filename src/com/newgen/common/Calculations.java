/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.common;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public class Calculations implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    List<List<String>> result;
    String Query;

    public String calculateLineTotalWithTax(String Quantity, String Rate, String TaxGroup) {
        System.out.println("Inside calculateLineTotalWithTax");
        formObject = FormContext.getCurrentInstance().getFormReference();
        String percentage = "0";
       /* if (TaxGroup.equalsIgnoreCase("")) {
            percentage = "0";
        } else {
            String Query = "select TaxPercentage from ItemTaxgroupMaster where TAXITEMGROUP='" + TaxGroup + "'";
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result: " + result);
            if (result.size() > 0) {
                percentage = result.get(0).get(0);
            } else {
                percentage = "0";
            }
        } */
        String TotalTaxAmount = "", LineTotal = "", TaxAmount = "";
        BigDecimal bquantity = new BigDecimal(Quantity);
        BigDecimal bRate = new BigDecimal(Rate);
        BigDecimal btaxPercent = new BigDecimal(percentage);
        BigDecimal bhundred = new BigDecimal(100);

        bquantity = bquantity.multiply(bRate);
        bquantity = bquantity.setScale(2, BigDecimal.ROUND_FLOOR);
        System.out.println("linetotal: " + bquantity);

        btaxPercent = btaxPercent.divide(bhundred);
        btaxPercent = btaxPercent.multiply(bquantity);
        btaxPercent = btaxPercent.setScale(2, BigDecimal.ROUND_FLOOR);
        System.out.println("taxAmount : " + btaxPercent);

        bquantity = bquantity.add(btaxPercent);
        bquantity = bquantity.setScale(2, BigDecimal.ROUND_FLOOR);
        System.out.println("totalamount : " + bquantity);

        TaxAmount = btaxPercent.toString();
        LineTotal = bquantity.toString();
        TotalTaxAmount = bquantity.toString();

        return TotalTaxAmount + "/" + LineTotal + "/" + TaxAmount + "/" + percentage;
    }

    public String calculatePercentAmount(String Amount, String Percent) {
        System.out.println("Amount : " + Amount);
        System.out.println("Percent : " + Percent);
        BigDecimal bAmount = new BigDecimal(Amount);
        BigDecimal bPercent = new BigDecimal(Percent);
        BigDecimal bHundred = new BigDecimal(100);

        bAmount = bAmount.divide(bHundred);
        System.out.println("after divide: " + bAmount);
        bAmount = bAmount.multiply(bPercent);
        System.out.println("final: " + bAmount);
        String percent = bAmount.setScale(2, BigDecimal.ROUND_FLOOR).toString();
        System.out.println("Percent " + percent);
        return percent;
    }

    public BigDecimal calculateSum(ArrayList<String> amount) {
        System.out.println("Inside calculate Sum");
        BigDecimal barray = null;
        BigDecimal sum = BigDecimal.valueOf(0);
        String a = "";
        for (int i = 0; i < amount.size(); i++) {
            System.out.println("Inside for loop");
            a = amount.get(i);
            barray = new BigDecimal(a);
            sum = sum.add(barray);
        }
        System.out.println("after sum : " + sum);
        return sum;
    }

    public BigDecimal calculateDifference(String value1, String value2) {
        System.out.println("Inside calculate Difference");
        BigDecimal bvalue1 = new BigDecimal(value1);
        BigDecimal bvalue2 = new BigDecimal(value2);

        BigDecimal difference = bvalue1.subtract(bvalue2);

        return difference;
    }

    public void exronBaseamountandExchangerateChange(String currencyId, String baseamountId, String newbaseamountId, String exchangerateId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside exr base and exchangerate change: ");
        String currency = formObject.getNGValue(currencyId);
        String baseamount = formObject.getNGValue(baseamountId);

        System.out.println("Currency: " + currency + " Base" + baseamount);
        if (!currency.equalsIgnoreCase("INR")) {
            System.out.println("Inside else");
            BigDecimal bamount = new BigDecimal(baseamount);
            BigDecimal exchanger = new BigDecimal(formObject.getNGValue(exchangerateId));
            if (!exchanger.toString().equalsIgnoreCase("")) {
                bamount = bamount.multiply(exchanger.setScale(2, BigDecimal.ROUND_FLOOR));
                bamount = bamount.setScale(2, BigDecimal.ROUND_FLOOR);
                formObject.setNGValue(newbaseamountId, bamount.toString());
            }
        } else {
            System.out.println("inside if");
            formObject.setNGValue(newbaseamountId, baseamount);
        }
    }

//    public void exronExchangrateChange(String currencyId, String baseamountId, String newbaseamountId, String exchangerateId) {
//        formObject = FormContext.getCurrentInstance().getFormReference();
//        String currency = formObject.getNGValue(currencyId);
//        String baseamount = formObject.getNGValue(baseamountId);
//        if (!currency.equalsIgnoreCase("INR")) {
//            BigDecimal bamount = new BigDecimal(baseamount);
//            String exchange = formObject.getNGValue(exchangerateId);
//            if (!exchange.equalsIgnoreCase("")) {
//                BigDecimal exchanger = new BigDecimal(exchange);
//                bamount = bamount.multiply(exchanger.setScale(2, BigDecimal.ROUND_FLOOR));
//                bamount = bamount.setScale(2, BigDecimal.ROUND_FLOOR);
//                formObject.setNGValue(newbaseamountId, bamount.toString());
//            }
//        } else {
//            formObject.setNGValue(newbaseamountId, baseamount);
//        }
//    }
    public void exronCurrencyChange(String currencyId, String baseamountId, String newbaseamountId, String exchangerateId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside exr currency change: ");
        String currency = formObject.getNGValue(currencyId);
        String baseamount = formObject.getNGValue(baseamountId);
        System.out.println("Currency: " + currency + " Base" + baseamount);
        if (!currency.equalsIgnoreCase("INR")) {
            System.out.println("Inside !INR");
            formObject.setNGValue(newbaseamountId, baseamount);
            formObject.setEnabled(exchangerateId, true);
            formObject.setNGValue(exchangerateId, "0");
        } else {
            formObject.setEnabled(exchangerateId, false);
            formObject.setNGValue(newbaseamountId, baseamount);
            formObject.setNGValue(exchangerateId, "0");
        }
    }
    
    public void exronCurrencyChangePoProcess(String currencyId, String baseamountId, String newbaseamountId, String exchangerateId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside exr currency change: ");
        String currency = formObject.getNGValue(currencyId);
        String baseamount = formObject.getNGValue(baseamountId);
        System.out.println("Currency: " + currency + " Base" + baseamount);
        if (!currency.equalsIgnoreCase("INR")) {
            System.out.println("Inside !INR");
            formObject.setNGValue(newbaseamountId, baseamount);
            formObject.setEnabled(exchangerateId, true);
//            formObject.setNGValue(exchangerateId, "");
        } else {
            formObject.setEnabled(exchangerateId, false);
            formObject.setNGValue(newbaseamountId, baseamount);
            formObject.setNGValue(exchangerateId, "");
        }
    }

    public String calculateLowerTDS(String Amount, String TDS_Percent, String TDS_Group) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside Lower_TDS");
        String Updated_AdjustedTDSAmount = "", Update_TDS_Percent = "";
        float Total_Amount, Limit, NewAdjusted_Origin_Amount, Adjusted_Origin_Amount = Float.parseFloat(Amount);
        System.out.println("Adjusted Origin Amount: " + Adjusted_Origin_Amount);
        try {
            Date invoicedate = new SimpleDateFormat("dd/MM/yyyy").parse(formObject.getNGValue("invoicedate"));
            System.out.println("invoice date after: " + invoicedate);
            Query = "Select From_Date, To_Date from ServicePolowerTDSMaster where VendorCode='" + formObject.getNGValue("suppliercode") + "'";
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result of Query: " + result);
            if (result.size() > 0) {
                Date to_date = new SimpleDateFormat("yyyy-MM-dd").parse(result.get(0).get(1).substring(0, 10));
                Date from_date = new SimpleDateFormat("yyyy-MM-dd").parse(result.get(0).get(0).substring(0, 10));
                if (invoicedate.compareTo(from_date) >= 0 && invoicedate.compareTo(to_date) <= 0) {
                    Query = "select count(*) from ServicePolowerTDSMaster where VendorCode = '" + formObject.getNGValue("suppliercode") + "' and TDSCode = '" + TDS_Group + "'";
                    System.out.println("Query: " + Query);
                    result = formObject.getDataFromDataSource(Query);
                    if (!("0".equalsIgnoreCase(result.get(0).get(0)))) {
                        System.out.println("inside if statement of result1");
                        Query = "Select Limit,Total_Amount,TDSPercentAfter, TaxPercent, Flag, From_Date, To_Date from ServicePolowerTDSMaster where VendorCode='" + formObject.getNGValue("suppliercode") + "'";
                        System.out.println("Query: " + Query);
                        result = formObject.getDataFromDataSource(Query);
                        System.out.println("result of Query: " + result);
                        if (result.size() > 0) {
                            Limit = Float.parseFloat(result.get(0).get(0));
                            System.out.println("Limit: " + Limit);
                            if (result.get(0).get(1).equalsIgnoreCase("") || result.get(0).get(1).equalsIgnoreCase(" ")) {
                                Total_Amount = 0.0f;
                            } else {
                                Total_Amount = Float.parseFloat(result.get(0).get(1));
                            }
                            System.out.println("Total Amount: " + Total_Amount);
                            NewAdjusted_Origin_Amount = Total_Amount + Adjusted_Origin_Amount;
                            System.out.println("NewAdjusted Origin Data: " + NewAdjusted_Origin_Amount);
                            if (result.get(0).get(4).equalsIgnoreCase("F")) {
                                if (NewAdjusted_Origin_Amount > Limit) {
                                    System.out.println("Inside first if");
                                    float OverLimitAmount = NewAdjusted_Origin_Amount - Limit;
                                    float LimitedAdjustedAmount = Limit - Total_Amount;
                                    float AdjustedTDSAmount_1 = Float.parseFloat(calculatePercentAmount(String.valueOf(OverLimitAmount), result.get(0).get(2)));
                                    float AdjustedTDSAmount_2 = Float.parseFloat(calculatePercentAmount(String.valueOf(LimitedAdjustedAmount), result.get(0).get(3)));
                                    Updated_AdjustedTDSAmount = String.valueOf(AdjustedTDSAmount_1 + AdjustedTDSAmount_2);
                                    Update_TDS_Percent = result.get(0).get(2);
                                    Query = "update ServicePolowerTDSMaster set Total_Amount = '" + NewAdjusted_Origin_Amount + "', Flag = 'T' where PanNumber = 'as'";
                                    System.out.println("Query1: " + Query);
                                    formObject.saveDataIntoDataSource(Query);

                                } else {
                                    System.out.println("inside first else if");
                                    Updated_AdjustedTDSAmount = calculatePercentAmount(String.valueOf(Adjusted_Origin_Amount), result.get(0).get(3));
                                    Update_TDS_Percent = TDS_Percent;
                                    Query = "update ServicePolowerTDSMaster set Total_Amount = '" + NewAdjusted_Origin_Amount + "'where PanNumber = 'as'";
                                    System.out.println("Query2: " + Query);
                                    formObject.saveDataIntoDataSource(Query);
                                }
                            } else {
                                System.out.println("inside first else if");
                                Updated_AdjustedTDSAmount = calculatePercentAmount(String.valueOf(Adjusted_Origin_Amount), result.get(0).get(3));
                                Update_TDS_Percent = result.get(0).get(2);
                                Query = "update ServicePolowerTDSMaster set Total_Amount = '" + NewAdjusted_Origin_Amount + "'where PanNumber = 'as'";
                                System.out.println("Query2: " + Query);
                                formObject.saveDataIntoDataSource(Query);

                            }
                        }
//                    else {
//                        System.out.println("inside else of result ");
//                        Updated_AdjustedTDSAmount = calculatePercentAmount(String.valueOf(Adjusted_Origin_Amount), TDS_Percent);
//                        Query = "update ServicePolowerTDSMaster set Total_Amount = '" + NewAdjusted_Origin_Amount + "'where PanNumber = 'as'";
//                        System.out.println("Query2: " + Query);
//                        formObject.saveDataIntoDataSource(Query);
//                    }
                    } else {
                        System.out.println("inside else of result ");
                        Update_TDS_Percent = TDS_Percent;
                        Updated_AdjustedTDSAmount = calculatePercentAmount(String.valueOf(Adjusted_Origin_Amount), TDS_Percent);
                    }
                }
            } else {
                System.out.println("inside else of result ");
                Update_TDS_Percent = TDS_Percent;
                Updated_AdjustedTDSAmount = calculatePercentAmount(String.valueOf(Adjusted_Origin_Amount), TDS_Percent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("LOwerTDS Result: Updated_AdjustedTDSAmount + \"#\" + Update_TDS_Percent");
        return Updated_AdjustedTDSAmount + "#" + Update_TDS_Percent;
    }

}
