package com.blog.eu.exepitons.treat;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.blog.eu.exepitons.launch.EmailjaEstaEmUso;
import com.blog.eu.exepitons.launch.Invalid;
import com.blog.eu.exepitons.launch.NotFout;

@RestControllerAdvice
public class NotFount {
    @ExceptionHandler(NotFout.class)
    public Map<String, ?> handleNotFout(NotFout ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("Error", HttpStatus.NOT_FOUND.getReasonPhrase());
        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.NOT_FOUND.value());
        return map;
    }
    @ExceptionHandler(Invalid.class)
    public Map<String, ?> handleInvalid(Invalid ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("Error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.BAD_REQUEST.value());
        return map;
    }
    @ExceptionHandler(Exception.class)
    public Map<String, ?> handleException(Exception ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("Error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return map;
    }
    @ExceptionHandler(EmailjaEstaEmUso.class)
    public Map<String, ?> handleEmailjaEstaEmUso(EmailjaEstaEmUso ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("Error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.BAD_REQUEST.value());
        return map;
    }
}
