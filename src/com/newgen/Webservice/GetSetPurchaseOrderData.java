package com.newgen.Webservice;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.Serializable;
import org.json.*;

// Referenced classes of package com.newgen.Webservice.GetSetPurchaseOrderData:
//            GetPOLineContract, GetPOLineChargesContract, SetPOData
public class GetSetPurchaseOrderData implements Serializable {

    FormReference formObject;

    public String parsePoOutputJSON(String content) throws JSONException {

        formObject = FormContext.getCurrentInstance().getFormReference();
        //  formObject.setNGValue("suppliercode", "asddsadas");
        String POLineContractXML = "";
        JSONObject objJSONObject = new JSONObject(content);

        //Check webservice IsSuccess status
        String IsSuccess = objJSONObject.optString("IsSucceess");
        String ErrorMessage = objJSONObject.optString("ErrorMessage");
        System.out.println("IsSuccess : " + IsSuccess);
        System.out.println("ErrorMessage : " + ErrorMessage);
        if (IsSuccess.equalsIgnoreCase("true")) {
            //Set Header Details
            formObject.setNGValue("suppliercode", objJSONObject.optString("VendorCode"));
            formObject.setNGValue("suppliername", objJSONObject.optString("VendorName"));
            formObject.setNGValue("loadingcity", objJSONObject.optString("LoadingCity"));
            formObject.setNGValue("site", objJSONObject.optString("BusinessUnit"));
            formObject.setNGValue("purchaseorderdate", objJSONObject.optString("AccountingDate"));

            //Set Line Details
            JSONArray objJSONArray_POLineContract = objJSONObject.getJSONArray("POLineContract");
            for (int i = 0; i < objJSONArray_POLineContract.length(); i++) {
                POLineContractXML = (new StringBuilder()).append(POLineContractXML).
                        append("<ListItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("LineNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("ItemNumber")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("ProductName")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("ItemModelGroupID")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("PurchQty")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("Unit")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("UnitPrice")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("DiscountPercentage")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("DiscountAmount")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("TDSGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("InventorySite")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("InventoryLocation")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("BusinessUnit")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("CostCenter")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("CostElement")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("Department")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("GLA")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("LedgerAccount")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("TaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("ItemTaxGroup")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("GstinGdiUid")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("HSN")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("SAC")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("VendorGSTIN")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("ItemTrackingDimension")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("QuarantineManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("PPBagManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("HLManagement")).
                        append("</SubItem><SubItem>").append(objJSONArray_POLineContract.getJSONObject(i).optString("RMManagement")).
                        append("</SubItem></ListItem>").toString();
            }
            // System.out.println((new StringBuilder()).append("PO Line XML :").append(POLineContractXML).toString());
            formObject.clear("q_polines");
            try {
                formObject.NGAddListItem("q_polines", POLineContractXML);
            } catch (Exception e) {
                System.out.println("Exception in adding line item :" + e);
            }
            return IsSuccess;
        } else {
            return ErrorMessage;
        }
    }
}
