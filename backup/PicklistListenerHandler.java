package com.newgen.common;

import java.io.Serializable;
import javax.faces.event.ActionEvent;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.Form;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.component.TextBox;
import com.newgen.omniforms.component.behavior.EventListenerImplementor;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.util.Constant.EVENT;
import com.newgen.omniforms.util.OFUtility;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

@SuppressWarnings("serial")
public class PicklistListenerHandler extends EventListenerImplementor implements Serializable {

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList objPicklist = null;
    Calculations objCalculations = null;
    General objGeneral = null;
    List<List<String>> result;
    String controlName = "", filter_value = "", query = "";
    private String processname;

    public PicklistListenerHandler() {
    }

    public PicklistListenerHandler(String picklistid) {
        super(picklistid);
    }

    public PicklistListenerHandler(String picklistid, EVENT compId) {
        super(picklistid, compId);
    }

    /*
     * This method will is called when click OK on Picklist and will set the
     * values in the Controls from picklist. (non-Javadoc)
     * 
     * @see com.newgen.omniforms.component.behavior.EventListenerImplementor#
     * btnOk_Clicked(javax.faces.event.ActionEvent)
     */
    @Override
    public void btnOk_Clicked(ActionEvent ae) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        objCalculations = new Calculations();
        objGeneral = new General();
        int index = 0;
        PickList m_objPickList = FormContext.getCurrentInstance().getDefaultPickList();
        Form obj = (Form) FormContext.getCurrentInstance().getFormReference();
        controlName = m_objPickList.getAssociatedTxtCtrl();
        TextBox comp = (TextBox) obj.getComponent(controlName);
        if (controlName.equalsIgnoreCase("q_sb_registrationno")) {
            index = 0;
        } else {
            index = 1;
        }
        comp.setValue(m_objPickList.getSelectedValue().get(index));
        OFUtility.render(comp);

        /*Controls for Process Name: No PO Invoice*/
        if (controlName.equalsIgnoreCase("journalname")) {
            formObject.setNGValue("journalcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("paymentterm")) {
            formObject.setNGValue("paymenttermcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
        }

        if (controlName.equalsIgnoreCase("q_ledgertdsgroup")) {
            formObject.setNGValue("q_ledgertdsgroupcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);

            String q_ledgeradjustedoriginamount = formObject.getNGValue("q_ledgeradjustedoriginamount");
            query = "select TaxPercwithPAN from TDSMaster where code = '" + m_objPickList.getSelectedValue().get(0) + "'";
            System.out.println("Query : " + query);
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("q_ledgertdspercent", result.get(0).get(0));
                String calculatedValue = objCalculations.calculatePercentAmount(q_ledgeradjustedoriginamount, result.get(0).get(0));
                formObject.setNGValue("q_ledgertdsamount", calculatedValue);
                formObject.setNGValue("q_ledgeradjustmenttdsamount", calculatedValue);
            }

            System.out.println("q_ledgertdsgroupcode: " + formObject.getNGValue("q_ledgertdsgroupcode"));
            System.out.println("ledger line 21: " + formObject.getNGValue("q_ledgerlinedetails", 0, 21));
            if (formObject.getNGValue("q_ledgertdsgroupcode").equalsIgnoreCase(formObject.getNGValue("q_ledgerlinedetails", 0, 21))) {
                formObject.setVisible("err_tdschange", false);
            } else {
                formObject.setVisible("err_tdschange", true);
            }
        }
        if (controlName.equalsIgnoreCase("account")) {
            formObject.setNGValue("accountcode", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("accountname", m_objPickList.getSelectedValue().get(1));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("qtd_hsnsacdescription")) {
            formObject.setNGValue("qtd_hsnsaccode", m_objPickList.getSelectedValue().get(0));
            String HSNSACRate = objGeneral.getHSNSACRate(
                    formObject.getNGValue("qtd_hsnsactype"),
                    formObject.getNGValue("qtd_hsnsaccode"),
                    formObject.getNGValue("qtd_taxcomponent")
            );
            formObject.setNGValue("qtd_taxrate", HSNSACRate);

            String Query = "select amount from cmplx_ledgerlinedetails where "
                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                    + "and ledgeraccount = '" + formObject.getNGValue("qtd_ledgeraccount") + "'";
            String taxamount = objCalculations.calculatePercentAmount(
                    formObject.getDataFromDataSource(Query).get(0).get(0),
                    HSNSACRate
            );
            formObject.setNGValue("qtd_taxamount", taxamount);
            formObject.setNGValue("qtd_taxamountadjustment", taxamount);

            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                    formObject.getNGValue("qtd_hsnsactype"),
                    formObject.getNGValue("qtd_hsnsaccode"),
                    formObject.getNGValue("qtd_taxcomponent"),
                    formObject.getNGValue("accounttype"),
                    formObject.getNGValue("accountcode")
            );
            String reversechargeamount = objCalculations.calculatePercentAmount(formObject.getDataFromDataSource(Query).get(0).get(0), reversechargerate);
            formObject.setNGValue("qtd_reversechargepercent", reversechargerate);
            formObject.setNGValue("qtd_reversechargeamount", reversechargeamount);

            if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("HSN")) {
                Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from HSNRateMaster where hsncode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";

            } else if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("SAC")) {
                Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from SACRateMaster where saccode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";
            }
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("CGST")
                        && ("0.00".equalsIgnoreCase(result.get(0).get(0))
                        || "0.0".equalsIgnoreCase(result.get(0).get(0))
                        || "0".equalsIgnoreCase(result.get(0).get(0))
                        || null == result.get(0).get(0)
                        || "NULL".equalsIgnoreCase(result.get(0).get(0))
                        || "Null".equalsIgnoreCase(result.get(0).get(0)))) {
                    formObject.setNGValue("qtd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qtd_nonbusinessusagepercent", true);
                } else {
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(0));
                }

                if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("SGST")
                        && ("0.00".equalsIgnoreCase(result.get(0).get(1))
                        || "0.0".equalsIgnoreCase(result.get(0).get(1))
                        || "0".equalsIgnoreCase(result.get(0).get(1))
                        || null == result.get(0).get(1)
                        || "NULL".equalsIgnoreCase(result.get(0).get(1))
                        || "Null".equalsIgnoreCase(result.get(0).get(1)))) {
                    System.out.println("Inside if and setting true");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qtd_nonbusinessusagepercent", true);
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(1));
                }

                if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("IGST")
                        && (!"0.00".equalsIgnoreCase(result.get(0).get(2))
                        || !"0.0".equalsIgnoreCase(result.get(0).get(2))
                        || !"0".equalsIgnoreCase(result.get(0).get(2))
                        || null == result.get(0).get(2)
                        || "NULL".equalsIgnoreCase(result.get(0).get(2))
                        || "Null".equalsIgnoreCase(result.get(0).get(2)))) {
                    formObject.setNGValue("qtd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qtd_nonbusinessusagepercent", true);
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(2));
                }
            }
        }
        if (controlName.equalsIgnoreCase("q_ledgeraccountdesc")) {
            formObject.setNGValue("q_ledgeraccountdesc", m_objPickList.getSelectedValue().get(1));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            formObject.setNGValue("q_ledgeraccount", m_objPickList.getSelectedValue().get(0));
        }

        if (controlName.equalsIgnoreCase("vendorlocation")) {
            formObject.setNGValue("vendorstate", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("vendoraddress", m_objPickList.getSelectedValue().get(3));
            formObject.setNGValue("vendorgstingdiuid", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("vendortaxinformation", m_objPickList.getSelectedValue().get(4));
        }

        if (controlName.equalsIgnoreCase("customerlocation")) {
            formObject.setNGValue("customerstate", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("customeraddress", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("customertaxinformation", m_objPickList.getSelectedValue().get(3));
        }

        if (controlName.equalsIgnoreCase("q_ledgerbusinessunit")) {
            formObject.setNGValue("q_ledgerbusinessunitcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            String BusinessunitCode = formObject.getNGValue("q_ledgerbusinessunitcode");
            if (BusinessunitCode.equalsIgnoreCase("104")) {
                System.out.println("inside if of pick button of business unit");
                formObject.setEnabled("q_ledgerstate", true);
                formObject.setNGValue("q_ledgerstate", "--Select--");
            } else {
                System.out.println("inside else");
                formObject.setEnabled("q_ledgerstate", false);
                query = "select StateName from StateMaster where"
                        + " AxRecId = (select StateAxRecId from SiteStateLinking where "
                        + "businessunitaxrecid = (select AxRecId from SiteMaster where SiteCode = '" + BusinessunitCode + "'))";
                System.out.println("Quer is " + query);
                result = formObject.getDataFromDataSource(query);
                if (result.size() > 0) {
                    formObject.setNGValue("q_ledgerstate", result.get(0).get(0));
                }
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgerrso")) {
            formObject.setNGValue("q_ledgerrsocode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgerwarehouse")) {
            formObject.setNGValue("q_ledgerwarehousecode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgergla")) {
            formObject.setNGValue("q_ledgerglavalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgerdepartment")) {
            formObject.setNGValue("q_ledgerdepartmentvalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgercostcenter")) {
            formObject.setNGValue("q_ledgercostcentervalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            String CostCenterValue = formObject.getNGValue("q_ledgercostcentervalue");
            query = "select value,Description from CostElement where "
                    + "AxRecId = (select CostCenterGroupRecId from CostCenter where value = '" + CostCenterValue + "')";
            System.out.println("QUery is " + query);
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("q_ledgercostcentergroupvalue", result.get(0).get(0));
                formObject.setNGValue("q_ledgercostcentergroup", result.get(0).get(0) + "-" + result.get(0).get(1));
            }
        }

        /*Controls for Process Name: Service PO Invoice*/
        if (controlName.equalsIgnoreCase("qwht_tdsgroup")) {
            String adjustedamountorigin = formObject.getNGValue("qwht_adjustedoriginamount");
            query = "select TaxPercwithPAN from TDSMaster where code = '" + m_objPickList.getSelectedValue().get(0) + "'";
            System.out.println("Query : " + query);
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("qwht_tdspercent", result.get(0).get(0));
                //formObject.setNGValue("adjustedtdsamountorigin", newbaseamount);
                String calculatedValue = objCalculations.calculatePercentAmount(adjustedamountorigin, result.get(0).get(0));
                formObject.setNGValue("qwht_tdsamount", calculatedValue);
                formObject.setNGValue("qwht_adjustedtdsamount", calculatedValue);
            }
        }
        if (controlName.equalsIgnoreCase("qtd_hsnsaccode")) {
            formObject.setNGValue("qtd_hsnsaccode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("qoc_vendoraccount")) {
            formObject.setNGValue("qoc_vendoraccountcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        /*Controls for Process Name: Supply PO Invoice*/
        /*Controls for Process Name: RA Bills*/
//         if (controlName.equalsIgnoreCase("vendorlocation")) {
//            formObject.setNGValue("vendorstate", m_objPickList.getSelectedValue().get(0));
//            formObject.setNGValue("vendoraddress", m_objPickList.getSelectedValue().get(3));
//            formObject.setNGValue("vendorgstingdiuid", m_objPickList.getSelectedValue().get(2));
//            formObject.setNGValue("vendortaxinformation", m_objPickList.getSelectedValue().get(4));
//        }
        if (controlName.equalsIgnoreCase("companylocation")) {
            formObject.setNGValue("companystate", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("companyaddress", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("companytaxinformation", m_objPickList.getSelectedValue().get(3));
        }
        if (controlName.equalsIgnoreCase("ij_projectcategory")) {
            formObject.setNGValue("ij_projectcategorycode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("ij_itemdesc")) {
            formObject.setNGValue("ij_itemno", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("qpo_structurename")) {
            formObject.setNGValue("qpo_structurecode", m_objPickList.getSelectedValue().get(0));
//            formObject.setNGValue("qpo_structurename", m_objPickList.getSelectedValue().get(1));
        }
        if (controlName.equalsIgnoreCase("ij_configuration")) {
            formObject.setNGValue("ij_configuration", m_objPickList.getSelectedValue().get(1));
        }
        if (controlName.equalsIgnoreCase("ij_site")) {
            formObject.setNGValue("ij_sitecode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("qrawht_tdsgroup")) {
            formObject.setNGValue("qrawht_tdsgroupcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "-" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            formObject.setNGValue("qrawht_adjustedoriginamount", formObject.getNGValue("invoiceamount"));
            String q_adjustedoriginamount = formObject.getNGValue("qrawht_adjustedoriginamount");
            if (!"".equalsIgnoreCase(q_adjustedoriginamount)) {
                query = "select TaxPercwithPAN from TDSMaster where code = '" + m_objPickList.getSelectedValue().get(0) + "'";
                System.out.println("Query : " + query);
                result = formObject.getDataFromDataSource(query);
                if (result.size() > 0) {
                    formObject.setNGValue("qrawht_tdspercent", result.get(0).get(0));
                    String calculatedValue = objCalculations.calculatePercentAmount(q_adjustedoriginamount, result.get(0).get(0));
                    formObject.setNGValue("qrawht_tdsamount", calculatedValue);
                    formObject.setNGValue("qrawht_adjustedtdsamount", calculatedValue);
                }
            }
        }

        if (controlName.equalsIgnoreCase("qratd_hsnsacdescription")) {
            formObject.setNGValue("qratd_hsnsaccode", m_objPickList.getSelectedValue().get(0));
            String HSNSACRate = objGeneral.getHSNSACRate(
                    formObject.getNGValue("qratd_hsnsactype"),
                    formObject.getNGValue("qratd_hsnsaccode"),
                    formObject.getNGValue("qratd_taxcomponent")
            );
            formObject.setNGValue("qratd_taxrate", HSNSACRate);

            String Query = "select projectdebitamount from cmplx_linejournal where "
                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "' "
                    + "and projectcode = '" + formObject.getNGValue("qratd_projectcode") + "'";
            String taxamount = objCalculations.calculatePercentAmount(
                    formObject.getDataFromDataSource(Query).get(0).get(0),
                    HSNSACRate
            );
            formObject.setNGValue("qratd_taxamount", taxamount);
            formObject.setNGValue("qratd_taxamountadjustment", taxamount);

            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
                    formObject.getNGValue("qratd_hsnsactype"),
                    formObject.getNGValue("qratd_hsnsaccode"),
                    formObject.getNGValue("qratd_taxcomponent"),
                    "Vendor",
                    formObject.getNGValue("contractor")
            );
            String reversechargeamount = objCalculations.calculatePercentAmount(
                    formObject.getDataFromDataSource(Query).get(0).get(0),
                    reversechargerate
            );
            formObject.setNGValue("qratd_reversechargepercent", reversechargerate);
            formObject.setNGValue("qratd_reversechargeamount", reversechargeamount);

            if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("HSN")) {
                Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from HSNRateMaster "
                        + "where hsncode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";

            } else if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("SAC")) {
                Query = "select CGSTLOIPerc,SGSTLOIPerc,IGSTLOIPerc from SACRateMaster "
                        + "where saccode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";
            }
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("CGST")
                        && ("0.00".equalsIgnoreCase(result.get(0).get(0))
                        || "0.0".equalsIgnoreCase(result.get(0).get(0))
                        || "0".equalsIgnoreCase(result.get(0).get(0))
                        || null == result.get(0).get(0)
                        || "NULL".equalsIgnoreCase(result.get(0).get(0))
                        || "Null".equalsIgnoreCase(result.get(0).get(0)))) {
                    formObject.setNGValue("qratd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qratd_nonbusinessusagepercent", true);
                } else {
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(0));
                }

                if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("SGST")
                        && ("0.00".equalsIgnoreCase(result.get(0).get(1))
                        || "0.0".equalsIgnoreCase(result.get(0).get(1))
                        || "0".equalsIgnoreCase(result.get(0).get(1))
                        || null == result.get(0).get(1)
                        || "NULL".equalsIgnoreCase(result.get(0).get(1))
                        || "Null".equalsIgnoreCase(result.get(0).get(1)))) {
                    System.out.println("Inside if and setting true");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qratd_nonbusinessusagepercent", true);
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(1));
                }

                if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("IGST")
                        && (!"0.00".equalsIgnoreCase(result.get(0).get(2))
                        || !"0.0".equalsIgnoreCase(result.get(0).get(2))
                        || !"0".equalsIgnoreCase(result.get(0).get(2))
                        || null == result.get(0).get(2)
                        || "NULL".equalsIgnoreCase(result.get(0).get(2))
                        || "Null".equalsIgnoreCase(result.get(0).get(2)))) {
                    formObject.setNGValue("qratd_nonbusinessusagepercent", "");
                    formObject.setEnabled("qratd_nonbusinessusagepercent", true);
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(2));
                }
            }
        }

    }

    @Override
    public void btnNext_Clicked(ActionEvent ae) {
    }

    /*
     * This method is called on Search click button of Picklist ,we can search
     * on the value in search text box. (non-Javadoc)
     * 
     * @see com.newgen.omniforms.component.behavior.EventListenerImplementor#
     * btnSearch_Clicked(javax.faces.event.ActionEvent)
     */
    @Override
    public void btnSearch_Clicked(ActionEvent ae) {
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        processname = formObject.getWFProcessName();
        objPicklist = FormContext.getCurrentInstance().getDefaultPickList();
        controlName = objPicklist.getAssociatedTxtCtrl();
        filter_value = objPicklist.getSearchFilterValue();

        /*Controls for Process Name: No PO Invoice*/
        if (controlName.equalsIgnoreCase("qtd_hsnsacdescription")) {
            String hsnsaccodetype = formObject.getNGValue("qtd_hsnsactype");
            if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select HSNCode,Description from HSNMaster where "
                            + "upper(HSNCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query :" + query);
                } else {
                    query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                    System.out.println("query :" + query);
                }

            } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select SACCode,Description from SACMaster where "
                            + "upper(SACCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query :" + query);
                } else {
                    query = "select SACCode,Description from SACMaster order by SACCode asc";
                    System.out.println("query :" + query);
                }

            }
        }

        if (controlName.equalsIgnoreCase("q_ledgeraccountdesc")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select AccountId,Description from LedgerACMaster where "
                        + "upper(AccountId) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select AccountId,Description from LedgerACMaster order by AccountId asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgerbusinessunit")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select SiteCode, SiteName from SiteMaster where "
                        + "upper(SiteCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(SiteName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgercostcenter")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select value,Description from CostCenter where "
                        + "upper(value) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select value, Description from CostCenter order by value asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgerdepartment")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from Department where "
                        + "upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from Department order by Value asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgergla")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from GLAMaster where "
                        + "upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from GLAMaster order by Value asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgertdsgroup")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Code,Description from TDSMaster where "
                        + "upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Code,Description from TDSMaster order by Code asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgerwarehouse")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select warehousecode,warehousename from warehousemaster where "
                        + "upper(warehousecode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(warehousename) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select warehousecode,warehousename from warehousemaster order by warehousecode asc";
            }
        }

        if (controlName.equalsIgnoreCase("q_ledgerrso")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select rsocode, rsoname from rsomaster where "
                        + "upper(rsocode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(rsoname) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select rsocode, rsoname from rsomaster order by rsocode asc";
            }
        }

        /*Controls for Process Name: Service PO Invoice*/
        if (controlName.equalsIgnoreCase("qoc_vendoraccount")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select VendorCode, VendorName from VendorMaster where "
                        + "upper(VendorCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(VendorName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select VendorCode, VendorName from VendorMaster order by VendorCode asc";
            }
        }

        /*Controls for Process Name: RA Bills*/
        if (controlName.equalsIgnoreCase("qratd_hsnsacdescription")) {
            String hsnsaccodetype = formObject.getNGValue("qratd_hsnsactype");
            if (hsnsaccodetype.equalsIgnoreCase("HSN")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select HSNCode,Description from HSNMaster where "
                            + "upper(HSNCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query :" + query);
                } else {
                    query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                    System.out.println("query :" + query);
                }

            } else if (hsnsaccodetype.equalsIgnoreCase("SAC")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select SACCode,Description from SACMaster where "
                            + "upper(SACCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query :" + query);
                } else {
                    query = "select SACCode,Description from SACMaster order by SACCode asc";
                    System.out.println("query :" + query);
                }

            }
        } else if (controlName.equalsIgnoreCase("companylocation")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select StateName,AddressId,Address,AddressName from AddressMaster "
                        + "where AddressType = 'Company' "
                        + "and upper(AddressId) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(AddressName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select StateName,AddressId,Address,AddressName from AddressMaster where AddressType = 'Company' ";
            }
        }

        /*Controls for Process Name: Supply PO Invoice*/
        if ((controlName.equalsIgnoreCase("account"))) {
            String accounttype = formObject.getNGValue("accounttype");
            if (accounttype.equalsIgnoreCase("Vendor")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select VendorCode, VendorName from VendorMaster where "
                            + "upper(VendorCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(VendorName) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query :" + query);
                } else {
                    query = "select VendorCode, VendorName from VendorMaster order by VendorCode asc";
                    System.out.println("query :" + query);
                }
            } else {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select Code,Description from CustomerMaster where "
                            + "upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query" + query);
                } else {
                    query = "select Code,Description from CustomerMaster order by Code asc";
                    System.out.println("query :" + query);
                }
            }
        } else if (controlName.equalsIgnoreCase("hsnsaccodevalue")) {
            String gsttype = formObject.getNGValue("hsnsaccodetype");
            if (gsttype.equalsIgnoreCase("SAC")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select SACCode,Description from SACMaster where"
                            + "upper(SACCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query of SAC if---  " + query);
                } else {
                    query = "select SACCode,Description from SACMaster order by SACCode asc";
                    System.out.println("query of SAC else");
                }
            } else {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select HSNCode,Description from HSNMaster where upper(HSNCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query of HSn if---" + query);
                } else {
                    query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                    System.out.println("query of HSN else");
                }
            }
        } else if (controlName.equalsIgnoreCase("tdsgroup")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Code,Description from TDSMaster where upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query of tds group if---  " + query);
            } else {
                query = "select Code,Description from TDSMaster order by Code asc";
                System.out.println("query of tds else");
            }
        } else if (controlName.equalsIgnoreCase("journalname")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Code,Description from JournalNamemaster where upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Code,Description from JournalNamemaster order by Code asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ledgerlinedetails_ledgeraccount")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select AccountId,Description from LedgerACMaster where upper(AccountId) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select AccountId,Description from LedgerACMaster order by AccountId asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ledgerlinedetails_fdcostcenter")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from CostCenter where upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from CostCenter order by Value asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ledgerlinedetails_fddepartment")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from Department where upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from Department order by Value asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ledgerlinedetails_fdgla")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from GLAMaster where upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from GLAMaster order by Value asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ledgerlinedetails_tdsgroup")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Code,Description from TDSMaster where upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Code,Description from TDSMaster order by Code asc";
            }
        } else if (controlName.equalsIgnoreCase("hsnsacvalue")) {
            String gsttype = formObject.getNGValue("hsnsacvalue");
            if (gsttype.equalsIgnoreCase("SAC")) {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select SACCode,Description from SACMaster where"
                            + "upper(SACCode) like '%" + filter_value.trim().toUpperCase() + "%' or "
                            + "upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query of SAC if---  " + query);
                } else {
                    query = "select SACCode,Description from SACMaster order by SACCode asc";
                    System.out.println("query of SAC else");
                }
            } else {
                if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                    query = "select HSNCode,Description from HSNMaster where upper(HSNCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                    System.out.println("query of HSn if---" + query);
                } else {
                    query = "select HSNCode,Description from HSNMaster order by HSNCode asc";
                    System.out.println("query of HSN else");
                }
            }
        } else if (controlName.equalsIgnoreCase("tdsgrp")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Code,Description from TDSMaster where upper(Code) like '%" + filter_value.trim().toUpperCase() + "%' or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query of tdsgrp if---  " + query);
            } else {
                query = "select Code,Description from TDSMaster order by Code asc";
                System.out.println("query of tdsgrp else");
            }
        } else if (controlName.equalsIgnoreCase("paymentterm")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select PaymentTermCode, PaymentTermDesc from PaymentTermMaster where upper(PaymentTermCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(PaymentTermDesc) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select PaymentTermCode, PaymentTermDesc from PaymentTermMaster order by PaymentTermCode asc";
            }
        } else if (controlName.equalsIgnoreCase("site")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select SiteCode, SiteName from SiteMaster where upper(SiteCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(SiteName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
            }
        } else if (controlName.equalsIgnoreCase("q_sb_registrationno")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select distinct registrationno from cmplx_serialbatchregistration where upper(registrationno) "
                        + "like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select distinct registrationno from cmplx_serialbatchregistration order by registrationno asc";
            }
        } else if (controlName.equalsIgnoreCase("vendorlocation")) {
            String vendorcodefieldid = "";
            if (processname.equalsIgnoreCase("RABill")) {
                vendorcodefieldid = "contractor";
            } else {
                vendorcodefieldid = "accountcode";
            }

            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select StateName, AddressId, GSTINNumber,Address,AddressName from AddressMaster "
                        + "where AddressType = 'Vendor' and PartyCode = '" + formObject.getNGValue(vendorcodefieldid) + "' "
                        + "and upper(AddressId) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(AddressName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select StateName, AddressId, GSTINNumber,Address,AddressName from AddressMaster "
                        + "where AddressType = 'Vendor' and PartyCode = '" + formObject.getNGValue(vendorcodefieldid) + "'";
            }
        } else if (controlName.equalsIgnoreCase("customerlocation")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select StateName,AddressId,Address,AddressName from AddressMaster "
                        + "where AddressType = 'Company' "
                        + "and upper(AddressId) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(AddressName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select StateName,AddressId,Address,AddressName from AddressMaster where AddressType = 'Company' ";
            }
        } else if (controlName.equalsIgnoreCase("qpo_structurename")) {
            System.out.println("search wale code m aagya");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select ProjectCode,ProjectDesc from ProjectMaster "
                        + "where upper(ProjectCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(ProjectDesc) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query of tds group if---  " + query);
            } else {
                query = "select ProjectCode,ProjectDesc from ProjectMaster";
                System.out.println("query of tds else");
            }
        } else if (controlName.equalsIgnoreCase("abs_configuration")) {
            System.out.println("search abs_configuration wale code m aagya");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select ItemCode,CofigurationCode from ItemConfigurationMaster "
                        + "where upper(ItemCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(CofigurationCode) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query of abs_configuration group if---  " + query);
            } else {
                query = "select ItemCode,CofigurationCode from ItemConfigurationMaster";
                System.out.println("query of abs_configuration else");
            }
        } else if (controlName.equalsIgnoreCase("abs_site")) {
            System.out.println("search wale code m aagya");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select SiteCode, SiteName from SiteMaster where upper(SiteCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(SiteName) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query of site group if---  " + query);
            } else {
                query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
                System.out.println("query of site else");
            }
        } else if (controlName.equalsIgnoreCase("ij_itemno")) {
            System.out.println("search wale code m aagya");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select ItemCode,ItemShortDesc from RABillItemMaster "
                        + "where upper(ItemCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(ItemShortDesc) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select ItemCode,ItemShortDesc from RABillItemMaster order by ItemCode asc";
                System.out.println("query of Item else");
            }
        } else if (controlName.equalsIgnoreCase("ij_projectcategory")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select  ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster "
                        + "where upper(ProjCategoryCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(ProjCategoryDesc) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select  ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster order by ProjCategoryDesc asc";
            }
        }

        System.out.println("outside if " + query);
        objPicklist.setBatchRequired(true);
        System.out.println("inside true");
        objPicklist.setBatchSize(10);
        System.out.println("batch size");
        objPicklist.populateData(query);
        System.out.println("Mudit query =" + query);
        objPicklist.setVisible(true);
    }

    public void openPickList(String ControlName, String Columns, String WindowTitle, int Width, int Height, String PopulateDataQuery) {
        formObject = FormContext.getCurrentInstance().getFormReference();

//        System.out.println("Inside openPickList : " + ControlName);
        objPicklist = formObject.getNGPickList(ControlName, Columns, true, 50);
        objPicklist.setWindowTitle(WindowTitle);
        objPicklist.setWidth(Width);
        objPicklist.setHeight(Height);
        objPicklist.setPicklistHeaderBGColor(new Color(204, 102, 0));
        objPicklist.setPicklistHeaderFGColor(Color.WHITE);
        objPicklist.setColumnHeaderBackColor(new Color(33, 116, 115));
        objPicklist.setColumnHeaderForeColor(Color.WHITE);
        objPicklist.setButtonFontStyle("Arial", 12, Font.ROMAN_BASELINE, Character.lowSurrogate(1));
//        objPicklist.setPicklistButtonBGColor(new Color(33, 116, 115));
//        objPicklist.setPicklistButtonBGColor(Color.WHITE);
        objPicklist.setFontStyle("Arial", 10, Font.HANGING_BASELINE, Character.lowSurrogate(1));
        objPicklist.addPickListListener(new PicklistListenerHandler(objPicklist.getClientId()));
        objPicklist.populateData(PopulateDataQuery);
        objPicklist.setVisible(true);
    }

}
