package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String name;
    private Map<PageDto, Collection<PrivilegeDto>> pagePrivilegeMap;
}
