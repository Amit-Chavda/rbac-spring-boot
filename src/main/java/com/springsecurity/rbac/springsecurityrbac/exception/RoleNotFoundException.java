package com.springsecurity.rbac.springsecurityrbac.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@ToString
public class RoleNotFoundException extends RuntimeException {
    private String name;
    private String message;
    private LocalDateTime timestamp;

    public RoleNotFoundException(String name, String message, LocalDateTime timestamp) {
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
    }

    public RoleNotFoundException(String message) {
        this.name = this.getClass().getName();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
