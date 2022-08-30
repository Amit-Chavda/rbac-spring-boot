package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Role;
import com.springsecurity.rbac.springsecurityrbac.entity.security.RolePagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class UserRoleService {

    private Logger logger = LoggerFactory.getLogger(UserRoleService.class);
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private RolePagesPrivilegesService rolePagesPrivilegesService;
    private PagesPrivilegesRepository pagesPrivilegesRepository;

    public UserRoleService(RoleRepository roleRepository, UserRepository userRepository,
                           RolePagesPrivilegesService rolePagesPrivilegesService, PagesPrivilegesRepository pagesPrivilegesRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.rolePagesPrivilegesService = rolePagesPrivilegesService;
        this.pagesPrivilegesRepository = pagesPrivilegesRepository;
    }

    public UserDto assignRole(AssignRole assignRole) throws UsernameNotFoundException, RoleNotFoundException {

        User user = userRepository.findByEmail(assignRole.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + assignRole.getUsername() + " not found")
        );

        List<Role> roles = assignRole.getRoleNames().stream()
                .map(roleName -> {
                    if (roleRepository.existsByName(roleName)) {
                        return roleRepository.findByName(roleName);
                    }
                    throw new RoleNotFoundException("Role with name " + roleName + " not found");
                }).toList();


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
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto revokeRole(RevokeRole revokeRole) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(revokeRole.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + revokeRole.getUsername() + " not found")
        );
        List<Role> revokingRoles = revokeRole.getRoleNames().stream()
                .map(roleName -> {
                    if (roleRepository.existsByName(roleName)) {
                        return roleRepository.findByName(roleName);
                    }
                    throw new RoleNotFoundException("Role with name " + roleName + " not found");
                }).toList();

        Collection<Role> roleCollection = new ArrayList<>(user.getRoles());

        //add new role only if user doesn't have that role already
        revokingRoles.forEach(roleCollection::remove);

        user.setRoles(roleCollection);
        logger.info("Role(s) {} revoked from user {}", revokeRole.getRoleNames(), revokeRole.getUsername());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto extendRole(ExtendRole extendRole) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(extendRole.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + extendRole.getUsername() + " not found")
        );
        if (user.getRoles() == null) {
            throw new UsernameNotFoundException("User does not have role(s) related to this privileges!");
        }

        Collection<PagesPrivilegesDto> pagesPrivilegesDtos = extendRole.getPagesPrivilegesDtos();


        Collection<RolePagesPrivileges> rolePagesPrivilegesList = new ArrayList<>();
        pagesPrivilegesDtos.forEach(pagesPrivilegesDto -> {

            String privilegeName = pagesPrivilegesDto.getPrivilegeDto().getName();
            String pageName = pagesPrivilegesDto.getPageDto().getName();

            RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();

            PagesPrivileges pagesPrivileges = pagesPrivilegesRepository.findByName(privilegeName, pageName);

            rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
            rolePagesPrivilegesList.add(rolePagesPrivilegesService.addSpecialPrivileges(rolePagesPrivileges));
            rolePagesPrivileges.setUser(user);
        });

        user.setRolePagesPrivileges(rolePagesPrivilegesList);
        user.setSpecialPrivileges(true);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto revokeExtendedPrivileges(RevokeExtendPrivilege revokeExtendPrivilege) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(revokeExtendPrivilege.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + revokeExtendPrivilege.getUsername() + " not found")
        );

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
        user.setSpecialPrivileges(!newRolePagesPrivileges.isEmpty());
        User user1 = userRepository.save(user);

        //delete unused mappings
        removableRolePagesPrivileges.forEach(rolePagesPrivileges -> rolePagesPrivilegesService.deleteById(rolePagesPrivileges.getId()));
        return UserMapper.toUserDto(user1);
    }
}
