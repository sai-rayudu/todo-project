package com.sai.todo.specification;
import org.springframework.data.jpa.domain.Specification;

import com.sai.todo.entity.Todo;
import com.sai.todo.enums.Priority;
import com.sai.todo.entity.User;

public class TodoSpecification {

    public static Specification<Todo> hasPriority(Priority priority){

        return (root,query,criteriaBuilder)->
                criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Todo> titleContains(String title){
        return(root,query,criteriaBuilder)->criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),"%"+title.toLowerCase()+"%");
    }

    public static Specification<Todo> belongsTo(User user){
        return (root,query,criteriaBuilder)->criteriaBuilder.equal(root.get("user"),user);
    }

}
