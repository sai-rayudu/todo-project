package com.sai.todo.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import org.springframework.stereotype.Service;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Base64;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;


@Service
public class GmailService {

    private static final String APPLICATION_NAME="Todo App";
    private static final GsonFactory JSON_FACTORY=GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH="tokens";
    @Value("${gmail.sender}")
    private String senderEmail;

    private Gmail getGmailService() throws Exception {
        var httpTransport=GoogleNetHttpTransport.newTrustedTransport();

        InputStream in=getClass().getClassLoader().getResourceAsStream("credentials.json");

        if(in == null){
               throw new IllegalStateException("credentials.json not found");
        }

        GoogleClientSecrets clientSecrets=GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow=new GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            JSON_FACTORY,
            clientSecrets,
            Collections.singletonList(GmailScopes.GMAIL_SEND)
        ).setDataStoreFactory(
            new FileDataStoreFactory(
                new java.io.File(TOKENS_DIRECTORY_PATH)
            )
        ).setAccessType("offline").build();

        LocalServerReceiver receiver=new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential=new AuthorizationCodeInstalledApp(flow ,receiver).authorize("user");
        return new Gmail.Builder(
            httpTransport,
            JSON_FACTORY,
            credential
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public void sendEmail(String to,String subject,String body) throws Exception{
        Gmail gmail=getGmailService();
        MimeMessage email=new MimeMessage(Session.getDefaultInstance(new Properties()));

        email.setFrom(new InternetAddress(senderEmail));
        email.addRecipient(
            jakarta.mail.Message.RecipientType.TO,
            new InternetAddress(to)
        );
        email.setSubject(subject);
        email.setText(body);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);

              Message message=new Message();

              message.setRaw(
                Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.toByteArray())
              );

              gmail.users()
              .messages().send("me",message).execute();


    }

  
    
}
