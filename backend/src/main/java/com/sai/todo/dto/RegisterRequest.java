package com.sai.todo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min=3,max=30)
    private String username;

      @NotBlank(message = "password cannot be empty")
      @Size(min=8,max=100)
    private String password;

     @NotBlank(message = "Email cannot be empty")
     @Email
     @Size(max=100)
     private String email;

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
}
