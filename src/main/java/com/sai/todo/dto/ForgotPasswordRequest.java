package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message="Username cannot be empty")
    private String username;
    
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
}
