/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.omniforms.user;

/**
 *
 * @author Richa Maheshwari
 */
import com.newgen.NonPoInvoices1.Accounts;
import com.newgen.NonPoInvoices1.Approver;
import com.newgen.NonPoInvoices1.Initiator;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;
public class NonPoInvoices1 implements IFormListenerFactory {

    @Override
    public FormListener getListener() {
        // TODO Auto-generated method stub

        String sActivityName = null;
        String sProcessName = null;
        FormListener objActivity = null;
        FormConfig formConfig = FormContext.getCurrentInstance().getFormConfig();
        sActivityName = formConfig.getConfigElement("ActivityName");
        sProcessName = formConfig.getConfigElement("ProcessName");
        System.out.println("**********sActivityName :" + sActivityName);
         if (sActivityName.equalsIgnoreCase("Introduction")
             || sActivityName.equalsIgnoreCase("Initiator")) {
            System.out.println("Returning to NonPoInvoices Head");
            return new Initiator();
         }
         else if (sActivityName.equalsIgnoreCase("Approver")){
                     return new Approver();
            }
         else if (sActivityName.equalsIgnoreCase("Accounts")){
                     return new Accounts();
         }
                return null;
       }
       
    }

