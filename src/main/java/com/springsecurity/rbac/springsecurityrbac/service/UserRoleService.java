package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Role;
import com.springsecurity.rbac.springsecurityrbac.entity.security.RolePagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
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
    private UserService userService;
    private RolePagesPrivilegesService rolePagesPrivilegesService;
    private PagesPrivilegesRepository pagesPrivilegesRepository;


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

            String privilegeName = pagesPrivileges1.getPage().getName();
            String pageName = pagesPrivileges1.getPrivilege().getName();

            PagesPrivileges pagesPrivileges = pagesPrivilegesRepository.findByName(privilegeName, pageName);

            rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
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
