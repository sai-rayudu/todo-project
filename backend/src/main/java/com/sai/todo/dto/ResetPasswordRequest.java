package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message="Username cannot be empty")
    private String username;

    @NotBlank(message="Password cannot be empty")
    @Size(min=8,max=100)
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
