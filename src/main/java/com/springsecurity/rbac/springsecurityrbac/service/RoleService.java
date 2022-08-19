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

    private final RoleRepository roleRepository;

    private final UserService userService;

    private final PagesPrivilegesService pagesPrivilegesService;

    private final RolePagesPrivilegesService rolePagesPrivilegesService;

    private PageRepository pageRepository;
    private PrivilegeRepository privilegeRepository;

    public RoleService(RoleRepository roleRepository, UserService userService, PagesPrivilegesService pagesPrivilegesService,
                       RolePagesPrivilegesService rolePagesPrivilegesService, PageRepository pageRepository,
                       PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.userService = userService;
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

    public UserDto assignRole(AssignRole assignRole) throws UsernameNotFoundException, RoleNotFoundException {

        List<Role> roles = assignRole.getRoleNames().stream().map(roleRepository::findByName).toList();

        User user = userService.findByEmail(assignRole.getUsername());
        Collection<Role> roleCollection = new ArrayList<>();

        //check if user has other roles
        if (user.getRoles() != null) {
            roleCollection.addAll(user.getRoles());
            roles.forEach(newRole -> {
                if (!roleCollection.contains(newRole)) {
                    roleCollection.add(newRole);
                }
            });
        } else {
            roleCollection.addAll(roles);
        }
        user.setRoles(roleCollection);
        logger.info("New role(s) {} assigned to user {}", assignRole.getRoleNames(), assignRole.getUsername());
        return UserMapper.toUserDto(userService.save(user));
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

    public UserDto revokeRole(RevokeRole revokeRole) {
        List<Role> revokingRoles = revokeRole.getRoleNames().stream().map(roleRepository::findByName).toList();

        User user = userService.findByEmail(revokeRole.getUsername());
        Collection<Role> roleCollection = new ArrayList<>(user.getRoles());

        //add new role only if user doesn't have that role already
        revokingRoles.forEach(existingRole -> {
            if (roleCollection.contains(existingRole)) {
                roleCollection.remove(existingRole);
            }
        });

        user.setRoles(roleCollection);
        logger.info("Role(s) {} revoked from user {}", revokeRole.getRoleNames(), revokeRole.getUsername());
        return UserMapper.toUserDto(userService.save(user));
    }

    public ExtendRole extendRole(ExtendRole extendRole) throws UsernameNotFoundException {

        User user = userService.findByEmail(extendRole.getUsername());
        if (user.getRoles() == null) {
            throw new UsernameNotFoundException("User does not have role(s) related to this privileges!");
        }

        Collection<PagesPrivilegesDto> pagesPrivilegesDtos = extendRole.getPagesPrivilegesDtos();


        Collection<RolePagesPrivileges> rolePagesPrivilegesList = new ArrayList<>();
        pagesPrivilegesDtos.forEach(pagesPrivilegesDto -> {

            PagesPrivileges pagesPrivileges1 = new PagesPrivileges(
                    PageMapper.toPage(pagesPrivilegesDto.getPageDto()),
                    PrivilegeMapper.toPrivilege(pagesPrivilegesDto.getPrivilegeDto())
            );

            RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();
            rolePagesPrivileges.setPagesPrivileges(pagesPrivilegesService.findByName(pagesPrivileges1));
            rolePagesPrivilegesList.add(rolePagesPrivilegesService.saveDirect(rolePagesPrivileges));
            rolePagesPrivileges.setUser(user);
        });

        user.setRolePagesPrivileges(rolePagesPrivilegesList);
        user.setSpecialPrivileges(true);
        userService.save(user);
        return extendRole;
    }

    public RevokeExtendPrivilege revokeExtendedPrivileges(RevokeExtendPrivilege revokeExtendPrivilege) throws UsernameNotFoundException {
        User user = userService.findByEmail(revokeExtendPrivilege.getUsername());

        Collection<RolePagesPrivileges> newRolePagesPrivileges = new ArrayList<>();
        Collection<RolePagesPrivileges> removableRolePagesPrivileges = new ArrayList<>();

        user.getRolePagesPrivileges()
                .forEach(rolePagesPrivileges -> {
                    String pageName = rolePagesPrivileges.getPagesPrivileges().getPage().getName();
                    String privilegeName = rolePagesPrivileges.getPagesPrivileges().getPrivilege().getName();

                    revokeExtendPrivilege.getSpecialPrivilegesMap()
                            .forEach((key, value) -> {
                                if (!key.getName().equals(pageName)) {
                                    value.forEach(privilegeDto -> {
                                        if (!privilegeDto.getName().equals(privilegeName)) {
                                            //collect new mappings
                                            newRolePagesPrivileges.add(rolePagesPrivileges);
                                        }
                                    });
                                } else {
                                    //collect unused mappings
                                    rolePagesPrivileges.setPagesPrivileges(null);
                                    removableRolePagesPrivileges.add(rolePagesPrivileges);
                                }
                            });

                });

        user.setRolePagesPrivileges(newRolePagesPrivileges);
        user.setSpecialPrivileges(true);
        userService.save(user);

        //delete unused mappings
        removableRolePagesPrivileges.forEach(rolePagesPrivileges -> rolePagesPrivilegesService.deleteById(rolePagesPrivileges.getId()));
        return revokeExtendPrivilege;
    }
}
