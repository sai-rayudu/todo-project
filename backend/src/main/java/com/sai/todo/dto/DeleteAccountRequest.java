package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteAccountRequest {

    
    @NotBlank(message="Password cannot be empty")
    private String  currentPassword;
    

    public String getCurrentPassword(){
        return currentPassword;

    }
    public void setCurrentPassword(String currentPassword){
        this.currentPassword=currentPassword;

    }
}
