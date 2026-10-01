package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyOtpRequest {
    @NotBlank(message="Username cannot be empty")
    private String username;

    @NotBlank(message="OTP cannot be empty")
    private String otp;

    public String getUsername(){
        return username;
    }

    public void  setUsername(String username){
        this.username=username;
    }

    public String getOtp(){
         return otp;

    }

    public void setOtp(String otp){
        this.otp=otp;
    }
       
    
    
}
