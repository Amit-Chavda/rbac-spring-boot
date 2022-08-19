package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignRole {
    private String username;
    private List<String> roleNames;
}
