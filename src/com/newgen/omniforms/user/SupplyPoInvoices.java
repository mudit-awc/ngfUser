/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.omniforms.user;

//import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;
import com.newgen.SupplyPoInvoices.Initiator;
import com.newgen.SupplyPoInvoices.QualityUser;
import com.newgen.SupplyPoInvoices.StoreUser;
import com.newgen.common.LogProcessing;
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
        } else if (sActivityName.equalsIgnoreCase("StoreUser")) {
            return new StoreUser();
        }
         else if (sActivityName.equalsIgnoreCase("QualityUser")) {
            return new QualityUser();
        }
        return null;
    }
}
