package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Collection<RoleDto> roles;
    private Collection<PagesPrivilegesDto> specialPagesPrivileges;
}

