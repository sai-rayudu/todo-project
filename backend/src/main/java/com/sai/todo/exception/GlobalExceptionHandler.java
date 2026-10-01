package com.sai.todo.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import java.sql.SQLIntegrityConstraintViolationException;


@RestControllerAdvice


public class GlobalExceptionHandler {

    
 
@ExceptionHandler(MethodArgumentNotValidException.class)
    
public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String message= ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoNotFound(TodoNotFoundException ex){
        return ResponseEntity.status(404).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(SQLIntegrityConstraintViolationException ex){

         String message=ex.getMessage();

         if(message!=null && message.contains("uk_user_email")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Email already exists"));
         }

         if(message!=null && message.contains("uk_user_username")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Username already exists"));
         }
          return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Database constraint violation"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        ErrorResponse error=new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex){

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occured"));
    }
}
