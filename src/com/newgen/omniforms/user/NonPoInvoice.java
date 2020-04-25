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
import com.newgen.NonPoInvoice.Accounts;
import com.newgen.NonPoInvoice.Approver;
import com.newgen.NonPoInvoice.Initiator;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;

public class NonPoInvoice implements IFormListenerFactory {

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
        if (sActivityName.equalsIgnoreCase("Initiator")||sActivityName.equalsIgnoreCase("Introduction")) {
            System.out.println("Returning to NonPoInvoice Initiator");
            return new Initiator();
        } else if (sActivityName.equalsIgnoreCase("Approver")) {
            return new Approver();
        } else if (sActivityName.equalsIgnoreCase("Accounts")
                || sActivityName.equalsIgnoreCase("AXSyncException")) {
            return new Accounts();
        }
        return null;
    }

}
