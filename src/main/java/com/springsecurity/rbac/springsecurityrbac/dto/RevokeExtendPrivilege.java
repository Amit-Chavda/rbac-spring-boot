package com.springsecurity.rbac.springsecurityrbac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevokeExtendPrivilege {
    private String username;
    private Map<PageDto, Collection<PrivilegeDto>> specialPrivilegesMap;
}
