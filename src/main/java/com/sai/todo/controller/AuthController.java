package com.sai.todo.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.sai.todo.dto.ForgotPasswordRequest;
import com.sai.todo.dto.LoginRequest;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.TodoDto;
import com.sai.todo.dto.VerifyOtpRequest;
import com.sai.todo.entity.User;


import com.sai.todo.service.AuthService;
import com.sai.todo.service.TodoService;
import com.sai.todo.service.MailService;
import com.sai.todo.dto.RegisterResponse;
import com.sai.todo.dto.ResetPasswordRequest;

import java.util.List;



@RestController
public class AuthController {
  
   
     
      private final TodoService todoService;
      private final MailService mailService;
      private final AuthService authService;
    public AuthController(TodoService todoService,AuthService authService,MailService mailService){
        
      
   
        this.todoService=todoService;
        this.authService=authService;
        this.mailService=mailService;

    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request){

       return authService.register(request);
        
    }
   


    @GetMapping("/current-user")
    public String currentUser(Authentication authentication){
        return authentication.getName();
    }


    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request){

        return authService.login(request);
    }




   

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request);

        return "OTP sent to your email";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @RequestBody VerifyOtpRequest request){
        authService.verifyOtp(request);
        return "OTP verified";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        authService.resetPassword(request);

        return "Password reset successfully";
    }











    
}
