package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String name;
    private LocalDateTime createdAt;
    private Map<PageDto, Collection<PrivilegeDto>> pagePrivilegeMap;
}
