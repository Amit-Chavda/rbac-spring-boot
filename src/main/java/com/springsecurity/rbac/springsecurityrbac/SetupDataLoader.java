package com.springsecurity.rbac.springsecurityrbac;

import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.dto.RoleDto;
import com.springsecurity.rbac.springsecurityrbac.dto.UserDto;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.dto.AssignRole;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.service.PageService;
import com.springsecurity.rbac.springsecurityrbac.service.PrivilegeService;
import com.springsecurity.rbac.springsecurityrbac.service.RoleService;
import com.springsecurity.rbac.springsecurityrbac.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private PasswordEncoder passwordEncoder;

    public SetupDataLoader(PrivilegeService privilegeService, UserService userService, PageService pageService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.privilegeService = privilegeService;
        this.userService = userService;
        this.pageService = pageService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //setup default privileges
        privilegeService.save(new Privilege(PRIVILEGE.READ));
        privilegeService.save(new Privilege(PRIVILEGE.WRITE));
        privilegeService.save(new Privilege(PRIVILEGE.UPDATE));
        privilegeService.save(new Privilege(PRIVILEGE.DELETE));

        //setup default pages
        pageService.save(new Page(PAGE.USER));
        pageService.save(new Page(PAGE.ROLE));
        pageService.save(new Page(PAGE.PRODUCT));


        //prepare all privileges for root user
        HashMap<PageDto, Collection<PrivilegeDto>> adminRole = new HashMap<>();

        Collection<PrivilegeDto> prList = PrivilegeMapper.toPrivilegeDtos(privilegeService.findAll());

        Collection<PageDto> pageList = PageMapper.toPageDtos(pageService.findAll());

        for (PageDto pageDto : pageList) {
            adminRole.put(pageDto, prList);
        }

        RoleDto adminRoleDto = new RoleDto("ADMIN", adminRole);

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
            roleService.assignRole(assignRole);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}

