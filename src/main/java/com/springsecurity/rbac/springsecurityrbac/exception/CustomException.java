package com.springsecurity.rbac.springsecurityrbac.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class CustomException {
    private String name;
    private String message;
    private Object description;
    private LocalDateTime timestamp;

    public CustomException(String name, String message, Object description, LocalDateTime timestamp) {
        this.name = name;
        this.message = message;
        this.description = description;
        this.timestamp = timestamp;
    }

}
