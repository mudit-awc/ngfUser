/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.common;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail {

    public void mailSent(String to) {
        // change below lines accordingly
       
        String from = "dms-noreply@orientelectric.com";
        String host = "192.168.10.95"; // or IP address

        // Get the session object
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object
        Session session = Session.getDefaultInstance(properties);

        // compose the message
        try {

            // javax.mail.internet.MimeMessage class 
            // is mostly used for abstraction.
            MimeMessage message = new MimeMessage(session);

            // header field of the header.
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject("subject");
            message.setText("Hello, DMS is sending email ");

            // Send message
            Transport.send(message);
            System.out.println("Mail has been sent..");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
