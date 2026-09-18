package com.sai.todo.mapper;

import org.springframework.stereotype.Component;

import com.sai.todo.dto.AdminUserDetailsDto;
import com.sai.todo.entity.User;

@Component
public class AdminUserDetailsMapper {

    private final TodoMapper todoMapper;

    public AdminUserDetailsMapper(TodoMapper todoMapper){
        this.todoMapper=todoMapper;
    }

    public AdminUserDetailsDto toDto(User user){
          
          AdminUserDetailsDto dto=new AdminUserDetailsDto();
          dto.setId(user.getId());
          dto.setUsername(user.getUsername());
          dto.setEmail(user.getEmail());
          dto.setRole(user.getRole());

          dto.setTodos(todoMapper.toDtoList(user.getTodos()));

           return dto;


    }
    
}
