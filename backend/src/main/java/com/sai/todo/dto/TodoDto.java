package com.sai.todo.dto;
import com.sai.todo.enums.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public class TodoDto {

    private int id;

    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;
    private boolean completed;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
  

    public TodoDto(){

    }

    public TodoDto(int id,String title,   String description,boolean completed,Priority priority,LocalDate dueDate,LocalDateTime createdAt,LocalDateTime updatedAt){
        this.id=id;
        this.title=title;
        this.description = description;
        this.completed=completed;
        this.priority=priority;
        this.dueDate=dueDate;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }

    public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public boolean isCompleted(){
    return completed;
}
public void setCompleted(boolean completed){
    this.completed=completed;
}

public Priority getPriority(){
    return priority;

}

public void setPriority(Priority priority){
    this.priority=priority;

}

public LocalDate getDueDate(){
    return dueDate;
}

public void setDueDate(LocalDate dueDate){
    this.dueDate=dueDate;

}

public  LocalDateTime getCreatedAt(){
    return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt){
    this.createdAt=createdAt;
}

public  LocalDateTime getUpdatedAt(){
    return updatedAt;
}
public void setUpdatedAt(LocalDateTime updatedAt){
    this.updatedAt=updatedAt;
}

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
    
}
