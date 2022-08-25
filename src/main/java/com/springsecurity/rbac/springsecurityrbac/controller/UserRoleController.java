package com.springsecurity.rbac.springsecurityrbac.controller;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.service.UserRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/userRole/")
public class UserRoleController {


    private UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/assignRole")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public UserDto assignRole(@RequestBody AssignRole assignRole) {
        try {
            return userRoleService.assignRole(assignRole);
        } catch (RoleNotFoundException | UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/extendRole")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public UserDto extendRole(@RequestBody ExtendRole extendRole) {

        try {
            return userRoleService.extendRole(extendRole);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/revokeExtendedPrivileges")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public UserDto revokeExtendedPrivileges(@RequestBody RevokeExtendPrivilege revokeExtendPrivilege) {
        try {
            return userRoleService.revokeExtendedPrivileges(revokeExtendPrivilege);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/revokeRole")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public UserDto revokeRole(@RequestBody RevokeRole revokeRole) {
        try {
            return userRoleService.revokeRole(revokeRole);
        } catch (RoleNotFoundException | UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
