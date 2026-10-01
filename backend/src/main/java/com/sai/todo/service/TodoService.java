package com.sai.todo.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.sai.todo.dto.TodoDto;
import com.sai.todo.dto.TodoPageResponse;
import com.sai.todo.entity.Todo;
import com.sai.todo.entity.User;
import com.sai.todo.enums.Priority;
import com.sai.todo.exception.TodoNotFoundException;
import com.sai.todo.mapper.TodoMapper;
import com.sai.todo.repository.TodoRepository;
import com.sai.todo.repository.UserRepository;
import com.sai.todo.specification.TodoSpecification;
import com.sai.todo.exception.UserNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;
  private final UserRepository userRepository;

  public TodoService(TodoRepository todoRepository,TodoMapper todoMapper,UserRepository userRepository){
   this.todoRepository=todoRepository;
   this.todoMapper=todoMapper;
   this.userRepository=userRepository;
  }

 

     public TodoPageResponse getTodos(Pageable pageable,String username){
      User user=findUserByUsername(username);
      Page<Todo> page= todoRepository.findByUser(user,pageable);
      List<TodoDto> todoDtos=todoMapper.toDtoList(page.getContent());

      return new TodoPageResponse(todoDtos,
        page.getNumber(),
        page.getTotalPages(),
      page.getTotalElements(),
    page.hasNext());
     }

     public Todo createTodo(Todo todo,String username) {
      User user=findUserByUsername(username);
       prepareTodo(todo, user);
        return todoRepository.save(todo);
     }
    
     public Todo getTodoById(int id,String username) {
      User user=findUserByUsername(username);
  
     return todoRepository.findByIdAndUser(id,user).orElseThrow(()-> new TodoNotFoundException("Todo not found"));

     


}

public Todo updateTodo(int id,TodoDto todoDto,String username){
         Todo todo=getTodoById(id,username);
         todoMapper.updateEntity(todoDto,todo);
         todo.setUpdatedAt(LocalDateTime.now());
       
   return todoRepository.save(todo);
}

public void deleteTodo(int id,String username) {
   Todo todo = getTodoById(id,username);

  todoRepository.delete(todo);

}



public List<TodoDto>  getTodos(String title,Priority priority,String username){
  User user=findUserByUsername(username);

        Specification<Todo> specification=Specification.allOf();
        specification=specification.and(TodoSpecification.belongsTo(user));
        if(priority!=null){
          specification=specification.and(TodoSpecification.hasPriority(priority));
        }
        if(title!=null && !title.isBlank()){
          specification=specification.and(TodoSpecification.titleContains(title));
        }
        
        List<Todo> todos=todoRepository.findAll(specification);
        return todoMapper.toDtoList(todos);
}

@Transactional
public void completeAllTodos(String username){

  User user=findUserByUsername(username);
  List<Todo> todos=todoRepository.findByUser(user);
   

  for(Todo todo:todos){
   
    todo.setCompleted(true);
           
    }
  }



@Transactional(readOnly = true)
  public List<TodoDto> getUserTodos(String username){
    User user=findUserByUsername(username);

    return todoMapper.toDtoList(user.getTodos());
  }

  public Todo createTodoForUser(Todo todo,int userId){
    User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
   prepareTodo(todo, user);
    return todoRepository.save(todo);
  }

  public Todo updateTodoForAdmin(int id,TodoDto todoDto){
    Todo todo=todoRepository.findById(id).orElseThrow(()->new TodoNotFoundException("Todo not found"));
    todoMapper.updateEntity(todoDto,todo);
    todo.setUpdatedAt(LocalDateTime.now());
    return todoRepository.save(todo);
  }

  public void deleteTodoForAdmin(int todoId){
    Todo todo=todoRepository.findById(todoId).orElseThrow(()->new TodoNotFoundException("Todo not found"));
    todoRepository.delete(todo);
  }


  private User findUserByUsername(String username){

    return userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User not found"));

  }

  private void prepareTodo(Todo todo,User user){
    LocalDateTime now=LocalDateTime.now();
    todo.setUser(user);
    todo.setCreatedAt(now);
    todo.setUpdatedAt(now);
  }


}




