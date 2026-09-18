package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {

    @NotBlank(message="Username cannot be empty")
    private String username;

    @NotBlank(message="Password cannot be empty")
    private String newPassword;


     public String getUsername(){
         return username;
     }

     public void setUsername(String username){
        this.username=username;
     }

     public String getNewPassword(){
        return newPassword;
     }
     public void setNewPassword(String newPassword){
        this.newPassword=newPassword;
     }

    
}
