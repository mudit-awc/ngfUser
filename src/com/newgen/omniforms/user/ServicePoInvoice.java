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

import com.newgen.ServicePoInvoice.Accounts;
import com.newgen.ServicePoInvoice.Approver;
import com.newgen.ServicePoInvoice.Initiator;
import com.newgen.ServicePoInvoice.Purchase;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;

public class ServicePoInvoice implements IFormListenerFactory {

    @Override
    public FormListener getListener() {
        // TODO Auto-generated method stub
        String sActivityName = null;
        FormConfig objConfig = FormContext.getCurrentInstance().getFormConfig();
        sActivityName = objConfig.getConfigElement("ActivityName");
        System.out.println("**********sActivityName :" + sActivityName);
        if (sActivityName.equalsIgnoreCase("Initiator")) {
            return new Initiator();
        } else if (sActivityName.equalsIgnoreCase("Approver")) {
            return new Approver();
        } else if (sActivityName.equalsIgnoreCase("AccountsMaker")
                || sActivityName.equalsIgnoreCase("AccountsChecker")
                || sActivityName.equalsIgnoreCase("AXSyncException")) {
            return new Accounts();
        }else if (sActivityName.equalsIgnoreCase("PurchaseUser")) {
            return new Purchase();
        }
        return null;
    }
}
