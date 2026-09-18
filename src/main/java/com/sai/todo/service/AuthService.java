package com.sai.todo.service;

import java.time.LocalDateTime;
import java.util.Random;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.sai.todo.dto.ForgotPasswordRequest;
import com.sai.todo.dto.LoginRequest;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.RegisterResponse;
import com.sai.todo.dto.ResetPasswordRequest;
import com.sai.todo.dto.VerifyOtpRequest;
import com.sai.todo.entity.PasswordResetOtp;
import com.sai.todo.entity.User;
import com.sai.todo.exception.UserNotFoundException;
import com.sai.todo.repository.PasswordResetOtpRepository;
import com.sai.todo.repository.UserRepository;
import com.sai.todo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service 
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetOtpRepository passwordResetOtpRepository;


    public AuthService(AuthenticationManager authenticationManager,JwtService jwtService,UserRepository userRepository,PasswordEncoder passwordEncoder,MailService mailService,PasswordResetOtpRepository passwordResetOtpRepository){

        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.mailService=mailService;
        this.passwordResetOtpRepository=passwordResetOtpRepository;

    }

    public String login(LoginRequest request){

        Authentication authentication =authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user=(User) authentication.getPrincipal();
        String role=user.getRole();

        return jwtService.generateToken(authentication.getName(),role);
    }


    public RegisterResponse register(RegisterRequest request){
        User user = new User();
         user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEmail(request.getEmail());
        User savedUser=userRepository.save(user);

        return new RegisterResponse(savedUser.getId(),savedUser.getUsername(),savedUser.getRole());
    }

    public void  forgotPassword(ForgotPasswordRequest request){
        User user=userRepository.findByUsername(request.getUsername()).orElseThrow(()->new UserNotFoundException("User not found"));
        int otp=100000+new Random().nextInt(900000);
        PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElse(new PasswordResetOtp());
        resetOtp.setOtp(otp);
        resetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        resetOtp.setVerified(false);
        resetOtp.setUser(user);
        passwordResetOtpRepository.save(resetOtp);
        mailService.sendEmail(user.getEmail(), "Password reset OTP","Your OTP is:"+otp);
    }

        public void verifyOtp(VerifyOtpRequest request){
           User user=userRepository.findByUsername(request.getUsername()).orElseThrow(()->new UserNotFoundException("User not found"));
            PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElseThrow(()->new RuntimeException("OTP not found"));

            if(resetOtp.getExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("OTP EXPIRED");
            }

           if(!resetOtp.getOtp().toString().equals(request.getOtp())){
            throw new RuntimeException("Invalid OTP");
           }
           resetOtp.setVerified(true);

           passwordResetOtpRepository.save(resetOtp);
        }


        public void resetPassword(ResetPasswordRequest request){

            User user=userRepository.findByUsername(request.getUsername()).orElseThrow(()->new UserNotFoundException("user not found"));

            PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElseThrow(()-> new RuntimeException("OTP not found"));

            if(!resetOtp.isVerified()){
                throw new RuntimeException("OTP not verified");

            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            resetOtp.setOtp(null);
            resetOtp.setExpiresAt(null);
            resetOtp.setVerified(false);

            passwordResetOtpRepository.save(resetOtp);
            
        }


    
}
