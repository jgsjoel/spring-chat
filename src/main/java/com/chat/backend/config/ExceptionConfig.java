package com.chat.backend.config;

import com.chat.backend.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionConfig {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String fileUploadSize;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List> handleInvalidFields(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error->error.getDefaultMessage()).collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String[]> handleInvalidFile(EntityNotFoundException ex) {
        return new ResponseEntity<>(new String[]{ex.getMessage()}, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String[]> response(MaxUploadSizeExceededException exception){
        return new ResponseEntity<>(new String[]{exception.getMessage()+": "+fileUploadSize}, HttpStatus.BAD_REQUEST);
    }



}

