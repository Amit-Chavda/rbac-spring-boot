package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.RoleMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoleService {
    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private RoleRepository roleRepository;
    private PagesPrivilegesService pagesPrivilegesService;
    private RolePagesPrivilegesService rolePagesPrivilegesService;

    private PageRepository pageRepository;
    private PrivilegeRepository privilegeRepository;


    public Role findByName(String name) throws RoleNotFoundException {
        Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByName(name));
        if (optionalRole.isEmpty()) {
            throw new RoleNotFoundException(RoleAlreadyExistException.class.getName(),
                    "Role " + name + " does not exist!", LocalDateTime.now());
        }
        return optionalRole.get();
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public RoleDto createRole(RoleDto roleDto) throws RoleAlreadyExistException {

        if (roleRepository.existsByName(roleDto.getName())) {
            throw new RoleAlreadyExistException(RoleAlreadyExistException.class.getName(),
                    "Role " + roleDto.getName() + " already exist!", LocalDateTime.now());
        }

        Role role = save(new Role(roleDto.getName()));
        Collection<RolePagesPrivileges> rolePagesPrivilegesList = RoleMapper.toRole(roleDto).getRolePagesPrivileges().stream().map(rolePagesPrivileges -> {

            String pageName = rolePagesPrivileges.getPagesPrivileges().getPage().getName();
            Page page = pageRepository.findByName(pageName).orElseThrow(
                    () -> new NoSuchElementException("Page with name " + pageName + " not found!")
            );

            String privilegeName = rolePagesPrivileges.getPagesPrivileges().getPrivilege().getName();
            Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(
                    () -> new NoSuchElementException("Privilege with name " + privilegeName + " not found!")
            );

            PagesPrivileges pagesPrivileges = pagesPrivilegesService.findByName(new PagesPrivileges(page, privilege));

            rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
            rolePagesPrivileges.setRole(role);
            return rolePagesPrivilegesService.save(rolePagesPrivileges);

        }).toList();
        role.setRolePagesPrivileges(new ArrayList<>(rolePagesPrivilegesList));
        logger.info("New role with name {} is created!", role.getName());
        return RoleMapper.toRoleDto(roleRepository.save(role));
    }


    public Collection<RoleDto> findAll() {
        return RoleMapper.toRoleDtos(roleRepository.findAll());
    }

    public RoleDto updateRole(RoleDto roleDto) {
        Role role = findByName(roleDto.getName());
        role.getRolePagesPrivileges().forEach(rolePagesPrivilegesService::delete);
        roleRepository.delete(role);
        return createRole(roleDto);
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }


}
