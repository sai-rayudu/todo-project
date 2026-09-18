package com.sai.todo.mapper;
import org.springframework.stereotype.Component;
import com.sai.todo.dto.TodoDto;
import com.sai.todo.entity.Todo;
import java.util.List;
import java.util.ArrayList;


@Component
public class TodoMapper {

    public Todo toEntity(TodoDto todoDto){
        Todo todo=new Todo();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setCompleted(todoDto.isCompleted());
        todo.setPriority(todoDto.getPriority());
        todo.setDueDate(todoDto.getDueDate());
        return todo;
    }

    public void updateEntity(TodoDto todoDto,Todo todo){
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setCompleted(todoDto.isCompleted());
        todo.setPriority(todoDto.getPriority());
            todo.setDueDate(todoDto.getDueDate());

    }

    public TodoDto toDto(Todo todo){

        TodoDto dto=new TodoDto(todo.getId(),todo.getTitle(),
         todo.getDescription(),todo.isCompleted(),todo.getPriority(), todo.getDueDate(),todo.getCreatedAt(),
         todo.getUpdatedAt());
         dto.setUsername(todo.getUser().getUsername());
         return dto;
    }

    public List<TodoDto> toDtoList(List<Todo> todos){
        List<TodoDto> todoDtos=new ArrayList<>();

        for(Todo todo:todos){
            todoDtos.add(toDto(todo));

        }
        return  todoDtos;
    }
    
}
