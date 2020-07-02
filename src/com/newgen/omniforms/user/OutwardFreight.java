package com.newgen.omniforms.user;

import com.newgen.OutwardFreight.Accounts;
import com.newgen.OutwardFreight.Approver;
import com.newgen.OutwardFreight.Initiator;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.listener.FormListener;

public class OutwardFreight implements IFormListenerFactory {

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
            System.out.println("Returning to OutwardFreight Head");
            return new Initiator();
        } else if (sActivityName.equalsIgnoreCase("Approver")) {
            return new Approver();
        } else if (sActivityName.equalsIgnoreCase("AccountsMaker")
                || sActivityName.equalsIgnoreCase("AccountsChecker")
                || sActivityName.equalsIgnoreCase("AXSyncException")) {
            return new Accounts();

        }
        return null;
    }
}
