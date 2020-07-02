/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.omniforms.user;

import com.newgen.RABill.*;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;

/**
 *
 * @author Admin
 */
public class RABill implements IFormListenerFactory {

    @Override
    public FormListener getListener() {

        // TODO Auto-generated method stub
        String sActivityName;
        FormConfig objConfig = FormContext.getCurrentInstance().getFormConfig();
        sActivityName = objConfig.getConfigElement("ActivityName");
        System.out.println("**********sActivityName :" + sActivityName);

        if (sActivityName.equalsIgnoreCase("ScanningUser")){
            return new ScanningUser();
        }
        else if (sActivityName.equalsIgnoreCase("Indexer")) {
            return new Indexer();
        } else if (sActivityName.equalsIgnoreCase("Approver")) {
            return new Approver();
        } else if (sActivityName.equalsIgnoreCase("AccountsMaker")
                || sActivityName.equalsIgnoreCase("AccountsChecker")
                || sActivityName.equalsIgnoreCase("AXSyncException")) {
            return new Accounts();
        } else if (sActivityName.equalsIgnoreCase("PurchaseUser")) {
            return new Purchase();
        }
        return null;
    }

}
