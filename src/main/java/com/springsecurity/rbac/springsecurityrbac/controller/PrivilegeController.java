package com.springsecurity.rbac.springsecurityrbac.controller;

import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.service.PrivilegeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/privilege")
public class PrivilegeController {
    private PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @GetMapping("/findAll")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public Collection<PrivilegeDto> findAllPrivileges() {
        return privilegeService.findAll();
    }

    @GetMapping("/findByName")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PrivilegeDto findPrivilegeByName(@RequestParam String name) {
        try {
            return privilegeService.findByName(name);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PrivilegeDto createPrivilege(@RequestBody PrivilegeDto privilegeDto) {
        return privilegeService.add(privilegeDto);
    }

    @DeleteMapping("/remove")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PrivilegeDto removePrivilege(@RequestBody PrivilegeDto privilegeDto) {
        try {
            return privilegeService.remove(privilegeDto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
