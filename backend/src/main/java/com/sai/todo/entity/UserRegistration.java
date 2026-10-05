package com.sai.todo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;



@Entity
@Table(
    name="user_registration",
    uniqueConstraints={
        @UniqueConstraint(name="uk_registration_username",columnNames="username"),
        @UniqueConstraint(name="uk_registration_email",columnNames="email")
    }
)


public class UserRegistration {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name="otp_hash",nullable=false)
    private String otpHash;

    @Column(name="otp_expires_at",nullable=false)
    private LocalDateTime otpExpiresAt;

    @Column(name="otp_attempts",nullable=false)
    private int otpAttempts=0;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime updatedAt;



    public Integer getId(){
        return id;
    }
     public void setId(Integer id){
        this.id=id;
     }   
     public String getUsername(){
        return username;
     }
     public void setUsername(String username){
        this.username=username;
     }
     public String getEmail(){
        return email;
     }
     public void setEmail(String email){
        this.email=email;
     }
     public String getPassword(){
        return password;
     }
     public void setPassword(String password){
        this.password=password;

     }
     public String getOtpHash(){
        return otpHash;
     }
     public void setOtpHash(String otpHash){
        this.otpHash=otpHash;
        
     }
     public LocalDateTime getOtpExpiresAt(){
             return otpExpiresAt;
     }
     public void setOtpExpiresAt(LocalDateTime otpExpiresAt){
        this.otpExpiresAt=otpExpiresAt;
     }
     public int getOtpAttempts(){
        return otpAttempts;
     }
     public void setOtpAttempts(int otpAttempts){
        this.otpAttempts=otpAttempts;

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

}
