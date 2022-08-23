package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendRole {
    private String username;
    private Collection<PagesPrivilegesDto> pagesPrivilegesDtos;
}
