/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.SupplyPoInvoices;

import com.newgen.Webservice.CallPurchaseOrderService;
import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.excp.CustomExceptionHandler;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author V_AWC
 */
public class MultipleGrnGeneral implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String Query, link = "", engineName = "", serverUrl = "", processInstanceId = "", workItemId = "",
            userName = "", UserIndex = "", ip = "";
    List<List<String>> result;
    General objGeneral = null;
    HashMap<String, String> hm = null;

    public void grnStartEndDateChange() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        boolean ShowMessage = false;
        String AlertMessage = "";
        //Add Delink WI Call Here
 //       int rowcount = formObject.getLVWRowCount("q_multiplegrninvoicing");
//        for (int i = 0; i < rowcount; i++) {
//            objGeneral.linkWorkitem(
//                    formConfig.getConfigElement("EngineName"),
//                    formConfig.getConfigElement("DMSSessionId"),
//                    formConfig.getConfigElement("ProcessInstanceId"),
//                    formObject.getNGValue("q_multiplegrninvoicing", i, 0),
//                    "D"
//            );
//        }
        //Delete Query Here
        Query = "Delete from cmplx_multiplegrninvoicing where pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "'";
        formObject.saveDataIntoDataSource(Query);
        formObject.clear("q_grn");
        formObject.clear("q_multiplegrninvoicing");

        Query = "select ext.grnnumber from ext_supplypoinvoices ext,WFINSTRUMENTTABLE wf "
                + "where wf.ProcessInstanceID = ext.processid "
                + "and wf.ActivityName = 'HoldMultipleGRN' "
                + "and ext.grnnumber not in(select grnnumber from cmplx_multiplegrninvoicing)"
                + "and purchaseorderno = '" + formObject.getNGValue("purchaseorderno") + "' "
                + "and format(grnsyncdate,'dd/MM/yyyy') "
                + "between '" + formObject.getNGValue("grnstartdate") + "' "
                + "and '" + formObject.getNGValue("grnenddate") + "'";
        System.out.println("Query: " + Query);
        List<List<String>> grnresult = formObject.getDataFromDataSource(Query);
        for (int i = 0; i < grnresult.size(); i++) {
            String grnnumber = grnresult.get(i).get(0);
            formObject.addComboItem("q_grn", grnnumber, grnnumber);
            String LockStatus = addMultipleGRN(grnnumber);
            if (LockStatus.equalsIgnoreCase("UnLocked")) {
                AlertMessage = AlertMessage;
            } else {
                ShowMessage = true;
                if (AlertMessage.equals("")) {
                    AlertMessage = LockStatus;
                } else {
                    AlertMessage = AlertMessage + " , " + LockStatus;
                }
            }
        }
        if (ShowMessage) {
            formObject.RaiseEvent("WFSave");
            throw new ValidatorException(new FacesMessage("Process Instance Id not added as : " + AlertMessage, ""));
        } else {
            formObject.RaiseEvent("WFSave");
        }
    }

    public void addMultipleGrnClick(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String grnnumber = formObject.getNGValue("q_grn");
        Query = "select count(*) from cmplx_multiplegrninvoicing where "
                + "pinstanceid = '" + processInstanceId + "' and grnnumber = '" + grnnumber + "'";
        System.out.println("Query : " + Query);

        if (formObject.getDataFromDataSource(Query).get(0).get(0).equals("0")) {
            String LockStatus = addMultipleGRN(grnnumber);
            if (LockStatus.equalsIgnoreCase("UnLocked")) {
                formObject.clear("q_polines");
                formObject.clear("q_gateentrylines");
                formObject.clear("q_invoiceline");
                formObject.clear("q_othercharges");
                formObject.clear("q_withholdingtax");
                formObject.clear("q_taxdocument");
                formObject.clear("q_prepayment");
                formObject.setNGValue("retentioncredit", "");
                formObject.setNGValue("retentionpercent", "");
                formObject.setNGValue("retentioncredit", "");
                formObject.setNGValue("retentionamount", "");
                formObject.setNGValue("companytaxinformation", "");
                formObject.setNGValue("companyaddress", "");
                formObject.setNGValue("vendortaxinformation", "");
                formObject.setNGValue("vendoraddress", "");
                formObject.RaiseEvent("WFSave");
            } else {
                throw new ValidatorException(new FacesMessage("Process Instance Id not added as : " + LockStatus, ""));
            }
        } else {
            throw new ValidatorException(new FacesMessage("GRN number already added.", ""));
        }
    }

    public void deleteMultipleGrnClick() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        formObject.clear("q_polines");
        formObject.clear("q_gateentrylines");
        formObject.clear("q_invoiceline");
        formObject.clear("q_othercharges");
        formObject.clear("q_withholdingtax");
        formObject.clear("q_taxdocument");
        formObject.clear("q_prepayment");
        formObject.setNGValue("retentioncredit", "");
        formObject.setNGValue("retentionpercent", "");
        formObject.setNGValue("retentioncredit", "");
        formObject.setNGValue("retentionamount", "");
        formObject.setNGValue("companytaxinformation", "");
        formObject.setNGValue("companyaddress", "");
        formObject.setNGValue("vendortaxinformation", "");
        formObject.setNGValue("vendoraddress", "");

        //loop of selected rows - 
        int[] selectedRowscount = formObject.getNGLVWSelectedRows("q_multiplegrninvoicing");
        System.out.println("selectedRowscount : " + selectedRowscount.length);
        for (int i = 0; i < selectedRowscount.length; i++) {
            //   formObject.getSelectedIndex("");
            String qmgrn_pid = formObject.getNGValue("q_multiplegrninvoicing", i, 0);
            // String qmgrn_pid = formObject.getNGValue("qmgrn_pid");
//        formObject.ExecuteExternalCommand("NGDeleteRow", "q_multiplegrninvoicing");
            Query = "update WFINSTRUMENTTABLE set VAR_STR16 = 'StoreMakerChecker' "
                    + "where ProcessInstanceID = '" + qmgrn_pid + "'";
            formObject.saveDataIntoDataSource(Query);
//            objGeneral.linkWorkitem(
//                    formConfig.getConfigElement("EngineName"),
//                    formConfig.getConfigElement("DMSSessionId"),
//                    formConfig.getConfigElement("ProcessInstanceId"),
//                    qmgrn_pid,
//                    "D"
//            );
        }
        //loop end

        formObject.removeSelectedRows("q_multiplegrninvoicing");
        formObject.RaiseEvent("WFSave");
    }

    public void combineMultipleGrnClick() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("button click Btn_combine");
        formObject.clear("q_polines");
        formObject.clear("q_gateentrylines");
        formObject.clear("q_invoiceline");
        formObject.clear("q_othercharges");
        formObject.clear("q_withholdingtax");
        formObject.clear("q_taxdocument");
        formObject.clear("q_prepayment");
        formObject.clear("retentioncredit");
        formObject.clear("retentionpercent");
        formObject.clear("retentionamount");
        formObject.clear("companytaxinformation");
        formObject.clear("companyaddress");
        formObject.clear("vendortaxinformation");
        formObject.clear("vendoraddress");
        CombineMultipleGrn();
    }

    public void cancelGrnClick() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        System.out.println("Inside cancel grn click");
        String grnpid = formObject.getNGValue("qmgrn_pid");

        Query = "update WFINSTRUMENTTABLE set VAR_STR16 = 'StoreMakerChecker' "
                + "where ProcessInstanceID = '" + grnpid + "'";
        formObject.saveDataIntoDataSource(Query);

        Query = "select ActivityId from WFINSTRUMENTTABLE "
                + "where ProcessInstanceID = '" + grnpid + "'";
        System.out.println("Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        objGeneral.forwardWI(
                formConfig.getConfigElement("EngineName"),
                formConfig.getConfigElement("DMSSessionId"),
                grnpid,
                formConfig.getConfigElement("ProcessDefId"),
                result.get(0).get(0),
                "<previousstatus>Create Reversal GRN</previousstatus><FilterDoA_ApproverLevel>StoreMaker</FilterDoA_ApproverLevel>"
        );

        formObject.ExecuteExternalCommand("NGDeleteRow", "q_multiplegrninvoicing");
        formObject.clear("q_polines");
        formObject.clear("q_gateentrylines");
        formObject.clear("q_invoiceline");
        formObject.clear("q_othercharges");
        formObject.clear("q_withholdingtax");
        formObject.clear("q_taxdocument");
        formObject.clear("q_prepayment");
        formObject.setNGValue("retentioncredit", "");
        formObject.setNGValue("retentionpercent", "");
        formObject.setNGValue("retentioncredit", "");
        formObject.setNGValue("retentionamount", "");
        formObject.setNGValue("companytaxinformation", "");
        formObject.setNGValue("companyaddress", "");
        formObject.setNGValue("vendortaxinformation", "");
        formObject.setNGValue("vendoraddress", "");

//        objGeneral.linkWorkitem(
//                formConfig.getConfigElement("EngineName"),
//                formConfig.getConfigElement("DMSSessionId"),
//                formConfig.getConfigElement("ProcessInstanceId"),
//                grnpid,
//                "D"
//        );
        formObject.RaiseEvent("WFSave");

    }

    String addMultipleGRN(String GRNnumber) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objGeneral = new General();
        String LockMessage = "";
        System.out.println("Inside btn add multiple grn");
        Query = "select processid , grnnumber,  format(grnsyncdate,'dd/MM/yyyy'), gateentryid, invoiceno,format(invoicedate,'dd/MM/yyyy'), "
                + "invoiceamount, lrno, format(lrdate,'dd/MM/yyyy'), loadingcity, transportercode, transportername, "
                + "vehicleno from ext_supplypoinvoices where grnnumber = '" + GRNnumber + "'";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.size() > 0) {
            LockMessage = CheckLockStatus(result.get(0).get(0));
            if (LockMessage.equalsIgnoreCase("UnLocked")) {
                String MultipleGrnXML = "";
                MultipleGrnXML = (new StringBuilder()).append(MultipleGrnXML).
                        append("<ListItem><SubItem>").append(result.get(0).get(0)). //pid
                        append("</SubItem><SubItem>").append(result.get(0).get(1)). //grn number
                        append("</SubItem><SubItem>").append(result.get(0).get(2)). //grn date
                        append("</SubItem><SubItem>").append(result.get(0).get(3)). //gate netry id
                        append("</SubItem><SubItem>").append(result.get(0).get(4)). //invoice number
                        append("</SubItem><SubItem>").append(result.get(0).get(5)). //invoice date
                        append("</SubItem><SubItem>").append(result.get(0).get(6)). //invoice amount
                        append("</SubItem><SubItem>").append(result.get(0).get(7)). //lr number
                        append("</SubItem><SubItem>").append(result.get(0).get(8)). //lr date
                        append("</SubItem><SubItem>").append(result.get(0).get(9)). //loading city
                        append("</SubItem><SubItem>").append(result.get(0).get(10)). //transporter code
                        append("</SubItem><SubItem>").append(result.get(0).get(11)). //transporter name
                        append("</SubItem><SubItem>").append(result.get(0).get(12)). //vehicle number
                        append("</SubItem></ListItem>").toString();

                System.out.println("XML :" + MultipleGrnXML);
                formObject.NGAddListItem("q_multiplegrninvoicing", MultipleGrnXML);
//                objGeneral.linkWorkitem(
//                        formConfig.getConfigElement("EngineName"),
//                        formConfig.getConfigElement("DMSSessionId"),
//                        formConfig.getConfigElement("ProcessInstanceId"),
//                        result.get(0).get(0),
//                        "A"
//                );
                formObject.RaiseEvent("WFSave");
            }
        }
        return LockMessage;
    }

    void CombineMultipleGrn() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("inside CombineMultipleGrn");
        Query = "select linenumber, itemid, itemname, challanqty, grnqty, wbfirstwt,wbsecondwt, "
                + "wbnetweight, ponumber,freight,businessunit,state,costcentergroup,costcenter,department,gla,pinstanceid "
                + "from cmplx_gateentryline where pinstanceid in (select grnprocessid from cmplx_multiplegrninvoicing "
                + "where pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "')";
        System.out.println("Query :" + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result : " + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println("inside for " + i);
            int rowcount = formObject.getLVWRowCount("q_gateentrylines");
            String GateLineContractXML = "";
            String q_linenumber = result.get(i).get(0);
            String q_itemnumber = result.get(i).get(1);

            if (rowcount == 0 && i == 0) {
                System.out.println("inside if of ==zero");
                GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                        append("<ListItem><SubItem>").append(result.get(0).get(0)).
                        append("</SubItem><SubItem>").append(result.get(0).get(1)).
                        append("</SubItem><SubItem>").append(result.get(0).get(2)).
                        append("</SubItem><SubItem>").append(result.get(0).get(3)).
                        append("</SubItem><SubItem>").append(result.get(0).get(4)).
                        append("</SubItem><SubItem>").append(result.get(0).get(5)).
                        append("</SubItem><SubItem>").append(result.get(0).get(6)).
                        append("</SubItem><SubItem>").append(result.get(0).get(7)).
                        append("</SubItem><SubItem>").append(result.get(0).get(8)).
                        append("</SubItem><SubItem>").append(result.get(0).get(9)).
                        append("</SubItem><SubItem>").append(result.get(0).get(10)).
                        append("</SubItem><SubItem>").append(result.get(0).get(11)).
                        append("</SubItem><SubItem>").append(result.get(0).get(12)).
                        append("</SubItem><SubItem>").append(result.get(0).get(13)).
                        append("</SubItem><SubItem>").append(result.get(0).get(14)).
                        append("</SubItem><SubItem>").append(result.get(0).get(15)).
                        append("</SubItem></ListItem>").toString();
                System.out.println("GateLineContractXML :" + GateLineContractXML);
                formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
            } else {
                System.out.println("inside else");
                boolean itemexistflag = false;
                for (int j = 0; j < rowcount; j++) {
                    String ge_linenumber = formObject.getNGValue("q_gateentrylines", j, 0);
                    String ge_itemnumber = formObject.getNGValue("q_gateentrylines", j, 1);

                    if (ge_itemnumber.equalsIgnoreCase(q_itemnumber)
                            && ge_linenumber.equals(q_linenumber)) {
                        System.out.println("Item matched breaking loop");
                        itemexistflag = true;

                        BigDecimal challanqty = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 3));
                        BigDecimal grnqty = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 4));
                        BigDecimal wbfirstwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 5));
                        BigDecimal wbsecondwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 6));
                        BigDecimal wbnetwt = new BigDecimal(formObject.getNGValue("q_gateentrylines", j, 7));

                        BigDecimal q_challanqty = new BigDecimal(result.get(i).get(3));
                        BigDecimal q_grnqty = new BigDecimal(result.get(i).get(4));
                        BigDecimal q_wbfirstwt = new BigDecimal(result.get(i).get(5));
                        BigDecimal q_wbsecondwt = new BigDecimal(result.get(i).get(6));
                        BigDecimal q_wbnetwt = new BigDecimal(result.get(i).get(7));

                        formObject.setNGValue("q_gateentrylines", j, 3, challanqty.add(q_challanqty).toString());
                        formObject.setNGValue("q_gateentrylines", j, 4, grnqty.add(q_grnqty).toString());
                        formObject.setNGValue("q_gateentrylines", j, 5, wbfirstwt.add(q_wbfirstwt).toString());
                        formObject.setNGValue("q_gateentrylines", j, 6, wbsecondwt.add(q_wbsecondwt).toString());
                        formObject.setNGValue("q_gateentrylines", j, 7, wbnetwt.add(q_wbnetwt).toString());

                        break;
                    }
                }

                if (itemexistflag == false) {
                    System.out.println("Item does not matched");
                    GateLineContractXML = (new StringBuilder()).append(GateLineContractXML).
                            append("<ListItem><SubItem>").append(result.get(i).get(0)).
                            append("</SubItem><SubItem>").append(result.get(i).get(1)).
                            append("</SubItem><SubItem>").append(result.get(i).get(2)).
                            append("</SubItem><SubItem>").append(result.get(i).get(3)).
                            append("</SubItem><SubItem>").append(result.get(i).get(4)).
                            append("</SubItem><SubItem>").append(result.get(i).get(5)).
                            append("</SubItem><SubItem>").append(result.get(i).get(6)).
                            append("</SubItem><SubItem>").append(result.get(i).get(7)).
                            append("</SubItem><SubItem>").append(result.get(i).get(8)).
                            append("</SubItem><SubItem>").append(result.get(i).get(9)).
                            append("</SubItem><SubItem>").append(result.get(i).get(10)).
                            append("</SubItem><SubItem>").append(result.get(i).get(11)).
                            append("</SubItem><SubItem>").append(result.get(i).get(12)).
                            append("</SubItem><SubItem>").append(result.get(i).get(13)).
                            append("</SubItem><SubItem>").append(result.get(i).get(14)).
                            append("</SubItem><SubItem>").append(result.get(i).get(15)).
                            append("</SubItem></ListItem>").toString();
                    System.out.println("GateLineContractXML :" + GateLineContractXML);
                    formObject.NGAddListItem("q_gateentrylines", GateLineContractXML);
                }
            }
//                            String AccessToken = new CallAccessTokenService().getAccessToken();
            new CallPurchaseOrderService().GetSetPurchaseOrder("", "Supply", formObject.getNGValue("purchaseorderno"), "Supply");
        }
        formObject.RaiseEvent("WFSave");
    }

    String CheckLockStatus(String ProcessInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String LockMessage = "UnLocked";
        String LockStatusQuery = "select LockStatus,LockedByName,ProcessInstanceID from WFINSTRUMENTTABLE "
                + "where ProcessInstanceID = '" + ProcessInstanceId + "'";
        List<List<String>> LockStatusResult = formObject.getDataFromDataSource(LockStatusQuery);
        if (LockStatusResult.get(0).get(0).equals("Y")) {
            LockMessage = LockStatusResult.get(0).get(2) + " is Locked by " + LockStatusResult.get(0).get(1);
        } else {
            Query = "update WFINSTRUMENTTABLE set VAR_STR16 = 'HoldMultipleGRN' "
                    + "where ProcessInstanceID = '" + ProcessInstanceId + "'";
            formObject.saveDataIntoDataSource(Query);
        }
        return LockMessage;
    }

    public void viewwi(String ProcessinstanceID) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("form congif xml " + formConfig.getConfigXML());
        engineName = formConfig.getConfigElement("EngineName");
        serverUrl = formConfig.getConfigElement("ServletPath");
        workItemId = formConfig.getConfigElement("WorkitemId");
        userName = formConfig.getConfigElement("UserName");
        UserIndex = formConfig.getConfigElement("UserIndex");
        hm = new HashMap<>();
        System.out.println("serverUrl 1== " + serverUrl);
        serverUrl = serverUrl.split("webdesktop")[0];
        System.out.println("serverUrl == " + serverUrl);
        ip = serverUrl.replaceAll("//", "/");

        link = "" + serverUrl + "webdesktop/login/loginapp.jsf?WDDomHost=" + ip.split("/")[1] + ""
                + "&CalledFrom=OPENWI&CabinetName=" + engineName + "&UserName=" + userName + "&UserIndex=" + UserIndex + ""
                + "&pid=" + ProcessinstanceID + "&wid=" + workItemId + "&OAPDomHost=" + ip.split("/")[1] + "";

        System.out.println("Link: " + link);

        throw new ValidatorException(new CustomExceptionHandler("viewwi", link, "", hm));
    }
}
