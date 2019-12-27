/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.ServicePoInvoices;

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
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

    @Override
    public void continueExecution(String arg0, HashMap<String, String> arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void eventDispatched(ComponentEvent pEvent) throws ValidatorException {
        // TODO Auto-generated method stub
//        System.out.println("Value Change Event :" + pEvent);
//        System.out.println("pEvent.getType() :" + pEvent.getType());
//        System.out.println("pEvent.getType().name() :" + pEvent.getType().name());
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();

        //objGeneral = new General();
        switch (pEvent.getType().name()) {
            case "VALUE_CHANGED":
                System.out.println("inside vALUE CHANGED");
                switch (pEvent.getSource().getName()) {
                    case "Text26":
                        System.out.println("inside Text26");
                        String per = formObject.getNGValue("Text26");
                        double ttamt = Double.parseDouble(formObject.getNGValue("totaltaxableamount"));
                        double perc = Double.parseDouble(formObject.getNGValue("Text26"));
                        if (per != null) {
                            System.out.println("inside totaltaxableamount");
                            System.out.println(perc);
                            double amt = ((perc / 100) * ttamt);
                            System.out.println(amt);
                            String amount = String.valueOf(amt);
                            formObject.setNGValue("Text28", amount);
                        } else {
                            System.out.println("inside else");
                            formObject.setNGValue("Text28", "");
                            //throw new ValidatorException(new FacesMessage("Please Enter Percentage"));
                        }
                        break;
                    case "proctype":  
                     formObject.clear("Combo4");
                        break;
                }
                break;

            case "MOUSE_CLICKED":
                switch (pEvent.getSource().getName()) {
                    case "Btn_Add_linedetails1":
                        int serialno;
                       String hsnsac=formObject.getNGValue("Text14");
                        //get row count of list view
                        ListView ListViewq_linedetails1 = (ListView) formObject.getComponent("q_linedetails1");
                        int RowCount_q_linedetails1 = ListViewq_linedetails1.getRowCount();
                        System.out.println("RowCount_q_linedetails1 " + RowCount_q_linedetails1);
                        //genrate the serial no
                        serialno = RowCount_q_linedetails1 + 1;
                        System.out.println("serialno " + serialno);
                        //set value in serial no text field
                        formObject.setNGValue("Text12", serialno);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails1");
                        
                        formObject.addComboItem("Combo4", hsnsac, hsnsac);
                        System.out.println("combo4");
                        break;
                    case "Btn_Modify_linedetails1":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails1");
                        break;
                    case "Btn_Add_linedetails2":
                        int serialno2;
                        //get row count of list view
                        ListView ListViewq_linedetails2 = (ListView) formObject.getComponent("q_linedetails2");
                        int RowCount_q_linedetails2 = ListViewq_linedetails2.getRowCount();

                        //genrate the serial no
                        serialno2 = RowCount_q_linedetails2 + 1;
                        //set value in serial no text field
                        formObject.setNGValue("Text19", serialno2);
                        formObject.ExecuteExternalCommand("NGAddRow", "q_linedetails2");
                        break;
                    case "Btn_Modify_linedetails2":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_linedetails2");
                        break;
                    //buttons for other Details Table    
                    case "Btn_Add_otherdetails":
                        formObject.ExecuteExternalCommand("NGAddRow", "q_otherdetails");
                        break;
                    case "Btn_Delete_otherdetails":
                        formObject.ExecuteExternalCommand("NGDeleteRow", "q_otherdetails");
                        break;
                    case "Btn_Modify_otherdetails":
                        formObject.ExecuteExternalCommand("NGModifyRow", "q_otherdetails");
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
        formObject.setNGValue("filestatus", null);
        System.out.println("filestatus value"+formObject.getNGValue("filestatus"));
        
        Query = "select statename from ngmaster_statename";
        System.out.println("query is :" + Query);
        result = formObject.getDataFromDataSource(Query);
        System.out.println("result is :" + result);
        System.out.println("result.size() : "+result.size());
        for (int a = 0; a < result.size(); a++) {
            System.out.println("the result a 00 index richa" + result.get(a).get(0));

            formObject.addComboItem("servicegiveninstate",result.get(a).get(0),result.get(a).get(0));
        
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
        formObject.setNGValue("login_user", userName);
        // IRepeater repeater1 = formObject.getRepeaterControl("Frame20");
        //****************************************************************************************
      

        
            String proctype = formObject.getNGValue("proctype");
            int levelflag = Integer.parseInt(formObject.getNGValue("levelflag")) + 1;
            System.out.println("levelflag : " + levelflag);
            // Query1 = "select UserName from ApproverMaster where Head='" + proctype + "' "
                   // + "order by ApproverLevel desc limit 1
            try {
                String state=formObject.getNGValue("servicegiveninstate");
              //if(!activityName.equalsIgnoreCase("Accounts")){
                  System.out.println("inside initiator and Approver activity");
            Query = "select TOP 1 ApproverName from ApproverMaster where Head='" + proctype + "' "
                    + "and ApproverLevel='" + levelflag + "'and State ='" + state + "'";
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
              //}
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