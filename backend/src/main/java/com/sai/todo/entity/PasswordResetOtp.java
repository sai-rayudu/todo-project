package com.sai.todo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
    @Column(nullable = false)
    private String otpHash;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean verified;
    @Column(nullable = false)
    private int attempts;
    @Column(nullable = false)
    private LocalDateTime lastSentAt;

    @OneToOne
    @JoinColumn(name="user_id",unique=true, nullable = false)
    private User user;


    public int getId(){
        return id;
    }

     public void setId(int id){
        this.id=id;
     }

     public String getOtpHash(){
        return otpHash;
     }
     public void setOtpHash(String otpHash){
        this.otpHash=otpHash;
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
     public int getAttempts(){
      return attempts;
     }
     public void setAttempts(int attempts){
      this.attempts=attempts;
     }

     public LocalDateTime getLastSentAt(){
      return lastSentAt;
     }
     public void setLastSentAt(LocalDateTime lastSentAt){
      this.lastSentAt=lastSentAt;
     }
    
}
