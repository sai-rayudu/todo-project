package com.sai.todo.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;
import java.sql.SQLIntegrityConstraintViolationException;


@RestControllerAdvice


public class GlobalExceptionHandler {

    
 @ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentNotValidException.class)
    
public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {

        String message= ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return new ErrorResponse(message);
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoNotFound(TodoNotFoundException ex){

        ErrorResponse error=new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(SQLIntegrityConstraintViolationException ex){
        ErrorResponse error=new ErrorResponse("Username already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        ErrorResponse error=new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
