package com.newgen.common;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.newgen.omniforms.FormConfig;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.component.ComboBox;
import com.newgen.omniforms.component.Form;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.component.TextBox;
import com.newgen.omniforms.component.behavior.EventListenerImplementor;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.util.Constant.EVENT;
import com.newgen.omniforms.util.OFUtility;
import java.text.ParseException;
import java.util.Calendar;

@SuppressWarnings("serial")
public class PicklistListenerHandler extends EventListenerImplementor implements
        Serializable {
    // public String sProcessName = "UBN_FTO_";

    FormReference formObject = null;
    FormConfig formConfig = null;
    PickList pickList = null;
    String controlName = "";
    String filter_value = "";
    String query = "";
    String engineName = "";
    String sessionId = "";
    String folderId = "";
    String destFolderIndex = "";
    String docIndexs = "";
    private List<List<String>> userarray;
    private String user_id;

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
        FormReference formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        PickList m_objPickList = FormContext.getCurrentInstance().getDefaultPickList();
        Form obj = (Form) FormContext.getCurrentInstance().getFormReference();
        controlName = m_objPickList.getAssociatedTxtCtrl();
        // m_objPickList.get
        System.out.println(" controlName " + controlName);
        System.out.println(" obj :" + obj);
        String sProcessName = formObject.getWFProcessName();
        General objGeneral = new General();
        if (controlName.equalsIgnoreCase("Route2_holiday_Id")) {
            TextBox comp = (TextBox) obj.getComponent(controlName);
            //  formObject.setNGValue("", m_objPickList.getSelectedValue().get(1));
            //comp.setValue(m_objPickList.getSelectedValue().get(2));
            String required_Date = "";
            try {
                String dbdate = m_objPickList.getSelectedValue().get(1);
                String[] splitdbDate = dbdate.split(" ");
                String dbdate_final = splitdbDate[0];
                System.out.println("DBDate Split: " + dbdate_final);
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dbDate_DT = Date.valueOf(dbdate_final); //coverts string to date
                System.out.println("dbDate_DT: " + required_Date);
                required_Date = sdf1.format(dbDate_DT);
                System.out.println("Required Date: " + required_Date);
            } catch (Exception ex) {
            }
            formObject.setNGValue("holiday_name", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("holiday_date", required_Date);
            // System.out.println("Holiday ID: " + m_objPickList.getSelectedValue().get(2));
            query = "Select holiday_id from complex_holiday_master where holiday_name = '" + m_objPickList.getSelectedValue().get(0) + "'";
            System.out.println("-Holiday id--->>" + query);
            List<List<String>> holidayid = formObject.getDataFromDataSource(query);
            System.out.println(holidayid.get(0).get(0));
            formObject.setNGValue("Route2_holiday_Id", holidayid.get(0).get(0));
            System.out.println("Holiday details set");

            // formObject.setNGValue(arg0, arg1)
            OFUtility.render(comp);
        }
        if (controlName.equalsIgnoreCase("TDFLOW_Course_id")) {
            System.out.println("Inside Course details ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            // comp.setValue(m_objPickList.getSelectedValue().get(0));
            System.out.println("Course id set ::" + m_objPickList.getSelectedValue().get(0) + "--value");

            formObject.setNGValue("TDFLOW_Course_id", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("c_coursename", m_objPickList.getSelectedValue().get(1));
            System.out.println("After setting course details");
        }
        if (controlName.equalsIgnoreCase("Master_courseid")) {
            System.out.println("Inside Course details ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            System.out.println("Course id set ::" + m_objPickList.getSelectedValue().get(0) + "--value");
            formObject.setNGValue("Master_courseid", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("s_title", m_objPickList.getSelectedValue().get(1));
            System.out.println("After setting course details");
        }
        if (controlName.equalsIgnoreCase("TDCancellation_request_id")) {
            System.out.println("Inside leave details ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            String reqid = m_objPickList.getSelectedValue().get(0);
            System.out.println(reqid + "VALUES--->>" + m_objPickList.getSelectedValue().get(0) + m_objPickList.getSelectedValue().get(1) + m_objPickList.getSelectedValue().get(2) + m_objPickList.getSelectedValue().get(3));
            //formObject.setNGValue("requestid", reqid);
            formObject.setNGValue("TDCancellation_request_id", reqid);
            formObject.setNGValue("movedate", m_objPickList.getSelectedValue().get(1));
            formObject.setNGValue("returndate", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("leavestatus", m_objPickList.getSelectedValue().get(3));
            System.out.println("After setting leave details");
            query = "Select user_id from ext_td where request_id = '" + reqid + "'";
            userarray = formObject.getDataFromDataSource(query);
            user_id = userarray.get(0).get(0);
            System.out.println(user_id + "Picklist user id #TDCancellation request id ");

        }
        if (controlName.equalsIgnoreCase("Route1_request_id")) {
            System.out.println("Inside leave details ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            String reqid = m_objPickList.getSelectedValue().get(0);
            System.out.println(reqid + "VALUES--->>" + m_objPickList.getSelectedValue().get(0) + m_objPickList.getSelectedValue().get(1) + m_objPickList.getSelectedValue().get(2) + m_objPickList.getSelectedValue().get(3));

            formObject.setNGValue("Route1_request_id", reqid);
            formObject.setNGValue("Route1_from_date", m_objPickList.getSelectedValue().get(1));
            formObject.setNGValue("Route1_to_date", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("leave_status", m_objPickList.getSelectedValue().get(3));
            System.out.println("After setting leave details");
            query = "Select user_id from EXT_EL where request_id = '" + reqid + "'";
            userarray = formObject.getDataFromDataSource(query);
            user_id = userarray.get(0).get(0);
            System.out.println(user_id + "Picklist user id #ELCancellation request id ");
        }
        if (controlName.equalsIgnoreCase("usr_id")) {
            System.out.println("Inside user details ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            String reqid = m_objPickList.getSelectedValue().get(0);
            System.out.println("VALUES--->>" + m_objPickList.getSelectedValue().get(0) + m_objPickList.getSelectedValue().get(1) + m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("usr_id", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("First_Name", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("Last_Name", m_objPickList.getSelectedValue().get(1));
            System.out.println("After setting leave details");
        }
        if (controlName.equalsIgnoreCase("Route1_joining_leave_id")) {
            System.out.println("Inside Leave id ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            // comp.setValue(m_objPickList.getSelectedValue().get(0));
            String reqid = m_objPickList.getSelectedValue().get(0);
            System.out.println("Leave id set ::" + m_objPickList.getSelectedValue().get(0) + "--value");
            formObject.setNGValue("Route1_joining_leave_id", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("j_fromDate", m_objPickList.getSelectedValue().get(1));
            formObject.setNGValue("j_toDate", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("j_el_days_leave", m_objPickList.getSelectedValue().get(3));
            System.out.println("After setting leave details");
            query = "Select user_id from EXT_EL where request_id = '" + reqid + "'";
            userarray = formObject.getDataFromDataSource(query);
            user_id = userarray.get(0).get(0);
            System.out.println(user_id + "Picklist user id #EL Joining request id ");

        }

        if (controlName.equalsIgnoreCase("LTC_request_id")) {
            System.out.println("Inside Leave id ");
            TextBox comp = (TextBox) obj.getComponent(controlName);
            // comp.setValue(m_objPickList.getSelectedValue().get(0));
            String reqid = m_objPickList.getSelectedValue().get(0);
            System.out.println("Leave id set ::" + m_objPickList.getSelectedValue().get(0) + "--value");
            formObject.setNGValue("LTC_request_id", m_objPickList.getSelectedValue().get(0));
            formObject.setNGValue("startDate", m_objPickList.getSelectedValue().get(1));
            formObject.setNGValue("endDate", m_objPickList.getSelectedValue().get(2));
            formObject.setNGValue("status", m_objPickList.getSelectedValue().get(3));
            System.out.println("After setting leave details");
            query = "Select user_id from EXT_LTC where request_id = '" + reqid + "'";
            userarray = formObject.getDataFromDataSource(query);
            user_id = userarray.get(0).get(0);
            System.out.println(user_id + "Picklist user id #LTC request id ");
        }


        System.out.println("--------------------------i am here---------------------------");

    }

    @Override
    public void btnNext_Clicked(ActionEvent ae) {
        // PickList objPckList =
        // FormContext.getCurrentInstance().getFormReference().getNGPickList(true);
        // System.out.println(" Fetched Records = " +
        // objPckList.getM_iTotalRecordsFetched());
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

        System.out.println("Inside method btnSearch_Clicked");
        formObject = FormContext.getCurrentInstance().getFormReference();
        formConfig = FormContext.getCurrentInstance().getFormConfig();
        pickList = FormContext.getCurrentInstance().getDefaultPickList();
        controlName = pickList.getAssociatedTxtCtrl();
        filter_value = pickList.getSearchFilterValue();
        String userName = formConfig.getConfigElement("UserName");
        String Query = "select distinct User_id from ng_user_master where user_name ='" + userName + "'";
        System.out.println(Query);
        List<List<String>> arrayList = new ArrayList();
        arrayList = formObject.getDataFromDataSource(Query);
        user_id = arrayList.get(0).get(0);
        System.out.println("User id" + user_id);
        System.out.println("controlName :" + controlName);
        System.out.println("test Filter value  : " + filter_value);

        if ((controlName.equalsIgnoreCase("Route2_holiday_Id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select holiday_name, holiday_date , holiday_id from complex_holiday_master where upper(holiday_name) like '%" + filter_value.trim().toUpperCase() + "%'";
                System.out.println("query :" + query);
            } else {
                query = "select holiday_name, holiday_date , holiday_id from complex_holiday_master order by holiday_name asc";
                System.out.println("query :" + query);
            }
        }
        if (controlName.equalsIgnoreCase("TDFLOW_Course_id")) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Course_id, Course_name from Complex_Course_Master where end_date >= getdate() and  upper(Course_name) like '%" + filter_value.trim().toUpperCase() + "%' order by Course_name";
                System.out.println("query :" + query);
            } else {
                query = "select Course_id, Course_name from Complex_Course_Master where end_date >= getdate() order by Course_name";
                System.out.println("query :" + query);
            }
        }

        //added on 11nov2016

        //1
        if ((controlName.equalsIgnoreCase("Master_courseid"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select Course_id, Course_name from Complex_Course_Master where upper(Course_id) like '%" + filter_value.trim().toUpperCase() + "%'  and end_date >= getdate() order by Course_name";
                System.out.println("query :" + query);
            } else {
                query = "select Course_id, Course_name from Complex_Course_Master where end_date >= getdate() order by Course_name";
                System.out.println("query :" + query);
            }
        }

        //2
        if ((controlName.equalsIgnoreCase("TDCancellation_request_id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select request_Id,convert(varchar(20),move_date,103),convert(varchar(20),return_date,103),request_status from EXT_TD where upper(request_Id) like '%" + filter_value.trim().toUpperCase() + "%' and request_Id is not null  and user_id=" + user_id + " and request_status in ('Approved' , 'Pending')";
                System.out.println("query :" + query);
            } else {
                query = "select request_Id,convert(varchar(20),move_date,103),convert(varchar(20),return_date,103),request_status from EXT_TD where and user_id=" + user_id + "  order by request_Id asc";
                System.out.println("query :" + query);
            }
        }

        //3
        if ((controlName.equalsIgnoreCase("Route1_request_id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select request_Id,convert(varchar(20),from_date,103),convert(varchar(20),to_date,103),request_status from EXT_EL where upper(request_Id) like '%" + filter_value.trim().toUpperCase() + "%' and request_Id is not null and request_status in ('Approved' , 'Pending')  and user_id=" + user_id + " order by request_Id";
                System.out.println("query :" + query);
            } else {
                query = "select request_Id,convert(varchar(20),from_date,103),convert(varchar(20),to_date,103),request_status from EXT_EL where and user_id=" + user_id + "  order by request_Id asc";
                System.out.println("query :" + query);
            }
        }

        //4
        if ((controlName.equalsIgnoreCase("usr_id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "Select u.firstname ,u.lastname, u.user_id from NG_User_Master u where upper(firstname) "
                        + "like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "or upper(user_id) like '%" + filter_value.trim().toUpperCase() + "%' "
                        + "and u.user_name not in ('Administrator' ,'Domain','Enterprise' , 'Supervisor','Supervisor2') order by u.user_name ";
                System.out.println("query :" + query);
            } else {
                query = "Select u.firstname ,u.lastname, u.user_id from NG_User_Master u "
                        + "where u.user_name not in ('Administrator' ,'Domain','Enterprise' , 'Supervisor','Supervisor2') order by u.user_name";
                System.out.println("query :" + query);
            }
        }

        //5
        if ((controlName.equalsIgnoreCase("Route1_joining_leave_id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select request_Id,convert(varchar(20),from_date,103),convert(varchar(20),to_date,103),el_deduct from EXT_EL where upper(request_Id) like '%" + filter_value.trim().toUpperCase() + "%' and request_status = 'Approved' and joining_flag = 'NA' and user_id = " + user_id + " order by request_Id";
                System.out.println("query :" + query);
            } else {
                query = "select request_Id,convert(varchar(20),from_date,103),convert(varchar(20),to_date,103),el_deduct from EXT_EL where request_Id is not null and request_status = 'Approved' and joining_flag = 'NA' and user_id = " + user_id + " order by request_Id";
                System.out.println("query :" + query);
            }
        }

        //6
        if ((controlName.equalsIgnoreCase("LTC_request_id"))) {
            if (!(filter_value.equalsIgnoreCase("") || filter_value.equalsIgnoreCase("*"))) {
                query = "select request_Id,convert(varchar(20),Start_date,103),convert(varchar(20),End_date,103),request_status from EXT_LTC where upper(request_Id) like '%" + filter_value.trim().toUpperCase() + "%' and request_status in ('Approved' , 'Pending') and user_id = " + user_id + "  order by request_Id";
                System.out.println("query :" + query);
            } else {
                query = "select request_Id,convert(varchar(20),Start_date,103),convert(varchar(20),End_date,103),request_status from EXT_LTC where and user_id=" + user_id + "  order by request_Id asc";
                System.out.println("query :" + query);
            }
        }


        pickList.setBatchRequired(true);
        pickList.setBatchSize(10);
        pickList.populateData(query);
        System.out.println("m_objPickList.getM_iTotalRecordsFetched() : "
                + pickList.getM_iTotalRecordsFetched());
        System.out.println("Mudit query =" + query);

        // m_objPickList =
        // FormContext.getCurrentInstance().getDefaultPickList();
        pickList.setVisible(true);

        // PickList objPckList =
        // FormContext.getCurrentInstance().getFormReference().getNGPickList(true);
		/*
         * String filter_value=objPckList.getSearchFilterValue();
         * System.out.println("Filter value : " + filter_value) ; String query=
         * "Select code,branchname from USR_0_UBN_BRANCH_MASTER where code like '"
         * +filter_value.trim()+"%'";
         * System.out.println("Filter query-->>"+query);
         * 
         * 
         * objPckList.clearInitialState(); objPckList.populateData(query);
         * objPckList.setBatchRequired(true); objPckList.setBatchSize(10);
         * objPckList.setVisible(true);
         */
    }
}
