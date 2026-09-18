package com.sai.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sai.todo.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;

import com.sai.todo.dto.AdminUserDetailsDto;
import com.sai.todo.dto.AdminUserDto;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.RegisterResponse;
import com.sai.todo.entity.User;
import com.sai.todo.exception.UserNotFoundException;
import com.sai.todo.mapper.AdminUserDetailsMapper;
import com.sai.todo.mapper.AdminUserMapper;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdminUserMapper adminUserMapper;
    private final AdminUserDetailsMapper adminUserDetailsMapper;
    private final AuthService authService;

    public AdminService(UserRepository userRepository,AdminUserMapper adminUserMapper,AdminUserDetailsMapper adminUserDetailsMapper,AuthService authService){
        this.userRepository=userRepository;
        this.adminUserMapper=adminUserMapper;
        this.adminUserDetailsMapper=adminUserDetailsMapper;
        this.authService=authService;
    }

    public List<AdminUserDto> getAllUsers(){
        return userRepository.findAll()
        .stream().map(adminUserMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailsDto searchUser(String search){

        User user;
        try{
            int id=Integer.parseInt(search);
            user=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found"));

        }
        catch(NumberFormatException e){

            user=userRepository.findByUsername(search).orElse(null);
        if(user==null){
            user=userRepository.findByEmail(search).orElseThrow(()->new UserNotFoundException("User not found"));
        }

        }

        
        return adminUserDetailsMapper.toDto(user);
    }

    public RegisterResponse createUser(RegisterRequest request) {

        return authService.register(request);
      
    }
    
}
