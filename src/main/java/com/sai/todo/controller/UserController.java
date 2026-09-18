package com.sai.todo.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import com.sai.todo.dto.DeleteAccountRequest;
import com.sai.todo.service.UserService;

@RequestMapping("/users")

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }
    

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
        @Valid @RequestBody DeleteAccountRequest request,Authentication authentication){

            userService.deleteMyAccount(authentication.getName(),request.getCurrentPassword());

            return ResponseEntity.noContent().build();

        }
    
}
