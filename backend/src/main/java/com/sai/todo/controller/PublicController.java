package com.sai.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/")
    public String home() {
        return "Todo App";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return """
                Todo App Privacy Policy

                This application is a Todo management application.

                We collect information such as username, email address, and password
                to provide authentication and application functionality.

                Passwords are securely hashed and are not stored as plain text.

                Email addresses may be used to send account verification and
                password-reset OTPs.

                We do not sell or share personal information for advertising purposes.

                If you have questions about this privacy policy, contact:
                sairayudu3575@gmail.com
                """;
    }
}
