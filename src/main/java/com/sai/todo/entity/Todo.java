package com.sai.todo.entity;
import com.sai.todo.enums.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
public class Todo {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;
    private boolean completed;

    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
@JoinColumn(name = "user_id")

    private User user;
    

    public Todo() {
    }

    public Todo(int id, String title,String description,boolean completed) {
        this.id = id;
        this.title = title;
           this.description = description;
           this.completed=completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

     public void setTitle(String title) {
        this.title = title;
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

public LocalDateTime getCreatedAt(){
    return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt){
    this.createdAt=createdAt;
    
}

public LocalDateTime getUpdatedAt(){
    return updatedAt;
}
public void setUpdatedAt(LocalDateTime updatedAt){
    this.updatedAt=updatedAt;
    
}
public User getUser(){
    return user;
}
public void setUser(User user){
    this.user=user;  
}
}