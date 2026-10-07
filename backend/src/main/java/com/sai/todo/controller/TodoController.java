package com.sai.todo.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import com.sai.todo.dto.TodoDto;
import com.sai.todo.dto.TodoPageResponse;
import com.sai.todo.entity.Todo;
import com.sai.todo.enums.Priority;
import com.sai.todo.mapper.TodoMapper;
import com.sai.todo.service.TodoService;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

@RestController
public class TodoController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public TodoController(TodoService todoService, TodoMapper todoMapper) {
        this.todoService = todoService;
        this.todoMapper = todoMapper;
    }

    @GetMapping("/todos")
    public TodoPageResponse getTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be>=0 and size must be > 0");
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by("priority").and(Sort.by("id").descending()));
        return todoService.getTodos(pageable, authentication.getName());

    }

    @PostMapping("/todos")
    public TodoDto createTodo(@Valid @RequestBody TodoDto todoDto, Authentication authentication) {
        Todo todo = todoMapper.toEntity(todoDto);
        Todo savedTodo = todoService.createTodo(todo, authentication.getName());
        return todoMapper.toDto(savedTodo);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoDto> getTodoById(
            @PathVariable int id,
            Authentication authentication) {
        Todo todo = todoService.getTodoById(id, authentication.getName());
        return ResponseEntity.ok(todoMapper.toDto(todo));
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable int id,
            @Valid @RequestBody TodoDto todoDto,
            Authentication authentication) {
        Todo updatedTodo = todoService.updateTodo(id, todoDto, authentication.getName());
        return ResponseEntity.ok(todoMapper.toDto(updatedTodo));

    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable int id, Authentication authentication) {
        todoService.deleteTodo(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos/search")
    public List<TodoDto> getFilteredTodos(
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String title,
            Authentication authentication) {
        return todoService.getTodos(title, priority, authentication.getName());
    }

    @GetMapping("/user-todos")
    public List<TodoDto> getUserTodos(Authentication authentication) {
        return todoService.getUserTodos(authentication.getName());
    }

}