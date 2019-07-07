package com.example.demo;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KIngAdvice {
    @ExceptionHandler(RuntimeException.class)
    public String getE(Exception e){
        return e.getMessage();
    }
}
