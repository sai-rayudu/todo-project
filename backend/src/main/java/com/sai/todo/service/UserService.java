package com.sai.todo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sai.todo.entity.PasswordResetOtp;
import com.sai.todo.entity.User;
import com.sai.todo.exception.BadRequestException;
import com.sai.todo.exception.UserNotFoundException;
import com.sai.todo.repository.PasswordResetOtpRepository;
import com.sai.todo.repository.TodoRepository;
import com.sai.todo.repository.UserRepository;

@Service
public class UserService {

private final UserRepository userRepository;
private final TodoRepository todoRepository;
private final PasswordEncoder passwordEncoder;
private final PasswordResetOtpRepository passwordResetOtpRepository;

public UserService(
    UserRepository userRepository,
    TodoRepository todoRepository,
    PasswordEncoder passwordEncoder,
    PasswordResetOtpRepository passwordResetOtpRepository
){
    this.userRepository=userRepository;
    this.todoRepository=todoRepository;
    this.passwordEncoder=passwordEncoder;
    this.passwordResetOtpRepository=passwordResetOtpRepository;
}

@Transactional
public void deleteMyAccount(String username,String currentPassword){

    User user=userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User not found"));

    if(!passwordEncoder.matches(currentPassword,user.getPassword())){
        throw new BadRequestException("Current password is incorrect");
    }

    PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElse(null);
    if(resetOtp!=null){
        passwordResetOtpRepository.delete(resetOtp);
    }

    todoRepository.deleteAll(user.getTodos());

        userRepository.delete(user);

}


    @Transactional
    public void deleteUserById(int userId){
        User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
    passwordResetOtpRepository.findByUser(user).ifPresent(passwordResetOtpRepository::delete);

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
