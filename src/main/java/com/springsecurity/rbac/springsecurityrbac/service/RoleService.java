package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.RoleDto;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.RoleMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private RoleRepository roleRepository;
    private PagesPrivilegesService pagesPrivilegesService;
    private RolePagesPrivilegesService rolePagesPrivilegesService;

    private PageRepository pageRepository;
    private PrivilegeRepository privilegeRepository;

    public RoleService(RoleRepository roleRepository, PagesPrivilegesService pagesPrivilegesService,
                       RolePagesPrivilegesService rolePagesPrivilegesService,
                       PageRepository pageRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.pagesPrivilegesService = pagesPrivilegesService;
        this.rolePagesPrivilegesService = rolePagesPrivilegesService;
        this.pageRepository = pageRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public Role findByName(String name) throws RoleNotFoundException {
        Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByName(name));
        if (optionalRole.isEmpty()) {
            throw new RoleNotFoundException(RoleAlreadyExistException.class.getName(),
                    "Role " + name + " does not exist!", LocalDateTime.now());
        }
        return optionalRole.get();
    }

    public RoleDto createRole(RoleDto roleDto) throws RoleAlreadyExistException, NoSuchElementException {

        if (roleRepository.existsByName(roleDto.getName())) {
            throw new RoleAlreadyExistException(RoleAlreadyExistException.class.getName(),
                    "Role " + roleDto.getName() + " already exist!", LocalDateTime.now());
        }

        Role role = roleRepository.save(new Role(roleDto.getName()));
        role.setCreatedAt(LocalDateTime.now());

        Collection<RolePagesPrivileges> rolePagesPrivilegesList =
                RoleMapper.toRole(roleDto).getRolePagesPrivileges().stream()
                        .map(rolePagesPrivileges -> {
                            //get page from database
                            String pageName = rolePagesPrivileges.getPagesPrivileges().getPage().getName();
                            Page page = pageRepository.findByName(pageName).orElseThrow(
                                    () -> new NoSuchElementException("Page with name " + pageName + " not found!")
                            );

                            //get privilege from database
                            String privilegeName = rolePagesPrivileges.getPagesPrivileges().getPrivilege().getName();
                            Privilege privilege = privilegeRepository.findByName(privilegeName).orElseThrow(
                                    () -> new NoSuchElementException("Privilege with name " + privilegeName + " not found!")
                            );

                            //create or get pages privileges mapping
                            PagesPrivileges pagesPrivileges = pagesPrivilegesService.addOrGet(new PagesPrivileges(page, privilege));

                            //set role
                            rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
                            rolePagesPrivileges.setRole(role);
                            return rolePagesPrivilegesService.addOrGet(rolePagesPrivileges);
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
        role.getRolePagesPrivileges().forEach(rolePagesPrivilegesService::deleteByRoleId);
        roleRepository.delete(role);
        return createRole(roleDto);
    }

    public void delete(Role role) throws RoleNotFoundException {
        Role role1 = findByName(role.getName());
        roleRepository.delete(role1);
    }


}
