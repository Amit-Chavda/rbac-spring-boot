package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.RoleMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RolePagesPrivilegesService rolePagesPrivilegesService;
    @Mock
    private PagesPrivilegesRepository pagesPrivilegesRepository;
    @InjectMocks
    private UserRoleService userRoleService;

    private String roleName = "ADMIN";
    private String username = "test@test.com";
    private PagesPrivileges pagesPrivileges;
    private RolePagesPrivileges rolePagesPrivileges;
    private Role role;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setup() {
        pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);

        role = new Role();
        role.setName(roleName);
        role.setId(1L);
        role.setRolePagesPrivileges(List.of(rolePagesPrivileges));

        user = new User();
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword("password");
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);
        user.setRoles(Collections.emptyList());
        user.setRolePagesPrivileges(Collections.emptyList());

        userDto = UserMapper.toUserDto(user);
        userDto.setRoles(Collections.emptyList());
    }


    /**
     * Method under test: {@link UserRoleService#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole() throws RoleNotFoundException, UsernameNotFoundException {
        // Arrange
        AssignRole assignRole = new AssignRole();
        assignRole.setUsername(username);
        assignRole.setRoleNames(List.of(roleName));

        userDto.setRoles(List.of(RoleMapper.toRoleDto(role)));

        when(roleRepository.existsByName(roleName)).thenReturn(true);
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserDto actualAssignRoleResult = this.userRoleService.assignRole(assignRole);

        // Assert
        verify(roleRepository, times(1)).findByName(roleName);
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user);

        assertThat(actualAssignRoleResult).isEqualTo(userDto);
    }


    /**
     * Method under test: {@link UserRoleService#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole2() throws RoleNotFoundException, UsernameNotFoundException {
        // Arrange
        AssignRole assignRole = new AssignRole();
        assignRole.setUsername(username);
        assignRole.setRoleNames(List.of(roleName));
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.assignRole(assignRole));

        // Assert
        verify(userRepository, times(1)).findByEmail(assignRole.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(roleRepository, rolePagesPrivilegesService, pagesPrivilegesRepository);
    }

    /**
     * Method under test: {@link UserRoleService#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole() {
        // Arrange
        RevokeRole revokeRole = new RevokeRole(username, List.of(roleName));

        UserDto expectedUserDto = UserMapper.toUserDto(user);
        expectedUserDto.setRoles(Collections.emptyList());
        when(roleRepository.existsByName(roleName)).thenReturn(true);
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);


        // Act
        UserDto actualRevokeRoleResult = this.userRoleService.revokeRole(revokeRole);

        // Assert
        verify(roleRepository, times(1)).findByName(roleName);
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, roleRepository);

        assertThat(actualRevokeRoleResult).isEqualTo(expectedUserDto);
    }

    /**
     * Method under test: {@link UserRoleService#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole2() {
        // Arrange
        RevokeRole revokeRole = new RevokeRole(username, List.of(roleName));
        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User with email " + revokeRole.getUsername() + " does not exists!");
        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.revokeRole(revokeRole));
        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(roleRepository, pagesPrivilegesRepository, rolePagesPrivilegesService);
    }

    /**
     * Method under test: {@link UserRoleService#extendRole(ExtendRole)}
     */
    @Test
    @Disabled("Need to fix")
    void testExtendRole() throws UsernameNotFoundException {
        // Arrange
        String pageName = PAGE.ROLE;
        String privilegeName = PRIVILEGE.READ;


        PagesPrivileges pagesPrivileges1 = new PagesPrivileges();
        pagesPrivileges1.setPrivilege(new Privilege(privilegeName));
        pagesPrivileges1.setPage(new Page(pageName));


        PagesPrivilegesDto pagesPrivilegesDto = new PagesPrivilegesDto();
        pagesPrivilegesDto.setPageDto(new PageDto(pageName));
        pagesPrivilegesDto.setPrivilegeDto(new PrivilegeDto(privilegeName));

        RolePagesPrivileges rolePagesPrivileges1 = new RolePagesPrivileges();
        rolePagesPrivileges1.setPagesPrivileges(pagesPrivileges1);

        pagesPrivileges1.setRolePagesPrivileges(List.of(rolePagesPrivileges1));

        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(username);
        extendRole.setPagesPrivilegesDtos(List.of(pagesPrivilegesDto));

        User user1 = user;
        user1.setSpecialPrivileges(true);
        user1.setRolePagesPrivileges(List.of(rolePagesPrivileges1));

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user1));
        when(pagesPrivilegesRepository.findByName(privilegeName, pageName)).thenReturn(pagesPrivileges1);
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        UserDto actualExtendRoleResult = this.userRoleService.extendRole(extendRole);

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
//        verify(pagesPrivilegesRepository, times(1)).findByName(privilegeName, pageName);
        verify(userRepository, times(1)).save(user1);
        assertThat(actualExtendRoleResult).isEqualTo(userDto);

    }


    /**
     * Method under test: {@link UserRoleService#extendRole(ExtendRole)}
     */
    @Test
    void testExtendRole2() throws UsernameNotFoundException {
        // Arrange
        ExtendRole extendRole = new ExtendRole(username, Collections.emptyList());
        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User with email " + extendRole.getUsername() + " not found");
        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.extendRole(extendRole));

        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(pagesPrivilegesRepository, roleRepository, rolePagesPrivilegesService);
    }

    /**
     * Method under test: {@link UserRoleService#extendRole(ExtendRole)}
     */
    @Test
    void testExtendRole3() throws UsernameNotFoundException {
        // Arrange
        ExtendRole extendRole = new ExtendRole(username, Collections.emptyList());
        user.setRoles(null);
        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User does not have role(s) related to this privileges!");
        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.extendRole(extendRole));
        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(pagesPrivilegesRepository, roleRepository, rolePagesPrivilegesService);

    }

    /**
     * Method under test: {@link UserRoleService#revokeExtendedPrivileges(RevokeExtendPrivilege)}
     */
    @Test
    void testRevokeExtendedPrivileges() throws UsernameNotFoundException {
        // Arrange
        String pageName = PAGE.ROLE;
        String privilegeName = PRIVILEGE.READ;

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPrivilege(new Privilege(privilegeName));
        pagesPrivileges.setPage(new Page(pageName));

        RolePagesPrivileges rolePagesPrivileges1 = new RolePagesPrivileges();
        rolePagesPrivileges1.setId(10L);
        rolePagesPrivileges1.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges1.setUser(user);

        user.setRolePagesPrivileges(List.of(rolePagesPrivileges1));

        User user1 = user;
        user1.setSpecialPrivileges(false);

        Map<PageDto, Collection<PrivilegeDto>> map = new HashMap<>();
        map.put(new PageDto(pageName), List.of(new PrivilegeDto(privilegeName)));


        RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege();
        revokeExtendPrivilege.setUsername(username);
        revokeExtendPrivilege.setSpecialPrivilegesMap(map);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        UserDto actualRevokeExtendedPrivilegesResult = this.userRoleService
                .revokeExtendedPrivileges(revokeExtendPrivilege);

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user1);
        verify(rolePagesPrivilegesService, times(1)).deleteById(rolePagesPrivileges1.getId());
        verifyNoMoreInteractions(userRepository, rolePagesPrivilegesService);
        assertThat(actualRevokeExtendedPrivilegesResult).isEqualTo(userDto);
    }

    /**
     * Method under test: {@link UserRoleService#revokeExtendedPrivileges(RevokeExtendPrivilege)}
     */
    @Test
    void testRevokeExtendedPrivileges2() throws UsernameNotFoundException {
        // Arrange
        RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege(username, new HashMap<>());
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.revokeExtendedPrivileges(revokeExtendPrivilege));

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(rolePagesPrivilegesService, roleRepository, pagesPrivilegesRepository);
    }
}

