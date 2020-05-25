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
import com.newgen.NonPoInvoice.Introduction;
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
        if (sActivityName.equalsIgnoreCase("Initiator")) {
            System.out.println("Returning to NonPoInvoice Initiator");
            return new Initiator();
        } else if (sActivityName.equalsIgnoreCase("Approver")) {
            return new Approver();
        } else if (sActivityName.equalsIgnoreCase("AccountsMaker")
                || sActivityName.equalsIgnoreCase("AccountsChecker")
                || sActivityName.equalsIgnoreCase("AXSyncException")) {
            return new Accounts();
        } else if (sActivityName.equalsIgnoreCase("Introduction")
                || sActivityName.equalsIgnoreCase("IntroductionWithoutExtraction")) {
            return new Introduction();
        }
        return null;
    }

}
