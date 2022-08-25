package com.springsecurity.rbac.springsecurityrbac.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@ToString
public class RoleAlreadyExistException extends RuntimeException {
    private String name;
    private String message;
    private LocalDateTime timestamp;

    public RoleAlreadyExistException(String name, String message, LocalDateTime timestamp) {
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
    }

    public RoleAlreadyExistException(String message) {
        this.name = this.getClass().getName();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
