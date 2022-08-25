package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private Collection<RoleDto> roles;
    private Collection<PagesPrivilegesDto> specialPagesPrivileges;
}

