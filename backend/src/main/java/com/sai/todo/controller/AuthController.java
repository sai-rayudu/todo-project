package com.sai.todo.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;


import com.sai.todo.dto.LoginRequest;
import com.sai.todo.dto.RegisterRequest;




import com.sai.todo.service.AuthService;

import com.sai.todo.dto.RegisterResponse;






@RestController
public class AuthController {
  
   
     
   
      private final AuthService authService;
    public AuthController(AuthService authService){
        
      
   
       
        this.authService=authService;
       

    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request){

       return authService.register(request);
        
    }
   


    @GetMapping("/current-user")
    public String currentUser(Authentication authentication){
        return authentication.getName();
    }


    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request){

        return authService.login(request);
    }




   

    
  

  

   











    
}
