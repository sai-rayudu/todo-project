package com.sai.todo.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sai.todo.entity.Todo;
import com.sai.todo.entity.User;

public interface TodoRepository extends JpaRepository<Todo, Integer> ,JpaSpecificationExecutor<Todo>{
   
    Page<Todo> findByUser(User user,Pageable pageable);
    Optional<Todo> findByIdAndUser(int id,User user);
    List<Todo> findByUser(User user);

}