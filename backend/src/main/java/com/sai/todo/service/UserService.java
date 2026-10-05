package com.sai.todo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sai.todo.entity.User;
import com.sai.todo.exception.BadRequestException;
import com.sai.todo.exception.UserNotFoundException;
import com.sai.todo.repository.TodoRepository;
import com.sai.todo.repository.UserRepository;

@Service
public class UserService {

private final UserRepository userRepository;
private final TodoRepository todoRepository;
private final PasswordEncoder passwordEncoder;


public UserService(
    UserRepository userRepository,
    TodoRepository todoRepository,
    PasswordEncoder passwordEncoder
){
    this.userRepository=userRepository;
    this.todoRepository=todoRepository;
    this.passwordEncoder=passwordEncoder;

}

@Transactional
public void deleteMyAccount(String username,String currentPassword){

    User user=userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User not found"));

    if(!passwordEncoder.matches(currentPassword,user.getPassword())){
        throw new BadRequestException("Current password is incorrect");
    }

   

    todoRepository.deleteAll(user.getTodos());

        userRepository.delete(user);

}


    @Transactional
    public void deleteUserById(int userId){
        User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
  

       todoRepository.deleteAll(user.getTodos());

        userRepository.delete(user);
    }

    public void changeRole(int userId,String role){
        if(!role.equals("USER")&& !role.equals("ADMIN")){

            throw new BadRequestException("Invalid role");

        }

        User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));

        user.setRole(role);
        userRepository.save(user);
    }

}
