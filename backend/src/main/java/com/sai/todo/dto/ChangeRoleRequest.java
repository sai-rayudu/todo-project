package com.sai.todo.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeRoleRequest {
    @NotBlank(message="Role cannot be empty")
    private String role;
    

    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role=role;
    }
}
