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
import java.math.BigDecimal;
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
        if (controlName.equalsIgnoreCase("q_sb_registrationno")
                || controlName.equalsIgnoreCase("department")
                || controlName.equalsIgnoreCase("purchaseorderno")) {
            index = 0;
        } else if (controlName.equalsIgnoreCase("qoc_linenumber")) {
            index = 2;
        } else {
            index = 1;
        }
        comp.setValue(m_objPickList.getSelectedValue().get(index));
        OFUtility.render(comp);

        /*Controls for Process Name: No PO Invoice*/
        if (controlName.equalsIgnoreCase("journalname")) {
            formObject.setNGValue("journalcode", m_objPickList.getSelectedValue().get(0));

            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("paymentterm")) {
            System.out.println("inside payment term button ok");
            formObject.setNGValue("paymenttermcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
        }
        if (controlName.equalsIgnoreCase("departmentdsc")) {
            System.out.println("inside departmentdsc button ok");
            formObject.setNGValue("department", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
        }

        if (controlName.equalsIgnoreCase("department")) {
            System.out.println("inside button ok of department");
            formObject.setNGValue("department", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("fd_departmentdescription")) {
            formObject.setNGValue("fd_department", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("fd_vendordsc")) {
            formObject.setNGValue("fd_vendor", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("fd_warehousedsc")) {
            formObject.setNGValue("fd_warehousecode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgertdsgroup")) {
            formObject.setNGValue("q_ledgertdsgroupcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            formObject.setNGValue("q_ledgeradjustedoriginamount", formObject.getNGValue("q_ledgeramount"));
            String q_ledgeradjustedoriginamount = formObject.getNGValue("q_ledgeradjustedoriginamount");
            query = "select TaxPercwithPAN from TDSMaster where code = '" + m_objPickList.getSelectedValue().get(0) + "'";
            System.out.println("Query : " + query);
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("q_ledgertdspercent", result.get(0).get(0));
                String calculatedValue = objCalculations.calculatePercentAmount(q_ledgeradjustedoriginamount, result.get(0).get(0));
                formObject.setNGValue("q_ledgertdsamount", calculatedValue);
                formObject.setNGValue("q_ledgeradjustmenttdsamount", new BigDecimal(calculatedValue).setScale(0, BigDecimal.ROUND_HALF_UP));
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
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);

            query = "select MSMEType,PaymentTerm from VendorMaster where VendorCode='" + formObject.getNGValue("accountcode") + "'";
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("msmestatus", result.get(0).get(0));
                formObject.setNGValue("paymentterm", result.get(0).get(1));
            } else {
                formObject.setNGValue("msmestatus", "");
                formObject.setNGValue("paymentterm", "");
            }

            //code for vendor location tab       
            String vendorloc = formObject.getNGValue("vendorlocation");
            System.out.println("Vendor Loc: " + vendorloc);
            query = "select AddressId, StateName, Address, GSTINNumber, AddressName "
                    + "from AddressMaster where PartyCode ='" + formObject.getNGValue("accountcode") + "'";
            System.out.println("Query for Vendor" + query);
            result = formObject.getDataFromDataSource(query);
            System.out.println("result for vendor is " + result);
            if (result.size() > 0) {
                formObject.setNGValue("vendorlocation", result.get(0).get(0));
                formObject.setNGValue("vendorstate", result.get(0).get(1));
                formObject.setNGValue("vendoraddress", result.get(0).get(2));
                formObject.setNGValue("vendorgstingdiuid", result.get(0).get(3));
                formObject.setNGValue("vendortaxinformation", result.get(0).get(4));
            } else {
                formObject.setNGValue("vendorlocation", "");
                formObject.setNGValue("vendorstate", "");
                formObject.setNGValue("vendoraddress", "");
                formObject.setNGValue("vendorgstingdiuid", "");
                formObject.setNGValue("vendortaxinformation", "");
            }
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
            System.out.println("Query :" + Query);
            String taxamount = objCalculations.calculatePercentAmount(
                    formObject.getDataFromDataSource(Query).get(0).get(0),
                    HSNSACRate
            );
            formObject.setNGValue("qtd_taxamount", taxamount);
            formObject.setNGValue("qtd_taxamountadjustment", taxamount);
            formObject.setNGValue("qtd_reversechargepercent", "0");
            formObject.setNGValue("qtd_reversechargeamount", "0");
            formObject.setNGValue("qtd_gstratetype", "None");
//            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
//                    formObject.getNGValue("qtd_hsnsactype"),
//                    formObject.getNGValue("qtd_hsnsaccode"),
//                    formObject.getNGValue("qtd_taxcomponent"),
//                    formObject.getNGValue("accounttype"),
//                    formObject.getNGValue("accountcode")
//            );
//            String reversechargeamount = objCalculations.calculatePercentAmount(
//                    taxamount,
//                    reversechargerate
//            );
//            formObject.setNGValue("qtd_reversechargepercent", reversechargerate);
//            formObject.setNGValue("qtd_reversechargeamount", reversechargeamount);

            if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("HSN")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00),COALESCE(IGSTLOIPerc,0.00) from HSNRateMaster where hsncode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";

            } else if (formObject.getNGValue("qtd_hsnsactype").equalsIgnoreCase("SAC")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00),COALESCE(IGSTLOIPerc,0.00) from SACRateMaster where saccode = '" + formObject.getNGValue("qtd_hsnsaccode") + "'";
            }
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("CGST")
                        && !"0.00".equalsIgnoreCase(result.get(0).get(0))) {
                    System.out.println("Inside CGST");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(0));
                } else if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("SGST")
                        && !("0.00".equalsIgnoreCase(result.get(0).get(1)))) {
                    System.out.println("Inside SGST");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(1));
                } else if (formObject.getNGValue("qtd_taxcomponent").equalsIgnoreCase("IGST")
                        && !"0.00".equalsIgnoreCase(result.get(0).get(2))) {
                    System.out.println("Inside IGST");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", result.get(0).get(2));
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qtd_nonbusinessusagepercent", "0.00");
                    formObject.setEnabled("qtd_nonbusinessusagepercent", true);
                }
            }
        }
        if (controlName.equalsIgnoreCase("q_ledgeraccountdesc")) {
            formObject.setNGValue("q_ledgeraccountdesc", m_objPickList.getSelectedValue().get(1));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            formObject.setNGValue("q_ledgeraccount", m_objPickList.getSelectedValue().get(0));
        }

        if (controlName.equalsIgnoreCase("vendorlocation")) {
            formObject.setNGValue("vendorstate", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("vendoraddress", m_objPickList.getSelectedValue().get(3));
            formObject.setNGValue("vendorgstingdiuid", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("vendortaxinformation", m_objPickList.getSelectedValue().get(4));
            new AccountsGeneral().refreshTaxDocument(formConfig.getConfigElement("ProcessInstanceId"));
            formObject.RaiseEvent("WFSave");
        }

        if (controlName.equalsIgnoreCase("customerlocation")) {
            formObject.setNGValue("customerstate", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("customeraddress", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("customertaxinformation", m_objPickList.getSelectedValue().get(3));
            new AccountsGeneral().refreshTaxDocument(formConfig.getConfigElement("ProcessInstanceId"));
            formObject.RaiseEvent("WFSave");
        }

        if (controlName.equalsIgnoreCase("q_ledgerbusinessunit")) {
            formObject.setNGValue("q_ledgerbusinessunitcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
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
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgerwarehouse")) {
            formObject.setNGValue("q_ledgerwarehousecode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgergla")) {
            formObject.setNGValue("q_ledgerglavalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgerdepartment")) {
            formObject.setNGValue("q_ledgerdepartmentvalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ledgercostcenter")) {
            formObject.setNGValue("q_ledgercostcentervalue", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            String CostCenterValue = formObject.getNGValue("q_ledgercostcentervalue");
            query = "select value,Description from CostElement where "
                    + "AxRecId = (select CostCenterGroupRecId from CostCenter where value = '" + CostCenterValue + "')";
            System.out.println("QUery is " + query);
            result = formObject.getDataFromDataSource(query);
            if (result.size() > 0) {
                formObject.setNGValue("q_ledgercostcentergroupvalue", result.get(0).get(0));
                formObject.setNGValue("q_ledgercostcentergroup", result.get(0).get(0) + "_" + result.get(0).get(1));
            }
        }

        /*Controls for Process Name: Service PO Invoice*/
        if (controlName.equalsIgnoreCase("qwht_tdsgroup")) {
            formObject.setNGValue("qwht_tdsgroupcode", m_objPickList.getSelectedValue().get(0));
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
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("qoc_vendoraccount")) {
            formObject.setNGValue("qoc_vendoraccountcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        /*Controls for Process Name: Supply PO Invoice*/
        if (controlName.equalsIgnoreCase("purchaseorderno")) {
            query = "select purchaseorderdate, suppliercode,suppliername,businessunit,site,state,department,currency,"
                    + "deliveryterm,msmestatus,paymentterm,compositescheme,purchasestatus from "
                    + "ext_supplypoinvoices where purchaseorderno = '" + m_objPickList.getSelectedValue().get(0) + "' "
                    + "order by itemindex desc";
            System.out.println("query :" + query);
            result = formObject.getDataFromDataSource(query);
            formObject.setNGValue("purchaseorderdate", result.get(0).get(0));
            formObject.setNGValue("suppliercode", result.get(0).get(1));
            formObject.setNGValue("suppliername", result.get(0).get(2));
            formObject.setNGValue("businessunit", result.get(0).get(3));
            formObject.setNGValue("site", result.get(0).get(4));
            formObject.setNGValue("state", result.get(0).get(5));
            formObject.setNGValue("department", result.get(0).get(6));
            formObject.setNGValue("currency", result.get(0).get(7));
            formObject.setNGValue("deliveryterm", result.get(0).get(8));
            formObject.setNGValue("msmestatus", result.get(0).get(9));
            formObject.setNGValue("paymentterm", result.get(0).get(10));
            formObject.setNGValue("compositescheme", result.get(0).get(11));
            formObject.setNGValue("purchasestatus", result.get(0).get(12));
        }

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
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("Vi_lj_ProjectCtegory")) {
            formObject.setNGValue("q_ljprojectcategorycode", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("Vi_lj_ProjectCtegory", m_objPickList.getSelectedValue().get(1));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("q_ljtefrid")) {
            System.out.println("inside value setting of tefrid");
            formObject.setNGValue("q_ljtefrid", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("q_ljtefrlineitemid", m_objPickList.getSelectedValue().get(1));
            //comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            //OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("ij_itemdesc")) {
            formObject.setNGValue("ij_itemno", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
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
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
        }

        if (controlName.equalsIgnoreCase("qrawht_tdsgroup")) {
            formObject.setNGValue("qrawht_tdsgroupcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
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

//            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
//                    formObject.getNGValue("qratd_hsnsactype"),
//                    formObject.getNGValue("qratd_hsnsaccode"),
//                    formObject.getNGValue("qratd_taxcomponent"),
//                    "Vendor",
//                    formObject.getNGValue("contractor")
//            );
//            String reversechargeamount = objCalculations.calculatePercentAmount(
//                    taxamount,
//                    reversechargerate
//            );
//            formObject.setNGValue("qratd_reversechargepercent", reversechargerate);
//            formObject.setNGValue("qratd_reversechargeamount", reversechargeamount);
            if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("HSN")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00),COALESCE(IGSTLOIPerc,0.00) "
                        + "from HSNRateMaster where hsncode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";

            } else if (formObject.getNGValue("qratd_hsnsactype").equalsIgnoreCase("SAC")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00),COALESCE(IGSTLOIPerc,0.00) "
                        + "from SACRateMaster where saccode = '" + formObject.getNGValue("qratd_hsnsaccode") + "'";
            }
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("CGST")
                        && !"0.00".equalsIgnoreCase(result.get(0).get(0))) {
                    System.out.println("Inside CGST");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(0));
                } else if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("SGST")
                        && !("0.00".equalsIgnoreCase(result.get(0).get(1)))) {
                    System.out.println("Inside SGST");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(1));
                } else if (formObject.getNGValue("qratd_taxcomponent").equalsIgnoreCase("IGST")
                        && !"0.00".equalsIgnoreCase(result.get(0).get(2))) {
                    System.out.println("Inside IGST");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", result.get(0).get(2));
                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qratd_nonbusinessusagepercent", "0.00");
                    formObject.setEnabled("qratd_nonbusinessusagepercent", true);
                }
            }
        }

        if (controlName.equalsIgnoreCase("qoftd_hsnsacdescription")) {
            formObject.setNGValue("qoftd_hsnsaccode", m_objPickList.getSelectedValue().get(0));
            String HSNSACRate = objGeneral.getHSNSACRate(
                    formObject.getNGValue("qoftd_hsnsactype"),
                    formObject.getNGValue("qoftd_hsnsaccode"),
                    formObject.getNGValue("qoftd_taxcomponent")
            );
            formObject.setNGValue("qoftd_taxrate", HSNSACRate);

            String Query = "select amount from cmplx_ledgerlinedetails where "
                    + "pinstanceid = '" + formConfig.getConfigElement("ProcessInstanceId") + "'";
            String taxamount = objCalculations.calculatePercentAmount(
                    formObject.getDataFromDataSource(Query).get(0).get(0),
                    HSNSACRate
            );
            formObject.setNGValue("qoftd_taxamount", taxamount);
            formObject.setNGValue("qoftd_taxamountadjustment", taxamount);

//            String reversechargerate = new AccountsGeneral().getReverseChargeRate(
//                    formObject.getNGValue("qoftd_hsnsactype"),
//                    formObject.getNGValue("qoftd_hsnsaccode"),
//                    formObject.getNGValue("qoftd_taxcomponent"),
//                    formObject.getNGValue("accounttype"),
//                    formObject.getNGValue("accountcode")
//            );
//            String reversechargeamount = objCalculations.calculatePercentAmount(
//                    formObject.getDataFromDataSource(Query).get(0).get(0),
//                    reversechargerate
//            );
//            formObject.setNGValue("qoftd_reversechargepercent", reversechargerate);
//            formObject.setNGValue("qoftd_reversechargeamount", reversechargeamount);
            if (formObject.getNGValue("qoftd_hsnsactype").equalsIgnoreCase("HSN")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00)"
                        + ",COALESCE(IGSTLOIPerc,0.00) from HSNRateMaster where hsncode = '" + formObject.getNGValue("qoftd_hsnsaccode") + "'";

            } else if (formObject.getNGValue("qoftd_hsnsactype").equalsIgnoreCase("SAC")) {
                Query = "select COALESCE(CGSTLOIPerc,0.00),COALESCE(SGSTLOIPerc,0.00)"
                        + ",COALESCE(IGSTLOIPerc,0.00) from SACRateMaster where saccode = '" + formObject.getNGValue("qoftd_hsnsaccode") + "'";
            }
            System.out.println("Query: " + Query);
            result = formObject.getDataFromDataSource(Query);
            if (result.size() > 0) {
                if (formObject.getNGValue("qoftd_taxcomponent").equalsIgnoreCase("CGST")
                        && !"0.00".equalsIgnoreCase(result.get(0).get(0))) {
                    formObject.setNGValue("qoftd_nonbusinessusagepercent", result.get(0).get(0));

                } else if (formObject.getNGValue("qoftd_taxcomponent").equalsIgnoreCase("SGST")
                        && !("0.00".equalsIgnoreCase(result.get(0).get(1)))) {
                    System.out.println("Inside if and setting true");
                    formObject.setNGValue("qoftd_nonbusinessusagepercent", result.get(0).get(1));

                } else if (formObject.getNGValue("qoftd_taxcomponent").equalsIgnoreCase("IGST")
                        && !(!"0.00".equalsIgnoreCase(result.get(0).get(2)))) {
                    formObject.setNGValue("qoftd_nonbusinessusagepercent", result.get(0).get(2));

                } else {
                    System.out.println("Inside else");
                    formObject.setNGValue("qoftd_nonbusinessusagepercent", "0.00");
                    formObject.setEnabled("qoftd_nonbusinessusagepercent", true);
                }
            }
        }

        if (controlName.equalsIgnoreCase("qofwht_tdsgroup")) {
            System.out.println("Inside tds group picklist");
            formObject.setNGValue("qofwht_tdsgroupcode", m_objPickList.getSelectedValue().get(0));
            comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1));
            OFUtility.render(comp);
            formObject.setNGValue("qofwht_adjustedoriginamount", formObject.getNGValue("TotalFreight"));
            String q_adjustedoriginamount = formObject.getNGValue("TotalFreight");
            System.out.println("Total freight value input: " + q_adjustedoriginamount);
            if (!"".equalsIgnoreCase(q_adjustedoriginamount)) {
                query = "select TaxPercwithPAN from TDSMaster where code = '" + m_objPickList.getSelectedValue().get(0) + "'";
                System.out.println("Query : " + query);
                result = formObject.getDataFromDataSource(query);
                if (result.size() > 0) {
                    formObject.setNGValue("qofwht_tdspercent", result.get(0).get(0));
                    System.out.println("tds percent: " + result.get(0).get(0));
                    String calculatedValue = objCalculations.calculatePercentAmount(q_adjustedoriginamount, result.get(0).get(0));
                    System.out.println("calculated value: " + calculatedValue);
                    formObject.setNGValue("qofwht_tdsamount", calculatedValue);
                    formObject.setNGValue("qofwht_adjustedoriginamount", q_adjustedoriginamount);
                    System.out.println("total freight value after: " + q_adjustedoriginamount);
                    formObject.setNGValue("qofwht_adjustedtdsamount", calculatedValue);
                }
            }
        }
        if (controlName.equalsIgnoreCase("qoc_linenumber")) {
            formObject.setNGValue("qoc_linenumber", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("qoc_itemnumber", m_objPickList.getSelectedValue().get(1));
            formObject.setNGValue("qoc_ponumber", m_objPickList.getSelectedValue().get(2));
            // comp.setValue(m_objPickList.getSelectedValue().get(0) + "_" + m_objPickList.getSelectedValue().get(1) + "_" + m_objPickList.getSelectedValue().get(2));
            //  OFUtility.render(comp);
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
        if (controlName.equalsIgnoreCase("departmentdsc")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select value,description from department where "
                        + "upper(value) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select value,description from department order by description asc";
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
        if (controlName.equalsIgnoreCase("fd_departmentdescription")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Value,Description from department where "
                        + "upper(Value) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(Description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select Value,Description from department order by description asc";
            }
        }

        if (controlName.equalsIgnoreCase("fd_vendordsc")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select VendorCode,VendorName from VendorMaster where "
                        + "upper(VendorCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(VendorName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select VendorCode,VendorName from VendorMaster order by VendorCode asc";
            }
        }
        
          if (controlName.equalsIgnoreCase("fd_warehousedsc")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select WarehouseCode,WarehouseName from WarehouseMaster where "
                        + "upper(WarehouseCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(WarehouseName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select WarehouseCode,WarehouseName from WarehouseMaster order by WarehouseCode asc";
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

        }
        if (controlName.equalsIgnoreCase("qoftd_hsnsacdescription")) {
            String hsnsaccodetype = formObject.getNGValue("qoftd_hsnsactype");
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
        } else if (controlName.equalsIgnoreCase("department")) {
            System.out.println("inside search of department");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select description from department where upper(description) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select description from department order by description asc";
            }
        } else if (controlName.equalsIgnoreCase("site")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select SiteCode, SiteName from SiteMaster where upper(SiteCode) like '%" + filter_value.trim().toUpperCase() + "%' or upper(SiteName) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select SiteCode, SiteName from SiteMaster order by SiteCode asc";
            }
        } else if (controlName.equalsIgnoreCase("q_sb_registrationno")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                //query = "select distinct registrationno from cmplx_serialbatchregistration where upper(registrationno) "
                query = "select distinct registrationno from cmplx_serialbatchregistration where registrationtype = '" + formObject.getNGValue("q_sb_registrationtype") + "' and itemid = '" + formObject.getNGValue("q_sb_itemno") + "' and upper(registrationno) "
                        + "like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select distinct registrationno from cmplx_serialbatchregistration where registrationtype = '" + formObject.getNGValue("q_sb_registrationtype") + "' and itemid = '" + formObject.getNGValue("q_sb_itemno") + "' order by registrationno asc";
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
                        + "and upper(StateName) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(AddressId) like '%" + filter_value.trim().toUpperCase() + "%'";

                System.out.println("Query customerlocation:::" + query);
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
        } else if (controlName.equalsIgnoreCase("Vi_lj_ProjectCtegory")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster "
                        + "where upper(ProjCategoryCode) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(ProjCategoryDesc) like '%" + filter_value.trim().toUpperCase() + "%'";
            } else {
                query = "select ProjCategoryCode,ProjCategoryDesc from ProjectCategoryMaster order by ProjCategoryDesc asc";
            }
        } else if (controlName.equalsIgnoreCase("q_ljtefrid")) {
            System.out.println("inside button search of tefrid");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select TEFRId,TEFRLineItemId from TEFRMaster "
                        + "where businessunit = '" + formObject.getNGValue("site") + "' "
                        + "and (upper(TEFRId) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(TEFRLineItemId) like '%" + filter_value.trim().toUpperCase() + "%')";
            } else {
                query = "Select TEFRId, TEFRLineItemId from TEFRMaster where businessunit = '" + formObject.getNGValue("site") + "'";
            }
        } else if (controlName.equalsIgnoreCase("qoc_linenumber")) {
            System.out.println("inside button search of qoc_linenumber");
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select linenumber,itemid,purchaseorderno from cmplx_invoicedetails "
                        + "where pinstanceid ='" + formConfig.getConfigElement("ProcessInstanceId") + "'"
                        + "and (upper(linenumber) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(itemid) like '%" + filter_value.trim().toUpperCase() + "%'"
                        + "or upper(purchaseorderno) like '%" + filter_value.trim().toUpperCase() + "%')";
            } else {
                query = "select linenumber,itemid,purchaseorderno from cmplx_invoicedetails where pinstanceid ='" + formConfig.getConfigElement("ProcessInstanceId") + "'";
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
