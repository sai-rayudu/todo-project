package com.sai.todo.service;




import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



import com.sai.todo.dto.LoginRequest;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.RegisterResponse;


import com.sai.todo.entity.User;
import com.sai.todo.exception.BadRequestException;
import com.sai.todo.exception.UserNotFoundException;

import com.sai.todo.repository.UserRepository;
import com.sai.todo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;



@Service 
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    public AuthService(AuthenticationManager authenticationManager,JwtService jwtService,
        UserRepository userRepository,PasswordEncoder passwordEncoder){

        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    

    }

    public String login(LoginRequest request){

        Authentication authentication =authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user=(User) authentication.getPrincipal();
        String role=user.getRole();

        return jwtService.generateToken(authentication.getName(),role,user.getTokenVersion());
    }


    public RegisterResponse register(RegisterRequest request) {

    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        throw new BadRequestException("Username already exists");
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new BadRequestException("Email already exists");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole("USER");

    userRepository.save(user);

    return new RegisterResponse("Registration successful");
}

    

    
}
