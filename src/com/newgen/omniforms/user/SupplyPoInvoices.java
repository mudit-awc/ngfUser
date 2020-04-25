/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.omniforms.user;

//import com.newgen.omniforms.FormReference;
import com.newgen.SupplyPoInvoices.AccountUser;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;
import com.newgen.SupplyPoInvoices.Initiator;
import com.newgen.SupplyPoInvoices.MultipleGRNInvoicing;
import com.newgen.SupplyPoInvoices.PurchaseUser;
import com.newgen.SupplyPoInvoices.QualityUser;
import com.newgen.SupplyPoInvoices.StoreUser;
import com.newgen.omniforms.FormConfig;

/**
 *
 * @author Admin
 */
public class SupplyPoInvoices implements IFormListenerFactory {

    //LogProcessing objLogProcessing = null;
    @Override
    public FormListener getListener() {
        // TODO Auto-generated method stub
        String sActivityName;
        FormConfig objConfig = FormContext.getCurrentInstance().getFormConfig();
        sActivityName = objConfig.getConfigElement("ActivityName");
        System.out.println("**********sActivityName :" + sActivityName);

        if (sActivityName.equalsIgnoreCase("Introduction")
                // || sActivityName.equalsIgnoreCase("ManualIntroduction")
                || sActivityName.equalsIgnoreCase("Initiator")) {
            System.out.println("Returning to SupplyPoInvoices Head");
            return new Initiator();
        } else if (sActivityName.equalsIgnoreCase("StoreUser") || sActivityName.equalsIgnoreCase("AXStoreSyncException")) {
            return new StoreUser();
        } else if (sActivityName.equalsIgnoreCase("QualityUser") || sActivityName.equalsIgnoreCase("AXQualitySyncException")) {
            return new QualityUser();
        } else if (sActivityName.equalsIgnoreCase("PurchaseUser")) {
            return new PurchaseUser();
        } else if (sActivityName.equalsIgnoreCase("AccountsUser")) {
            return new AccountUser();
        } else if (sActivityName.equalsIgnoreCase("MultipleGRNInvoicing")) {
            return new MultipleGRNInvoicing();
        }
        return null;
    }
}
