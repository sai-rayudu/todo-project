package com.sai.todo.service;
import org.springframework.stereotype.Service;



@Service
public class MailService{

       private final GmailService gmailService;

    public MailService(GmailService gmailService){
        this.gmailService=gmailService;   
    }

    public void sendEmail(String to,String subject,String body){
       
        
     try{
          gmailService.sendEmail(to,subject,body);
            
        }
         catch(Exception e){
             throw new RuntimeException("Failed to send email", e);
        }

    }
}