package com.sai.todo.repository;
import com.sai.todo.entity.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRegistrationRepository extends JpaRepository<UserRegistration,Integer> {

    Optional<UserRegistration> findByEmail(String email);
    Optional<UserRegistration> findByUsername(String username);
    
    
}
