/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.NonPoInvoices1;

import com.newgen.common.General;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.IRepeater;
import com.newgen.omniforms.component.ListView;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.event.ComponentEvent;
import com.newgen.omniforms.event.FormEvent;
import com.newgen.omniforms.listener.FormListener;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Richa Maheshwari
 */
public class Initiator implements FormListener {

    FormReference formObject = null;
    FormConfig formConfig = null;
    String activityName = null;
    String engineName = null;
    String sessionId = null;
    String folderId = null;
    String FILE = null;
    String serverUrl = null;
    String processInstanceId = null;
    String workItemId = null;
    String userName = null;
    String processDefId = null;
    String Query = null;
    List<List<String>> result;
    PickList objPicklist;
    General objGeneral = null;
    List<List<String>> result1;
    String Query1 = null;

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        // TODO Auto-generated method stub
        System.out.println("Value Change Event :" + pEvent);
        System.out.println("pEvent.getType() :" + pEvent.getType());
        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        //objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                switch (pEvent.getSource().getName()) {
                    case "":

                        break;
                }
                break;
            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Add_linedetails_1":
                        //System.out.println("");
                        int serialno1;
                        //get row count of list view
                        ListView ListViewq_linedetails_1 = (ListView) formObject.getComponent("q_linedetails_1");
                        int RowCount_q_linedetails_1 = ListViewq_linedetails_1.getRowCount();
                        System.out.println("RowCount_q_linedetails_1 " + RowCount_q_linedetails_1);
                        //genrate the serial no
                        serialno1 = RowCount_q_linedetails_1 + 1;
                        System.out.println("serialno1 " + serialno1);
                        //set value in serial no text field
                        formObject.setNGValue("Text34", serialno1);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails_1");
                        break;
                    case "Btn_Add_linedetails_2":
                        int serialno2;
                        //get row count of list view
                        ListView ListViewq_linedetails_2 = (ListView) formObject.getComponent("q_linedetails_2");
                        int RowCount_q_linedetails_2 = ListViewq_linedetails_2.getRowCount();
                        System.out.println("RowCount_q_linedetails_2 " + RowCount_q_linedetails_2);
                        //genrate the serial no
                        serialno2 = RowCount_q_linedetails_2 + 1;
                        System.out.println("serialno2 " + serialno2);
                        //set value in serial no text field
                        formObject.setNGValue("Text52", serialno2);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails_2");
                        break;
                    case "Btn_Add_linedetails_3":
                        int serialno3;
                        //get row count of list view
                        ListView ListViewq_linedetails_3 = (ListView) formObject.getComponent("q_linedetails_3");
                        int RowCount_q_linedetails_3 = ListViewq_linedetails_3.getRowCount();
                        System.out.println("RowCount_q_linedetails_3 " + RowCount_q_linedetails_3);
                        //genrate the serial no
                        serialno3 = RowCount_q_linedetails_3 + 1;
                        System.out.println("serialno3 " + serialno3);
                        //set value in serial no text field
                        formObject.setNGValue("Text58", serialno3);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails_3");
                        break;
                    case "Btn_Add_linedetails_4":
                        int serialno4;
                        //get row count of list view
                        ListView ListViewq_linedetails_4 = (ListView) formObject.getComponent("q_linedetails_4");
                        int RowCount_q_linedetails_4 = ListViewq_linedetails_4.getRowCount();
                        System.out.println("RowCount_q_linedetails_4 " + RowCount_q_linedetails_4);
                        //genrate the serial no
                        serialno4 = RowCount_q_linedetails_4 + 1;
                        System.out.println("serialno4 " + serialno4);
                        //set value in serial no text field
                        formObject.setNGValue("Text64", serialno4);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails_4");
                        break;
                    case "Btn_Add_otherdetails_npo":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_otherdetails_npo");
                        break;

//                    case "Btn_Delete_linedetails_1":
//                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_linedetails_1");
//                        break;
//                    case "Btn_Delete_linedetails_2":
//                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_linedetails_2");
//                        break;
//                    case "Btn_Delete_linedetails_3":
//                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_linedetails_3");
//                        break;
//                    case "Btn_Delete_linedetails_4":
//                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_linedetails_4");
//                        break;
                    case "Btn_Delete_otherdetails_npo":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_otherdetails_npo");
                        break;
                    case "Btn_Modify_linedetails_1":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails_1");
                        break;
                    case "Btn_Modify_linedetails_2":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails_2");
                        break;
                    case "Btn_Modify_linedetails_3":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails_3");
                        break;
                    case "Btn_Modify_linedetails_4":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails_4");
                        break;
                    case "Btn_Modify_otherdetails_npo":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_otherdetails_npo");
                        break;
                }
                break;
            case "TAB_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "":
                        break;
                }
                break;
        }
    }

    @Override
    public void formLoaded(FormEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println(" -------------------Intiation Workstep Loaded from formloaded.----------------");
        // TODO Auto-generated method stub
        System.out.println("form Loaded called : 20/05/2019");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        try {

            // objGeneral = new General();
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

//  ************************************************************************************
            if (activityName.equalsIgnoreCase("Introduction") || activityName.equalsIgnoreCase("Initiator")) {
                formObject.setVisible("filestatus", false);
                formObject.setVisible("Label23", false);
            }
        } catch (Exception e) {
            System.out.println("Exception in FieldValueBagSet::::" + e.getMessage());
        }
    }

    @Override
    public void formPopulated(FormEvent arg0) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        // TODO Auto-generated method stub     
        //objGeneral = new General();
        System.out.println("----------------------Intiation Workstep Loaded from form populated........---------------------------");
        System.out.println("processInstanceID is " + processInstanceId);

        formObject.setNGValue("filestatus", null);
        System.out.println("filestatus value" + formObject.getNGValue("filestatus"));
//**************************************************************************************************************************************************        
        Query = "select statename from ngmaster_statename";
        System.out.println("query is :" + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result is :" + result);
        System.out.println("result.size() : "+result.size());
        for (int a = 0; a < result.size(); a++) {
            //System.out.println("the result a 00 index richa" + result.get(a).get(0));

            formObject.addComboItem("Drop_ss1",result.get(a).get(0),result.get(a).get(0));
        
            formObject.addComboItem("Drop_ss2", result.get(a).get(0), result.get(a).get(0));
            formObject.addComboItem("Drop_ss3", result.get(a).get(0), result.get(a).get(0));
        }
    }

    @Override
    public void saveFormCompleted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        System.out.print("-------------------save form completed---------");
    }

    @Override
    public void saveFormStarted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormCompleted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

    }

    @Override
    public void submitFormStarted(FormEvent arg0) throws ValidatorException {
        // TODO Auto-generated method stub
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        System.out.println("**********-------SUBMIT FORM Started------------*************");
        //formObject.setNGValue("login_user", userName);
        // IRepeater repeater1 = formObject.getRepeaterControl("Frame20");
        //****************************************************************************************

        try {
            String proctype = formObject.getNGValue("proctype");
            String state = formObject.getNGValue("servicegiveninstate");
            System.out.println("state is : " + formObject.getNGValue("servicegiveninstate"));
            int levelflag = Integer.parseInt(formObject.getNGValue("levelflag")) + 1;
            System.out.println("levelflag : " + levelflag);
            //Query = "select ApproverName from ApproverMaster where Head='" + proctype + "' "
            // + "and ApproverLevel='" + levelflag + "' and State= limit 1";
            Query = "select TOP 1 ApproverName from ApproverMaster where Head ='" + proctype + "'and ApproverLevel='" + levelflag + "'";
            //+"and State ='" + state + "'";
            if (!proctype.equalsIgnoreCase("Demurrage and Wharfage (Plant/GU) (Rail)") || !proctype.equalsIgnoreCase("Primary Freight and Freight on clinker Sale (Rail)") || !proctype.equalsIgnoreCase("Other Logistic Expenses (Rail)") || !proctype.equalsIgnoreCase("Travel Allowance Bills (TA Bills) (Train)")) {
                Query = "select TOP 1 ApproverName from ApproverMaster where Head ='" + proctype + "'and ApproverLevel='" + levelflag + "'"
                        + "and State ='" + state + "'";
            }
            System.out.println("Query:" + Query);

            result = formObject.getDataFromDataSource(Query);

            System.out.println("result" + result);
            if (result.size() > 0) {
                System.out.println("assignto" + result.get(0).get(0));
                formObject.setNGValue("assignto", result.get(0).get(0));
                formObject.setNGValue("levelflag", levelflag);
            } else {
                formObject.setNGValue("assignto", "NA");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
}
