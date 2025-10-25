package com.theawesomeengineer.taskmanager.exception;

import com.theawesomeengineer.taskmanager.model.Error;
import com.theawesomeengineer.taskmanager.model.GeneralError;
import com.theawesomeengineer.taskmanager.model.InvalidInputError;
import com.theawesomeengineer.taskmanager.model.TaskNotFoundError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleValidationExceptions(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InvalidInputError());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Error> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TaskNotFoundError(ex.getTaskId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralError());
    }

}
