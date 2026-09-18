package com.sai.todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sai.todo.dto.AdminUserDetailsDto;
import com.sai.todo.dto.AdminUserDto;
import com.sai.todo.dto.ChangeRoleRequest;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.RegisterResponse;
import com.sai.todo.dto.TodoDto;
import com.sai.todo.entity.Todo;
import com.sai.todo.mapper.TodoMapper;
import com.sai.todo.service.AdminService;
import com.sai.todo.service.TodoService;
import com.sai.todo.service.UserService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public AdminController(AdminService adminService,UserService userService,TodoService todoService,TodoMapper todoMapper){
        this.adminService=adminService;
        this.userService=userService;
        this.todoMapper=todoMapper;
        this.todoService=todoService;
    }

    @GetMapping("/users")
    public List<AdminUserDto> getAllUsers(){
        return adminService.getAllUsers();
    }


    @GetMapping("/user/search")
    public AdminUserDetailsDto searchUser(@RequestParam String search){
        return adminService.searchUser(search);
    }
    @PostMapping("/users")
    public RegisterResponse createUser(@Valid @RequestBody RegisterRequest request){
        return adminService.createUser(request);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId){

        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable int userId,@Valid @RequestBody ChangeRoleRequest request){
        userService.changeRole(userId,request.getRole());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/users/{userId}/todos")
    public TodoDto createTodoForUser(
        @PathVariable int userId,
        @Valid @RequestBody TodoDto todoDto)
        {
            Todo todo=todoMapper.toEntity(todoDto);
            Todo savedTodo=todoService.createTodoForUser(todo,userId);
            return todoMapper.toDto(savedTodo);

    }

    @PutMapping("/todos/{todoId}")
    public TodoDto updateTodo(
        @PathVariable int todoId,
        @Valid @RequestBody TodoDto todoDto){

            Todo updatedTodo=todoService.updateTodoForAdmin(todoId,todoDto);
            return todoMapper.toDto(updatedTodo);

        }
        @DeleteMapping("/todos/{todoId}")
        public ResponseEntity<Void> deleteTodo(@PathVariable int todoId){

            todoService.deleteTodoForAdmin(todoId);
            return ResponseEntity.noContent().build();

        }
    

   
    

    
}
