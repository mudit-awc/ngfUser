package com.newgen.common;

import com.newgen.SupplyPoInvoices.Initiator;
import com.newgen.json.JSONObject;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;

public class General implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    General objGeneral = null;
    String Query = null, assign = null;
    Calendar c = Calendar.getInstance();
    Date currentDate = new Date();
    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    List<List<String>> resultarray, userarray, historyarray;
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

    public static String callWebService(String serviceURL, String inputJSON) {
        String outputJSON = "";
        System.out.println("Input JSON.... " +inputJSON );
        try {
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("Authorization", "Basic OjEyMw==");
            OutputStream os = conn.getOutputStream();
            os.write(inputJSON.getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            outputJSON = br.readLine();
            System.out.println("Output JSON.... " + outputJSON);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputJSON;
    }

    public static String convertNewgenDateToSapDate(String sDate) {
        try {
            String formatDate = "";
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date sDate_temp = formatter.parse(sDate);
            System.out.println("Date parse : " + sDate_temp);
            formatDate = formatter1.format(sDate_temp);
            System.out.println("Date format : " + formatDate);
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

    public void setTransactionLogs() {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("Inside set transaction");
        String processDefId = formConfig.getConfigElement("ProcessDefId");
        String processInstanceId = formConfig.getConfigElement("ProcessInstanceId");
        String userName = formConfig.getConfigElement("UserName");
        String activityName = "";//formObject.getWFActivityName();

        try {
            System.out.println("Inside setAuditLogs--------------------");
            String file_status;
            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String today = formatter.format(date);
            System.out.println("Printing date time @ng_orient_transaction_log--" + date);
            System.out.println("Printing processdefid @ng_orient_transaction_log--" + processDefId);
            System.out.println("Printing processInstanceId @ng_orient_transaction_log--" + processInstanceId);
            System.out.println("Printing Submitted by @ng_orient_transaction_log--" + userName);
            System.out.println("Printing submitted on @ng_orient_transaction_log--" + today);

            if (activityName.equalsIgnoreCase("Gate Entry")) {
                // Query for Next WorkItem                
                Query = "insert into ng_orient_transaction_log(status,transactionid,pinstanceid,processdefid,acted_on,acted_by,received_on,activity_name)"
                        + "values ('" + formObject.getNGValue("gate_decision") + "','','" + processInstanceId + "','" + processDefId + "','" + today + "','" + userName + "','','" + activityName + "')";
                System.out.println("Query 1/GE : " + Query);
                formObject.saveDataIntoDataSource(Query);
                // Query for Current WorkItem
                Query = "insert into ng_orient_transaction_log(status,transactionid,pinstanceid,processdefid,acted_on,acted_by,received_on,activity_name)"
                        + "values ('Pending','','" + processInstanceId + "','" + processDefId + "','','','" + today + "','Store')";
                System.out.println("Query 2/GE : " + Query);
                formObject.saveDataIntoDataSource(Query);

            } else if (activityName.equalsIgnoreCase("Store")) {

                // Getting form status
                file_status = formObject.getNGValue("store_decision");
                // Query for Next WorkItem
                Query = "insert into ng_orient_transaction_log(status,transactionid,pinstanceid,processdefid,acted_on,acted_by,received_on,activity_name)"
                        + "values ('Pending','','" + processInstanceId + "','" + processDefId + "','" + today + "','" + userName + "','','')";
                System.out.println("Query 1/Store : " + Query);
                formObject.saveDataIntoDataSource(Query);
                // Query for Current Workitem
                Query = "update ng_orient_transaction_log set status='" + file_status + "' and acted_on = '" + today + "' where status = 'Pending' "
                        + "and pinstanceid = '" + processInstanceId + "'";
                System.out.println("Query 2/Store : " + Query);
                formObject.saveDataIntoDataSource(Query);

            } else if (activityName.equalsIgnoreCase("Quality")) {

                // Getting form status
                file_status = formObject.getNGValue("quality_decision");
                // Query for Next WorkItem
                Query = "insert into ng_orient_transaction_log(status,transactionid,pinstanceid,processdefid,acted_on,acted_by,received_on,activity_name)"
                        + "values ('Pending','','" + processInstanceId + "','" + processDefId + "','" + today + "','" + userName + "','','')";
                formObject.saveDataIntoDataSource(Query);
                // Query for Current Workitem
                Query = "update ng_orient_transaction_log set status='" + file_status + "' and acted_on = '" + today + "' where status = 'Pending' and pinstanceid = '" + processInstanceId + "'";
                formObject.saveDataIntoDataSource(Query);

            } else if (activityName.equalsIgnoreCase("Accounts")) {

                // Getting form status
                // Query for Next WorkItem
                Query = "insert into ng_orient_transaction_log(status,transactionid,pinstanceid,processdefid,acted_on,acted_by,received_on,activity_name)"
                        + "values ('Pending','','" + processInstanceId + "','" + processDefId + "','" + today + "','" + userName + "','','')";
                formObject.saveDataIntoDataSource(Query);
                // Query for Current Workitem
                Query = "update ng_orient_transaction_log set status='Parked' and acted_on = '" + today + "' where status = 'Pending' and pinstanceid = '" + processInstanceId + "'";
                formObject.saveDataIntoDataSource(Query);

            } else if (activityName.equalsIgnoreCase("Finance")) {

                // Getting form status
                // Query for Previous WorkItem
                Query = "update ng_orient_transaction_log set status='Posted' and acted_on = '" + today + "' where status = 'Pending' and pinstanceid = '" + processInstanceId + "'";
                formObject.saveDataIntoDataSource(Query);

            }

        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

    }
}
