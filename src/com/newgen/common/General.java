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
import com.newgen.omniforms.excp.CustomExceptionHandler;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;

public class General implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    General objGeneral = null;
    String activityName = null, engineName = null, sessionId = null, folderId = null, serverUrl = null,
            processInstanceId = null, workItemId = null, userName = null, processDefId = null;
    String Query = null, assign = null, ip = null, ip1 = null, UserIndex = null;
    Calendar c = Calendar.getInstance();
    Date currentDate = new Date();
    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    List<List<String>> resultarray, userarray, historyarray, result;
    public static NGEjbClient ngEjbClient = null;
    XMLParser xmlParser = new XMLParser();
    HashMap<String, String> hm;

//    public static String executeWithCallBroker(String inputXml) {
    public String executeWithCallBroker(String inputXml) {
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        String ServerIP = formConfig.getConfigElement("ServletPath");
        ServerIP = ServerIP.replaceAll("[://]", "/");
        ServerIP = ServerIP.split("/")[3];
        String outputXml = "";
        try {
            System.out.println("INPUT: " + inputXml);
            ngEjbClient = NGEjbClient.getSharedInstance();
            outputXml = ngEjbClient.makeCall(ServerIP, "8080", "JBOSSEAP", inputXml);//makeCall(inputXml);
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
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String currentdate = formatter.format(date);
           // System.out.println("currentdate :"+currentdate);
           // System.out.println("Vaibhav :"+java.time.LocalDateTime.now());
            Query = "select concat(UserName,'_',PersonalName,' ',FamilyName) from pdbuser where UserName='" + username + "'";
            result = formObject.getDataFromDataSource(Query);
            System.out.println("result maintainHistory : " + result.get(0).get(0));
            String historylistview = "<ListItem>"
                    + "<SubItem>" + result.get(0).get(0) + "</SubItem>"
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
            formObject.setNGValue("Months", getCurrentMonth());
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
                + "+ "
                + "(select count(*) from ext_outwardFreight ext, WFINSTRUMENTTABLE wf "
                + "where ext.processid = wf.ProcessInstanceID "
                + "and wf.ActivityName not in ('Initiator', 'Discard') "
                + "and ext.accountcode = '" + VendorCode + "' "
                + "and ext.invoicenumber = '" + InvoiceNumber + "' "
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

    public void setItemTypeFlag(String processInstanceId) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        Query = "select sum(cast(po.quarantinemanagement as int)) ,"
                + "sum(cast(po.ppbagmanagement as int)) ,"
                + "sum(cast(po.hlmanagement as int)) ,"
                + "sum(cast(po.rmmanagement as int)) "
                + "from cmplx_gateentryline gel, cmplx_poline po where "
                + "po.pinstanceid = gel.pinstanceid "
                + "and po.itemnumber = gel.itemid "
                + "and po.pinstanceid = '" + processInstanceId + "'"
                + "group by po.pinstanceid";
        System.out.println("Query : " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (Integer.parseInt(result.get(0).get(0)) > 0) {
            formObject.setNGValue("itemtypeflag", "Quarantine");
        } else if (Integer.parseInt(result.get(0).get(1)) > 0) {
            formObject.setNGValue("itemtypeflag", "PP Bags");
        } else if (Integer.parseInt(result.get(0).get(2)) > 0) {
            formObject.setNGValue("itemtypeflag", "HG Limestone");
        } else if (Integer.parseInt(result.get(0).get(3)) > 0) {
            formObject.setNGValue("itemtypeflag", "Raw Material");
        } else {
            formObject.setNGValue("itemtypeflag", "None");
        }
    }

    public String getCurrentMonth() {
        String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];
        return month;
    }

    public void linkWorkitem(String EngineName, String SessionId, String ProcessInstanceID, String LinkedProcessInstanceID, String Operation) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        System.out.println("Inside link WI");

        System.out.println("LinkedProcessInstanceID :" + LinkedProcessInstanceID);
        String WFLinkWorkitem_Input = " <?Xml version=1.0?>"
                + "<WFLinkWorkitem_Input>"
                + "<Option>WFLinkWorkitem</Option>"
                + "<EngineName>" + EngineName + "</EngineName>"
                + "<SessionId>" + SessionId + "</SessionId>"
                + "<Operation>" + Operation + "</Operation>"
                + "<WorkItemId>1</WorkItemId>"
                + "<ProcessInstanceID>" + ProcessInstanceID + "</ProcessInstanceID>"
                + "<LinkedProcessInstanceID>" + LinkedProcessInstanceID + "</LinkedProcessInstanceID>"
                + "</WFLinkWorkitem_Input>";
        System.out.println("WFLinkWorkitem_Input :" + WFLinkWorkitem_Input);
        executeWithCallBroker(WFLinkWorkitem_Input);
    }

    public void forwardWI(String EngineName, String SessionId, String ProcessInstanceID, String processDefId, String activityId, String attributes) {
        String inputxml = "", outputxml = "";
        inputxml = " <WMGetWorkItem_Input> "
                + " <Option>WMGetWorkItem</Option>"
                + " <EngineName>" + EngineName + "</EngineName>"
                + " <SessionId>" + SessionId + "</SessionId>"
                + " <ProcessInstanceId>" + ProcessInstanceID + "</ProcessInstanceId>"
                + " <WorkItemId>1</WorkItemId> "
                + " </WMGetWorkItem_Input>";
        outputxml = executeWithCallBroker(inputxml);
        System.out.println("Get Work Item Output XML :: " + outputxml);
        //xmlParser.setInputXML(outputxml);
        WFXmlResponse objXmlResponse = new WFXmlResponse(outputxml);
        if ("0".equalsIgnoreCase(objXmlResponse.getVal("MainCode"))) {
            inputxml = "<WMAssignWorkItemAttributes_Input>"
                    + "<Option>WMAssignWorkItemAttributes</Option>"
                    + "<EngineName>" + EngineName + "</EngineName>"
                    + "<SessionId>" + SessionId + "</SessionId>"
                    + "<ProcessInstanceId>" + ProcessInstanceID + "</ProcessInstanceId>"
                    + "<WorkItemId>1</WorkItemId>"
                    + "<ActivityId>" + activityId + "</ActivityId>"
                    + "<ProcessDefId>" + processDefId + "</ProcessDefId>"
                    + "<LastModifiedTime></LastModifiedTime>"
                    + "<ActivityType>10</ActivityType>"
                    + "<complete>D</complete>"
                    + "<UserDefVarFlag>Y</UserDefVarFlag>"
                    + "<Documents></Documents>"
                    + "<Attributes>" + attributes + "</Attributes>"
                    + "</WMAssignWorkItemAttributes_Input>";

            System.out.println("Assign Work Item Input :: " + inputxml);
            outputxml = executeWithCallBroker(inputxml);
            System.out.println("Assign Work Item Output XML :: " + outputxml);
        } else {
            throw new ValidatorException(new FacesMessage(objXmlResponse.getVal("Subject"), ""));
        }
    }

    public boolean checkSupplyPoDoAUser(String ApproverLevel) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        boolean DoADefinedFlag = false;
        System.out.println("Inside checkSupplyDoA");
        Query = "select count(*) from SupplyPoApproverMaster "
                + "where  head = '" + formObject.getNGValue("proctype") + "' "
                + "and site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' "
                + "and ApproverLevel = '" + ApproverLevel + "' ";
        System.out.println("Query: " + Query);
        result = formObject.getDataFromDataSource(Query);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {
            throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
        } else {
            DoADefinedFlag = true;
        }
        return DoADefinedFlag;
    }

    public boolean checkServiceNonPoDoAUser(String ApproverLevel, String ApproverStage) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String sQuery;
        boolean DoADefinedFlag = false;
        System.out.println("Inside checkServiceNonPoDoAUser");
        Query = "select count(*) from ServiceNonPOApproverMaster "
                + "where head = '" + formObject.getNGValue("proctype") + "' "
                + "and site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' ";
        sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
        System.out.println("Query: " + sQuery);
        result = formObject.getDataFromDataSource(sQuery);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {
            if (ApproverStage.equalsIgnoreCase("Approver")) {
                ApproverLevel = "AccountsMaker";
                sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
                System.out.println("Query: " + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
                } else {
                    formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
                    formObject.setNGValue("levelflag", ApproverLevel);
                    DoADefinedFlag = true;
                }
            } else {
                throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
            }
        } else {
            formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
            formObject.setNGValue("levelflag", ApproverLevel);
            DoADefinedFlag = true;
        }
        return DoADefinedFlag;
    }

    public boolean checkServicePoDoAUser(String ApproverLevel, String ApproverStage) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String sQuery;
        boolean DoADefinedFlag = false;
        System.out.println("Inside checkServicePoDoAUser");
        Query = "select count(*) from ServicePOApproverMaster "
                + "where head = '" + formObject.getNGValue("proctype") + "' "
                + "and site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' ";
        sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
        System.out.println("Query: " + sQuery);
        result = formObject.getDataFromDataSource(sQuery);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {
            if (ApproverStage.equalsIgnoreCase("Approver")) {
                ApproverLevel = "AccountsMaker";
                sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
                System.out.println("Query: " + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
                } else {
                    formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
                    formObject.setNGValue("levelflag", ApproverLevel);
                    DoADefinedFlag = true;
                }
            } else {
                throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
            }
        } else {
            formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
            formObject.setNGValue("levelflag", ApproverLevel);
            DoADefinedFlag = true;
        }
        return DoADefinedFlag;
    }

    public boolean checkRABillDoAUser(String ApproverLevel, String ApproverStage) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String sQuery;
        boolean DoADefinedFlag = false;
        System.out.println("Inside checkRABillDoAUser");
        Query = "select count(*) from RABillApproverMaster "
                + "where site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' ";
        sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
        System.out.println("Query: " + sQuery);
        result = formObject.getDataFromDataSource(sQuery);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {
            if (ApproverStage.equalsIgnoreCase("Approver")) {
                ApproverLevel = "AccountsMaker";
                sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
                System.out.println("Query: " + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
                } else {
                    formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
                    formObject.setNGValue("levelflag", ApproverLevel);
                    DoADefinedFlag = true;
                }
            } else {
                throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
            }
        } else {
            formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
            formObject.setNGValue("levelflag", ApproverLevel);
            DoADefinedFlag = true;
        }
        return DoADefinedFlag;
    }

    public boolean checkOutwardFreightDoAUser(String ApproverLevel, String ApproverStage) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        String sQuery;
        boolean DoADefinedFlag = false;
        System.out.println("Inside FreightBillApproverMaster");
        Query = "select count(*) from FreightBillApproverMaster "
                + "where site = '" + formObject.getNGValue("site") + "' "
                + "and state = '" + formObject.getNGValue("state") + "' "
                + "and department = '" + formObject.getNGValue("department") + "' ";
        sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
        System.out.println("Query: " + sQuery);
        result = formObject.getDataFromDataSource(sQuery);
        if (result.get(0).get(0).equalsIgnoreCase("0")) {
            if (ApproverStage.equalsIgnoreCase("Approver")) {
                ApproverLevel = "AccountsMaker";
                sQuery = Query + "and ApproverLevel = '" + ApproverLevel + "' ";
                System.out.println("Query: " + sQuery);
                result = formObject.getDataFromDataSource(sQuery);
                if (result.get(0).get(0).equalsIgnoreCase("0")) {
                    throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
                } else {
                    formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
                    formObject.setNGValue("levelflag", ApproverLevel);
                    DoADefinedFlag = true;
                }
            } else {
                throw new ValidatorException(new FacesMessage("DoA is not defined for selected values"));
            }
        } else {
            formObject.setNGValue("FilterDoA_ApproverLevel", ApproverLevel);
            formObject.setNGValue("levelflag", ApproverLevel);
            DoADefinedFlag = true;
        }
        return DoADefinedFlag;
    }

    public void compareDate(String InvoiceDate, String PostingDate) {
        System.out.println("inside compareDate");
        System.out.println("InvoiceDate = " + InvoiceDate);
        System.out.println("PostingDate = " + PostingDate);

        if ((date_converter(PostingDate)).compareTo(date_converter(InvoiceDate)) < 0) {
            System.out.println("trueeee");
            throw new ValidatorException(new FacesMessage("Invoice date can not be later than Posting Date"));
        }
    }

    public String formatExtractedInvoiceDt(String InvoiceDateEx) {
        String FormattedInvoiceDt = "";
        if (InvoiceDateEx.contains(".")) {
            InvoiceDateEx = InvoiceDateEx.replace('.', '/');
            System.out.println(InvoiceDateEx);
        }
        if (InvoiceDateEx.contains("-")) {
            InvoiceDateEx = InvoiceDateEx.replace('-', '/');
            System.out.println(InvoiceDateEx);
        }
        if (InvoiceDateEx.contains("\\")) {
            InvoiceDateEx = InvoiceDateEx.replace('\\', '/');
            System.out.println(InvoiceDateEx);
        }
        System.out.println("InvoiceDateEx :" + InvoiceDateEx);

        String splitInvoiceDateEx[] = InvoiceDateEx.split("/");
        String dd = splitInvoiceDateEx[0];
        if (dd.length() == 1) {
            dd = "0" + dd;
        }
        String mm = splitInvoiceDateEx[1];
        if (mm.length() == 1) {
            mm = "0" + mm;
        }
        FormattedInvoiceDt = dd + "/" + mm + "/" + splitInvoiceDateEx[2];
        return FormattedInvoiceDt;
    }

    public Date date_converter(String inputdate) {
        Date date = null;
        if (inputdate.length() <= 10) {
            DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (inputdate.contains(".")) {
                inputdate = inputdate.replace('.', '/');
                System.out.println(inputdate);
            }
            if (inputdate.contains("-")) {
                inputdate = inputdate.replace('-', '/');
                System.out.println(inputdate);
            }
            if (inputdate.contains("\\")) {
                inputdate = inputdate.replace('\\', '/');
                System.out.println(inputdate);
            }
            try {
                if (Integer.parseInt(inputdate.split("/")[1]) > 12 && Integer.parseInt(inputdate.split("/")[2]) > 999) {
                    inputdate = inputdate.split("/")[1] + "/" + inputdate.split("/")[0] + "/" + inputdate.split("/")[2];
                    date = targetFormat.parse(inputdate);
                } else if (Integer.parseInt(inputdate.split("/")[0]) > 999
                        && Integer.parseInt(inputdate.split("/")[2]) < 32) {
                    inputdate = inputdate.split("/")[2] + "/" + inputdate.split("/")[1] + "/" + inputdate.split("/")[0];
                    date = targetFormat.parse(inputdate);
                } else {
                    date = targetFormat.parse(inputdate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            throw new ValidatorException(new FacesMessage("input date is not in correct format"));
        }
        System.out.println("return date :" + date);
        return date;
    }

    public void openbamreport(String Report_name) {
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        formObject = FormContext.getCurrentInstance().getFormReference();
        //    System.out.println(formConfig.getConfigXML());
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

            hm = new HashMap<>();
            UserIndex = formConfig.getConfigElement("UserIndex");
            System.out.println("serverUrl 1== "+serverUrl);
            serverUrl = serverUrl.split("webdesktop")[0];
            System.out.println("serverUrl == "+serverUrl);
            ip = serverUrl.replaceAll("//", "/");
            Query = "select REPORTID from CRREPORTTABLE where REPORTNAME = '" + Report_name + "'";
            result = formObject.getDataFromDataSource(Query);
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
        String Link = "" + serverUrl + "bam/login/login.jsf?CalledFrom=EXT&UserId=" + userName + "&UserIndex=" + UserIndex + "&SessionId=" + sessionId + "&CabinetName="
                + "" + engineName + "&processInstanceId=" + processInstanceId + "&LaunchClient=RI&ReportIndex=" + result.get(0).get(0) + "&AjaxRequest=Y&OAPDomHost=" + ip.split("/")[1] + "&ProcessInstanceId="
                + processInstanceId + "";
        System.out.println("link == "+Link);
        System.out.println("Moving to client js QQQQ");
        throw new ValidatorException(new CustomExceptionHandler(Report_name, Link, "", hm));
    }

    public void setInvoiceExtractedData(String ponumberid, String invoicenumberid, String invoiceamountid, String invoicedateid) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        if (!ponumberid.equalsIgnoreCase("")) {
            System.out.println("Po number ex:" + formObject.getNGValue("ponumberex"));
            if (formObject.getNGValue(ponumberid).equals("")) {
                formObject.setNGValue(ponumberid, formObject.getNGValue("ponumberex"));
            }
        }

        System.out.println("Invoice Number ex :" + formObject.getNGValue("invoicenumberex"));
        if (formObject.getNGValue(invoicenumberid).equals("")) {
            formObject.setNGValue(invoicenumberid, formObject.getNGValue("invoicenumberex"));
        }

        System.out.println("InvAmountEx :" + formObject.getNGValue("invoiceamountex").replace(",", ""));
        if (formObject.getNGValue(invoiceamountid).equals("")) {
            formObject.setNGValue(invoiceamountid, formObject.getNGValue("invoiceamountex").replace(",", ""));
        }

        System.out.println("Invoice Date ex :" + formObject.getNGValue("invoicedateex"));
        if (formObject.getNGValue(invoicedateid).equals("")) {
            formObject.setNGValue(invoicedateid, formatExtractedInvoiceDt(formObject.getNGValue("invoicedateex")));
        }

    }

}
