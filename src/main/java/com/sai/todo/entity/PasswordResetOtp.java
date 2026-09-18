package com.sai.todo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private Integer otp;
    private LocalDateTime expiresAt;
    private boolean verified;

    @OneToOne
    @JoinColumn(name="user_id",unique=true)
    private User user;


    public int getId(){
        return id;
    }

     public void setId(int id){
        this.id=id;
     }

     public Integer getOtp(){
        return otp;
     }
     public void setOtp(Integer otp){
        this.otp=otp;
     }
     public LocalDateTime getExpiresAt(){
        return expiresAt;
     }
     public void setExpiresAt(LocalDateTime expiresAt){
        this.expiresAt=expiresAt;
     }
     public boolean isVerified(){
        return verified;
     }
     public void setVerified(boolean verified){
        this.verified=verified;
     }
     public User getUser(){
        return user;
     }
     public void setUser(User user){
        this.user=user;
     }
    
}
