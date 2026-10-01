package com.sai.todo.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sai.todo.entity.User;

public interface UserRepository extends JpaRepository<User,Integer> {
    
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
