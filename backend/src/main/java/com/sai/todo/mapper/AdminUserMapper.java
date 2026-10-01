package com.sai.todo.mapper;

import org.springframework.stereotype.Component;

import com.sai.todo.dto.AdminUserDto;
import com.sai.todo.entity.User;


@Component
public class AdminUserMapper {

    
   public AdminUserDto toDto(User user){
       
    AdminUserDto dto=new AdminUserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());

      return dto;

   }


    
}
