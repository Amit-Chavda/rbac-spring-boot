package com.springsecurity.rbac.springsecurityrbac.handler;

import com.springsecurity.rbac.springsecurityrbac.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ExpiredJwtException.class)
    public final ResponseEntity<CustomException> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {

        CustomException expiredJwtException = new CustomException(
                ex.getClass().toString(),
                ex.getMessage() + " or you don't have permission to perform this operation!",
                request.getDescription(false),
                LocalDateTime.now()

        );
        return new ResponseEntity<>(expiredJwtException, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        CustomException badRequest = new CustomException(
                ex.getClass().toString(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()

        );
        return new ResponseEntity<>(badRequest, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<CustomException> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {


        CustomException accessDeniedException = new CustomException(
                ex.getClass().toString(),
                ex.getMessage() + " or you don't have permission to perform this operation!",
                request.getDescription(false),
                LocalDateTime.now()

        );
        return new ResponseEntity<>(accessDeniedException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<CustomException> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {

        CustomException badCredentialsException = new CustomException(
                ex.getClass().toString(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(badCredentialsException, HttpStatus.BAD_REQUEST);
    }

}