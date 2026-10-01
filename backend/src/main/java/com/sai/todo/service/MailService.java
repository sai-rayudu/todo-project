package com.sai.todo.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;


@Service
public class MailService {



    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }


    public void sendEmail(String to,String subject,String body){
        SimpleMailMessage message=new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    
}
