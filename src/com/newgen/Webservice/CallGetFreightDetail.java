package com.newgen.Webservice;

import com.newgen.common.General;
import com.newgen.common.ReadProperty;
import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CallGetFreightDetail {

	FormReference formObject;
	General objGeneral = null;
	ReadProperty objReadProperty = null;
	String webserviceStatus;
	FormConfig formConfig = null;
	String processInstanceId = "";
	String Query = "";
	List<List<String>> result;
	
	public void GetSetFreightDetail(String loginuserid, String TransporterCode, String Transporter_InvoiceNumber) {
		formObject = FormContext.getCurrentInstance().getFormReference();
		System.out.println("inside GetSetFreightDetail method");
		try {
			System.out.println("inside FreightDetail class ");
			System.out.println("FreightDetail loginuserid : " + loginuserid);
			System.out.println("FreightDetail TransporterCode : " + TransporterCode);
			System.out.println("FreightDetail Transporter_InvoiceNumber : " + Transporter_InvoiceNumber);

			objReadProperty = new ReadProperty();
			JSONObject request_json = new JSONObject();
			try {
				System.out.println("inside try");
				request_json.put("loginuserid", loginuserid);
				request_json.put("TransporterCode", TransporterCode);
				request_json.put("Transporter_InvoiceNumber", Transporter_InvoiceNumber);
			} catch (JSONException ex) {
				Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
			}

			String outputJSON = callFreightDetailWebService(objReadProperty.getValue("getOutwardFreightDetail"),
					request_json.toString().trim());
			System.out.println("outputJSON FreightDetail: " + outputJSON);

			webserviceStatus = parserFreightDetailOutputJSON(outputJSON);
			// addToSypplyInvoice();

			System.out.println("IsStatus return :call freight detail : " + webserviceStatus);
		} catch (JSONException ex) {
			Logger.getLogger(CallPurchaseOrderService.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (!webserviceStatus.equalsIgnoreCase("success")) {
			throw new ValidatorException(new FacesMessage("Error : " + webserviceStatus, ""));
		}
	}

	private String parserFreightDetailOutputJSON(String content) throws JSONException {
		formConfig = FormContext.getCurrentInstance().getFormConfig();
		formObject = FormContext.getCurrentInstance().getFormReference();		
		processInstanceId = formConfig.getConfigElement("ProcessInstanceId");
		formObject = FormContext.getCurrentInstance().getFormReference();
		System.out.println("inside parserFreightDetailOutputJSON");
                formObject.clear("q_transportdetail");
                formObject.clear("q_tds_document");
		String FreightDetailXML = "", tds_documentXML = "", Order_Type = "", OrderNumber = "",
				Wcl_InvoiceNumber = "", Wcl_InvoiceDate = "", DeliveryPointCode = "", DeliverPointName = "", LRNo = "",
				LRDate = "", VehicleNo = "", Shoartage = "", Quantity = "", Freight = "", TotalFreight = "",
				SourcewareHouseCode = "", DestinationWareHouseCode = "", Site = "", Line_No = "",sourcewarehousecode="";
		
                String CGSTAmount = "", SGSTAmount = "", IGSTAmount = "";

		JSONObject objJSONObject = new JSONObject(content);

		// Check IsSuccess status
		String IsSuccess = objJSONObject.optString("replyCode");
		String ErrorMessage = objJSONObject.optString("replyMsg");
		System.out.println("IsSuccess : " + IsSuccess);
		System.out.println("ErrorMessage :call Freight Detail :" + ErrorMessage);
		if (IsSuccess.equalsIgnoreCase("success")) {
			System.out.println("inside trueee");
			formObject.setNGValue("order_type", objJSONObject.optString("OrderType"));
			formObject.setNGValue("DelieveryTerm", objJSONObject.optString("DeliveryTerm"));
			formObject.setNGValue("TotalFreight", objJSONObject.optString("TotalFreight"));
		//	formObject.setNGValue("FromWareHouseName", objJSONObject.optString("FromWareHouseName"));
			formObject.setNGValue("Finalbillamount", objJSONObject.optString("FinalBillAmount"));
			
			formObject.setNGValue("cgst_amount", objJSONObject.optString("CGSTAmount"));
                        formObject.setNGValue("sgst_amount", objJSONObject.optString("SGSTAmount"));
                        formObject.setNGValue("igst_amount", objJSONObject.optString("IGSTAmount"));
                        
                        CGSTAmount = objJSONObject.optString("CGSTAmount");
                        SGSTAmount = objJSONObject.optString("SGSTAmount");
                        IGSTAmount = objJSONObject.optString("IGSTAmount");

			JSONArray objJSONArray_transportdetail = objJSONObject.getJSONArray("FreightBillPassingLineDetailModel");
			System.out.println("objJSONArray_transportdetail.length() " + objJSONArray_transportdetail.length());

			for (int j = 0; j < objJSONArray_transportdetail.length(); j++) {
                            
                           	Line_No = Integer.toString(j + 1);
				Order_Type = objJSONArray_transportdetail.getJSONObject(j).optString("Order_Type");
				OrderNumber = objJSONArray_transportdetail.getJSONObject(j).optString("OrderNumber");
				Wcl_InvoiceNumber = objJSONArray_transportdetail.getJSONObject(j).optString("Wcl_InvoiceNumber");
				Wcl_InvoiceDate = objJSONArray_transportdetail.getJSONObject(j).optString("Wcl_InvoiceDate");
				DeliveryPointCode = objJSONArray_transportdetail.getJSONObject(j).optString("DeliveryPointCode");
				DeliverPointName = objJSONArray_transportdetail.getJSONObject(j).optString("DeliverPointName");
				LRNo = objJSONArray_transportdetail.getJSONObject(j).optString("LRNo");
				LRDate = objJSONArray_transportdetail.getJSONObject(j).optString("LRDate");
				VehicleNo = objJSONArray_transportdetail.getJSONObject(j).optString("VehicleNo");
				Shoartage = objJSONArray_transportdetail.getJSONObject(j).optString("Shoartage");
				Quantity = objJSONArray_transportdetail.getJSONObject(j).optString("Quantity");
				Freight = objJSONArray_transportdetail.getJSONObject(j).optString("Freight");
				TotalFreight = objJSONArray_transportdetail.getJSONObject(j).optString("TotalFreight");
				SourcewareHouseCode = objJSONArray_transportdetail.getJSONObject(j).optString("SourcewareHouseCode");
				DestinationWareHouseCode = objJSONArray_transportdetail.getJSONObject(j)
						.optString("DestinationWareHouseCode");
				Site = objJSONArray_transportdetail.getJSONObject(j).optString("Site");
                                sourcewarehousecode = objJSONArray_transportdetail.getJSONObject(0).optString("SourcewareHouseCode");
				FreightDetailXML = "<ListItem>"
						+ "<SubItem>" + Line_No + "</SubItem>"
						+ "<SubItem>" + Order_Type + "</SubItem>"
						+ "<SubItem>" + OrderNumber + "</SubItem>"
						+ "<SubItem>" + Wcl_InvoiceNumber + "</SubItem>"
						+ "<SubItem>" + Wcl_InvoiceDate	+ "</SubItem>"
						+ "<SubItem>" + DeliveryPointCode + "</SubItem>"
						+ "<SubItem>" + DeliverPointName + "</SubItem>"
						+ "<SubItem>" + LRNo + "</SubItem>"
						+ "<SubItem>" + LRDate + "</SubItem>"
						+ "<SubItem>" + VehicleNo + "</SubItem>"
						+ "<SubItem>" + Shoartage + "</SubItem>"
						+ "<SubItem>" + Quantity + "</SubItem>"
						+ "<SubItem>" + Freight + "</SubItem>"
						+ "<SubItem>" + TotalFreight + "</SubItem>"
						+ "<SubItem>" + SourcewareHouseCode + "</SubItem>"
						+ "<SubItem>" + DestinationWareHouseCode + "</SubItem>"
						+ "<SubItem>" + Site + "</SubItem>"
                                                + "</ListItem>";
				
				try {
        				formObject.NGAddListItem("q_transportdetail", FreightDetailXML);
                                        Query = "Update complex_transportdetail set Shoartage = '"+Shoartage+"' where procid ='"+processInstanceId+"' and Line_No = '"+Line_No+"'";
                                        formObject.saveDataIntoDataSource(Query);
				} catch (Exception e) {
					System.out.println("Exception in freight line item :" + e);
				}
			}
                        
                        if (CGSTAmount.equalsIgnoreCase("0") || CGSTAmount.equalsIgnoreCase("")
						|| CGSTAmount.equalsIgnoreCase(" ") || CGSTAmount.equalsIgnoreCase("0.0")) {
					System.out.println("inside tds_documentXML");
					tds_documentXML = "<ListItem>"
						+ "<SubItem>" + formObject.getNGValue("vendorgstingdiuid") + "</SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem>IGST</SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem>" +IGSTAmount+ "</SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem> </SubItem>"
						+ "<SubItem> </SubItem>"
						+ "</ListItem>";
					System.out.println("tds_documentXML: "+tds_documentXML);
                                      	formObject.NGAddListItem("q_tds_document", tds_documentXML);
				}else {
					String[] tax_comp = {"CGST","SGST",CGSTAmount,SGSTAmount};
					for(int i=0;i<2;i++) {
						tds_documentXML = "<ListItem>"
								+ "<SubItem>" + formObject.getNGValue("vendorgstingdiuid") + "</SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem>" +tax_comp[i]+ "</SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem>" +tax_comp[i+2]+ "</SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem> </SubItem>"
								+ "<SubItem> </SubItem>"
								+ "</ListItem>";
						System.out.println("tds_documentXML: "+tds_documentXML);
						formObject.NGAddListItem("q_tds_document", tds_documentXML);						
					}			
				}
                        
                        formObject.setNGValue("FromWareHouseName",sourcewarehousecode + "-" + objJSONObject.optString("FromWareHouseName"));
			String processid=processInstanceId;
                        String TotalFreight1 = formObject.getNGValue("TotalFreight");
			System.out.println("Process ID:"+processid);
			System.out.println("Total Frigth:"+TotalFreight1);
			Query = "select count(*) from cmplx_ledgerlinedetails where pinstanceid = '"+processid+"'";
			result = formObject.getDataFromDataSource(Query);
			if(result.size() >0) {
				Query = "update cmplx_ledgerlinedetails set amount = '"+TotalFreight1+"' where pinstanceid = '"+processid+"'";
				System.out.println("Insert Query:"+Query);
				formObject.saveDataIntoDataSource(Query);
			}else {
				Query = "INSERT INTO cmplx_ledgerlinedetails (amount, pinstanceid) VALUES ('"+TotalFreight1+"','"+processid+"')";
				System.out.println("Insert Query:"+Query);
				formObject.saveDataIntoDataSource(Query);
			}return IsSuccess;
		} else {
			return ErrorMessage;
		}
	}

	private String callFreightDetailWebService(String serviceURL, String inputJSON) {
		String outputJSON = "";
		try {

			System.out.println("inside my service freight");
			URL url = new URL(serviceURL);
			HttpURLConnection http = null;

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setDoInput(true);
			OutputStream out = http.getOutputStream();
			out.write(inputJSON.getBytes());
			out.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			System.out.println("br :" + br);
			outputJSON = br.readLine();
			System.out.println("Output JSON.... " + outputJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputJSON;
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}
