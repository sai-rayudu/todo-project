package com.sai.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sai.todo.entity.PasswordResetOtp;
import com.sai.todo.entity.User;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp,Integer> {

      Optional<PasswordResetOtp> findByUser(User user);

    
} 


    

