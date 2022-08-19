package com.springsecurity.rbac.springsecurityrbac;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(SetupDataLoader.class);
    private PrivilegeService privilegeService;

    private UserService userService;
    private PageService pageService;

    private RoleService roleService;

    private UserRoleService userRoleService;

    public SetupDataLoader(PrivilegeService privilegeService, UserService userService,
                           PageService pageService, RoleService roleService,
                           UserRoleService userRoleService) {
        this.privilegeService = privilegeService;
        this.userService = userService;
        this.pageService = pageService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //setup default privileges
        privilegeService.addOrGet(new PrivilegeDto(PRIVILEGE.READ));
        privilegeService.addOrGet(new PrivilegeDto(PRIVILEGE.WRITE));
        privilegeService.addOrGet(new PrivilegeDto(PRIVILEGE.UPDATE));
        privilegeService.addOrGet(new PrivilegeDto(PRIVILEGE.DELETE));

        //setup default pages
        pageService.addOrGet(new PageDto(PAGE.USER));
        pageService.addOrGet(new PageDto(PAGE.ROLE));
        pageService.addOrGet(new PageDto(PAGE.PRODUCT));
        pageService.addOrGet(new PageDto(PAGE.USER_ROLE));

        //prepare all privileges for root user
        HashMap<PageDto, Collection<PrivilegeDto>> adminRole = new HashMap<>();

        Collection<PrivilegeDto> prList = privilegeService.findAll();

        Collection<PageDto> pageList = pageService.findAll();

        for (PageDto pageDto : pageList) {
            adminRole.put(pageDto, prList);
        }

        RoleDto adminRoleDto = new RoleDto();
        adminRoleDto.setName("ADMIN");
        adminRoleDto.setPagePrivilegeMap(adminRole);

        try {
            roleService.createRole(adminRoleDto);
        } catch (Exception e) {
            logger.error(e.toString());
        }


        UserDto admin = new UserDto();

        //setup default root admin
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword("admin");
        admin.setEmail("admin@test.com");
        admin.setRoles(List.of(adminRoleDto));
        admin.setEnabled(true);


        try {
            userService.createUser(admin);
        } catch (Exception e) {
            logger.error(e.toString());
        }

        AssignRole assignRole = new AssignRole();
        assignRole.setUsername(admin.getEmail());
        assignRole.setRoleNames(new ArrayList<>(Collections.singleton(adminRoleDto.getName())));

        try {
            userRoleService.assignRole(assignRole);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}

