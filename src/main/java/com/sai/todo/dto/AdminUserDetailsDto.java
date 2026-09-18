package com.sai.todo.dto;

import java.util.List;

public class AdminUserDetailsDto {

    int id;
    String username;
    String email;
    String role;
    List<TodoDto> todos;
    


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public  String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }

     public String getRole(){
        return role;
     }

     public void setRole(String role){
               
        this.role=role;
     }

     public List<TodoDto> getTodos(){
        return todos;
     }

     public void setTodos(List<TodoDto> todos){
        this.todos=todos;
     }

     public String getEmail(){
        return email;
     }

     public void setEmail(String email){
         this.email=email;
     }


}
