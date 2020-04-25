package com.newgen.common;

import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.Panel;
import com.newgen.omniforms.context.FormContext;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;

public class General implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    General objGeneral = null;
    String Query = null, assign = null;
    Calendar c = Calendar.getInstance();
    Date currentDate = new Date();
    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    List<List<String>> resultarray, userarray, historyarray, result;
    public static NGEjbClient ngEjbClient = null;
    XMLParser xmlParser = new XMLParser();

    public static String executeWithCallBroker(String inputXml) {
        String outputXml = "";
        try {
            System.out.println("INPUT: " + inputXml);
            // outputXml = DMSCallBroker.execute(inputXml, "192.168.10.59", Short.parseShort("3333"), 0);
            ngEjbClient = NGEjbClient.getSharedInstance();
            //  outputXml = ngEJBClient.makeCall(serverIP, serverPort, serverType, inputXml);
            outputXml = ngEjbClient.makeCall("192.168.10.59", "8080", "JBOSSEAP", inputXml);//makeCall(inputXml);
            System.out.println("OUTPUT: " + outputXml);
        } catch (NGException ex) {
            Logger.getLogger(General.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputXml;
    }

    public static String convertNewgenDateToSapDate(String sDate) {
        try {
            String formatDate = "";
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date sDate_temp = formatter.parse(sDate);
            formatDate = formatter1.format(sDate_temp);
            return formatDate;
        } catch (ParseException ex) {
            System.out.println("Error while convertin date: " + ex);
            return null;
        }
    }

    public static String convertSqlDateTonewgenDate(String sDate) {
        try {
            String formatDate = "";
            DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date sDate_temp = formatter1.parse(sDate);
            System.out.println("Date parse : " + sDate_temp);
            formatDate = formatter.format(sDate_temp);
            System.out.println("Date format : " + formatDate);
            return formatDate;
        } catch (ParseException ex) {
            System.out.println("Error while convertin date: " + ex);
            return null;
        }
    }

    public String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dateobj = new Date();
        System.out.println("Current Date :" + df.format(dateobj));
        return df.format(dateobj);
    }

    public String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date dateobj = new Date();
        System.out.println("Current Date :" + df.format(dateobj));
        return df.format(dateobj);
    }

    public String getCurrDateForRange() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date dateobj = new Date();
        System.out.println("Current Date :" + df.format(dateobj));
        return df.format(dateobj);
    }

    public String getEmailBody(String usernamedoa, String processid) {

        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        String body = "", userindex = "";
        Query = "select UserIndex from PDBUser where UserName='" + usernamedoa + "'";
        System.out.println("Query for userindex : " + Query);
        resultarray = formObject.getDataFromDataSource(Query);
        userindex = resultarray.get(0).get(0);
        System.out.println("userindex : " + userindex);
        body = "http://192.168.10.58:8080/webdesktop/login/loginapp.jsf?WDDomHost=192.168.10.58:8080"
                + "&CalledFrom=OPENWI&"
                + "CabinetName=orient_uat&"
                + "UserName=" + usernamedoa + "&"
                + "UserIndex=" + userindex + "&"
                + "pid=" + processid + "&"
                + "wid=1&OAPDomHost=192.168.10.58:8080";

        return body;
    }

    public boolean isRepRowDeleted(String frameName, int index) {
        // FormReference formObject = FormContext.getCurrentInstance().getFormReference();        
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        UIComponent objComp = formObject.getComponent(frameName);

        if (objComp instanceof Panel) {
            Panel objPanel = (Panel) objComp;
            if (index > -1) {
                if (objPanel.getObjRowStatusMap() != null && objPanel.getObjRowStatusMap().containsKey(index)) {
                    String[] ar = objPanel.getObjRowStatusMap().get(index);
                    String insertionOrdId = ar[1];
                    if (insertionOrdId != null && !"".equals(insertionOrdId)) {
                        int insertOrdId = Integer.parseInt(insertionOrdId);
                        if (insertOrdId < 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public String getHSNSACRate(String hsnsaccodetype, String hsnsaccodevalue, String TaxComponent) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        String HSNSACrate = "";
        String SelectColumn = "";
        System.out.println("hsnsaccodetype" + hsnsaccodetype);
        System.out.println("hsnsaccodevalue" + hsnsaccodevalue);
        System.out.println("TaxComponenet" + TaxComponent);
        if (TaxComponent.equalsIgnoreCase("CGST")) {
            System.out.println("CGST");
            SelectColumn = "CGSTRate";
        } else if (TaxComponent.equalsIgnoreCase("SGST")) {
            System.out.println("SGST");
            SelectColumn = "SGSTRate";
        } else if (TaxComponent.equalsIgnoreCase("IGST")) {
            System.out.println("IGST");
            SelectColumn = "IGSTRate";
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
        resultarray = formObject.getDataFromDataSource(Query);
        if (resultarray.size() > 0) {
            HSNSACrate = resultarray.get(0).get(0);
            System.out.println("HSNSACrate :" + HSNSACrate);
        } else {
            throw new ValidatorException(new FacesMessage("Rate is not defined in the HSN/SAC rate master for " + TaxComponent + "=" + hsnsaccodevalue));
        }
        return HSNSACrate;
    }

    public void maintainHistory(String username, String activityName, String status, String exception, String remarks, String ListviewID) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("Inside transaction history" + ListviewID);
        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatter1 = new SimpleDateFormat("hh:mm aa");
            String currentdate = formatter.format(date);

            String historylistview = "<ListItem>"
                    + "<SubItem>" + username + "</SubItem>"
                    + "<SubItem>" + currentdate + "</SubItem>"
                    + "<SubItem>" + activityName + "</SubItem>"
                    + "<SubItem>" + status + "</SubItem>"
                    + "<SubItem>" + exception + "</SubItem>"
                    + "<SubItem>" + remarks + "</SubItem>"
                    + "</ListItem>";
            System.out.println("History list" + historylistview);
            formObject.NGAddListItem(ListviewID, historylistview);
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
    }

//    public boolean checkInvoiceDuplicity(String ProcessName, String InvoiceNo, String PurchaseOrderNo, String processInstanceId) {
//        formObject = FormContext.getCurrentInstance().getFormReference();
//        System.out.println("Inside invoice duplicity check");
//        boolean InvoiceExist = false;
//        if (ProcessName.equalsIgnoreCase("ServicePoInvoice")) {
//            Query = "select count(*) from ext_servicepoinvoice ext, WFINSTRUMENTTABLE wf, cmplx_multiplepo cmplx "
//                    + "where ext.processid = wf.ProcessInstanceID "
//                    + "and ext.processid = cmplx.pinstanceid "
//                    + "and wf.ActivityName not in ('Discard') "
//                    + "and ext.invoicenumber = '" + InvoiceNo + "' "
//                    + "and cmplx.purchaseorderno in (" + PurchaseOrderNo + ") "
//                    + "and ext.processid <> '" + processInstanceId + "'";
//        }
//        System.out.println("Query :" + Query);
//        String count = formObject.getDataFromDataSource(Query).get(0).get(0);
//        System.out.println("Count :" + count);
//        if (count.equals("0")) {
//            InvoiceExist = false;
//        } else {
//            InvoiceExist = true;
//            throw new ValidatorException(new FacesMessage("Duplicate Invoice Number!! Invoice with same invoice number has been already processed."));
//        }
//        return InvoiceExist;
//    }
    public void setException(String userName, String exceptionReasonFieldId, String remarksFieldId) {
        formObject = FormContext.getCurrentInstance().getFormReference();

        String exceptionlistview = "<ListItem>"
                + "<SubItem>" + getCurrentDate() + "</SubItem>"
                + "<SubItem>" + userName + "</SubItem>"
                + "<SubItem>" + formObject.getNGValue(exceptionReasonFieldId) + "</SubItem>"
                + "<SubItem>" + formObject.getNGValue(remarksFieldId) + "</SubItem>"
                + "<SubItem>" + "No" + "</SubItem>"
                + "<SubItem></SubItem>"
                + "<SubItem></SubItem>"
                + "<SubItem></SubItem>"
                + "</ListItem>";
        System.out.println("Exception list" + exceptionlistview);
        formObject.NGAddListItem("q_wfexception", exceptionlistview);

    }

    public void setFiscalYear(String s_InvoiceDate, String FiscalYearFieldId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        try {
            int FiscalYear;
            Date d = new Date();
            int year = d.getYear();
            int currentYear = year + 1900;
            String s_fiscaldate = "01/04/" + currentYear;
//            String s_invoicedate = "11/06/2020";
            Date invoicedate = new SimpleDateFormat("dd/MM/yyyy").parse(s_InvoiceDate);
            Date fiscaldate = new SimpleDateFormat("dd/MM/yyyy").parse(s_fiscaldate);
            if (fiscaldate.compareTo(invoicedate) == -1
                    || fiscaldate.compareTo(invoicedate) == 0) {
                FiscalYear = currentYear;
            } else {
                FiscalYear = currentYear - 1;
            }
            formObject.setNGValue(FiscalYearFieldId, FiscalYear);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public BigDecimal getServicePoRemainingQty(BigDecimal remainingqty) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        Query = "select COALESCE(SUM(CAST(cmplx.quantity as numeric(38,2))),0) from cmplx_invoicedetails cmplx, "
                + "WFINSTRUMENTTABLE wf where cmplx.pinstanceid = wf.ProcessInstanceID "
                + "and cmplx.purchaseorderno = '" + formObject.getNGValue("qpo_ponumber") + "' "
                + "and cmplx.linenumber =  '" + formObject.getNGValue("qpo_linenumber") + "' "
                + "and cmplx.itemid = '" + formObject.getNGValue("qpo_itemnumber") + "' "
                + "and ActivityName not in ('Discard' , 'End')";
        System.out.println("Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        return remainingqty.subtract(new BigDecimal(result.get(0).get(0)));
    }

    public BigDecimal getRABillRemainingQty(BigDecimal remainingqty) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        Query = "select COALESCE(SUM(CAST(cmplx.currentqty as numeric(38,2))),0) from cmplx_raabstractsheet cmplx, "
                + "WFINSTRUMENTTABLE wf where cmplx.pinstanceid = wf.ProcessInstanceID "
                + "and cmplx.purchaseorderno = '" + formObject.getNGValue("purchaseorder") + "' "
                + "and cmplx.linenumber =  '" + formObject.getNGValue("qpo_linenumber") + "' "
                + "and cmplx.itemnumber = '" + formObject.getNGValue("qpo_itemnumber") + "' "
                + "and ActivityName not in ('Discard' , 'End')";
        System.out.println("Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        return remainingqty.subtract(new BigDecimal(result.get(0).get(0)));
    }

    public boolean checkDuplicateInvoice(String VendorCode, String InvoiceNumber, String FiscalYear, String ProcessInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();

        boolean InvoiceExist = false;
        Query = "Select ("
                + "(select count(*) from ext_nonpoinvoice ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Initiator', 'Discard') "
                + "and ext.accountcode = '" + VendorCode + "' "
                + "and ext.invoicenumber = '" + InvoiceNumber + "' "
                + "and ext.fiscalyear = '" + FiscalYear + "' "
                + "and ext.processid <> '" + ProcessInstanceId + "') "
                + "+ "
                + "(select count(*) from ext_servicepoinvoice ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Initiator', 'Discard') "
                + "and ext.suppliercode = '" + VendorCode + "' "
                + "and ext.invoicenumber = '" + InvoiceNumber + "' "
                + "and ext.fiscalyear = '" + FiscalYear + "' "
                + "and ext.processid <> '" + ProcessInstanceId + "') "
                + "+ "
                + "(select count(*) from ext_rabill ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Indexer', 'Discard') "
                + "and ext.contractor = '" + VendorCode + "' "
                + "and ext.invoicenumber = '" + InvoiceNumber + "' "
                + "and ext.fiscalyear = '" + FiscalYear + "' "
                + "and ext.processid <> '" + ProcessInstanceId + "') "
                + "+ "
                + "(select count(*) from ext_supplypoinvoices ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Initiator', 'Discard') "
                + "and ext.suppliercode = '" + VendorCode + "' "
                + "and ext.invoiceno = '" + InvoiceNumber + "' "
                + "and ext.fiscalyear = '" + FiscalYear + "' "
                + "and ext.processid <> '" + ProcessInstanceId + "') "
                + ") as TotalCount";

        System.out.println("Query :" + Query);
        String count = formObject.getDataFromDataSource(Query).get(0).get(0);
        System.out.println("Count :" + count);
        if (count.equals("0")) {
        } else {
            InvoiceExist = true;
            throw new ValidatorException(new FacesMessage("Duplicate Invoice Number!! Invoice with same invoice number has been already processed."));
        }
        return InvoiceExist;
    }

}
