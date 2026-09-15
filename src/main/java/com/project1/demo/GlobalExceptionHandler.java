package com.project1.demo;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponceDto> handleGenericException(Exception e) {
        log.error("Handling Exception",e);
        var errorDto=new ErrorResponceDto(
                "Internal Server Error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(errorDto);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponceDto> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException",e);
        var errorDto=new ErrorResponceDto(
                "Not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(errorDto);
    }
    @ExceptionHandler(exception={IllegalArgumentException.class,
                                IllegalStateException.class,
                                })
    public ResponseEntity<ErrorResponceDto> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException",e);
        var errorDto=new ErrorResponceDto(
                "Bad Request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(errorDto);
    }
    @ExceptionHandler(exception={
            MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponceDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException",e);
        var errorDto=new ErrorResponceDto(
                "Bad Request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(errorDto);
    }
}
