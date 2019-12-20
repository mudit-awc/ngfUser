/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
// NEWGEN SOFTWARE TECHNOLOGIES LIMITED
// Group : CIG
// Product / Project : OmniFlow /Remfry-Sagar
// Module : Common for all Processes
// File Name : EventListenerHandler.java
// Author : mohit.sharma
// Date written : 01/09/2014
// Description : Product Java File for handling Events. 					

package com.newgen.omniforms.user;

import com.newgen.omniforms.component.Form;
import com.newgen.omniforms.component.PickList;
import com.newgen.omniforms.component.behavior.EventListenerImplementor;
import com.newgen.omniforms.context.FormContext;
import com.newgen.omniforms.util.Constant.EVENT;
import javax.faces.event.ActionEvent;

/**
 *
 * @author mohit.sharma
 */
public class EventListenerHandler extends EventListenerImplementor{ 
	PickList pickLists=null;
	String associatedCtrl=null;
    
    public EventListenerHandler( String picklistid )
    {
        super(picklistid);       
    }
    
    public EventListenerHandler(String picklistid , EVENT compId){
        super(picklistid,compId);        
    }

    @Override
    public void btnNext_Clicked(ActionEvent ae) {
        //PickList objPckList = FormContext.getCurrentInstance().getFormReference().getNGPickList(true);
        //System.out.println(" Fetched Records = " + objPckList.getM_iTotalRecordsFetched());
    }
    
    @Override
    public void btnSearch_Clicked(ActionEvent ae){
       
    }
    
     @Override
    public void btnOk_Clicked(ActionEvent ae) {
    	// pickLists = FormContext.getCurrentInstance().getDefaultPickList();
    	 //associatedCtrl = pickLists.getAssociatedTxtCtrl();
          //System.out.println("associated control**********"+associatedCtrl);
          Form formObj = (Form) FormContext.getCurrentInstance().getFormReference();
          if (associatedCtrl.equalsIgnoreCase("Text1")) 
          {
              //TextBox tbx = (TextBox) formObj.getComponent("Text1");
              //tbx.setValue(pickLists.getSelectedValue().get(0));
             // OFUtility.render(tbx);
          }
          
    }
    
   @Override
   public void btnPrev_Clicked(ActionEvent ae) {     
      
    }
}

