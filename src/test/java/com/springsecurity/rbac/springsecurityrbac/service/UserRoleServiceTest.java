package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        user.setRoles(List.of(role));
    }


    /**
     * Method under test: {@link UserRoleService#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole() throws RoleNotFoundException, UsernameNotFoundException {
        // Arrange
        String roleName = "ADMIN";
        String username = "test@test.com";

        AssignRole assignRole = new AssignRole();
        assignRole.setUsername(username);
        assignRole.setRoleNames(List.of(roleName));

        UserDto userDto = UserMapper.toUserDto(user);

        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserDto actualAssignRoleResult = this.userRoleService.assignRole(assignRole);

        // Assert
        verify(roleRepository, times(1)).findByName(roleName);
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user);

        assertThat(actualAssignRoleResult)
                .isNotNull()
                .isEqualTo(userDto);
    }


    /**
     * Method under test: {@link UserRoleService#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole2() throws RoleNotFoundException, UsernameNotFoundException {
        // Arrange
        String roleName = "ADMIN";
        String username = "test@test.com";
        AssignRole assignRole = new AssignRole();
        assignRole.setUsername(username);
        assignRole.setRoleNames(List.of(roleName));

        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User with email " + assignRole.getUsername() + " does not exists!");

        when(roleRepository.findByName(roleName)).thenThrow(usernameNotFoundException);

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.assignRole(assignRole));

        // Assert
        verify(roleRepository, times(1)).findByName(roleName);
        verifyNoInteractions(userRepository);
    }

    /**
     * Method under test: {@link UserRoleService#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole() {
        // Arrange
        User expectedResult = new User();
        expectedResult.setFirstName("firstname");
        expectedResult.setLastName("lastname");
        expectedResult.setPassword("password");
        expectedResult.setEmail("test@test.com");
        expectedResult.setEnabled(true);
        expectedResult.setSpecialPrivileges(false);

        UserDto expectedUserDto = UserMapper.toUserDto(expectedResult);

        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(expectedResult);

        RevokeRole revokeRole = new RevokeRole(username, List.of(roleName));

        // Act
        UserDto actualRevokeRoleResult = this.userRoleService.revokeRole(revokeRole);

        // Assert
        verify(roleRepository, times(1)).findByName(roleName);
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user);

        assertThat(actualRevokeRoleResult)
                .isNotNull()
                .isEqualTo(expectedUserDto);
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

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.revokeRole(revokeRole));

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(roleRepository, pagesPrivilegesRepository, rolePagesPrivilegesService);


    }

    /**
     * Method under test: {@link UserRoleService#extendRole(ExtendRole)}
     */
    @Test
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

        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(username);
        extendRole.setPagesPrivilegesDtos(List.of(pagesPrivilegesDto));

        RolePagesPrivileges rolePagesPrivileges1 = new RolePagesPrivileges();
        rolePagesPrivileges1.setPagesPrivileges(pagesPrivileges1);
        rolePagesPrivileges1.setUser(user);


        User user1 = user;
        user1.setSpecialPrivileges(true);

        when(userRepository.findByEmail(username)).thenReturn(Optional.ofNullable(user));
        when(pagesPrivilegesRepository.findByName(privilegeName, pageName)).thenReturn(pagesPrivileges1);
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        ExtendRole actualExtendRoleResult = this.userRoleService.extendRole(extendRole);

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verify(pagesPrivilegesRepository, times(1)).findByName(privilegeName, pageName);
        verify(userRepository, times(1)).save(user1);

        assertThat(actualExtendRoleResult)
                .isNotNull()
                .isEqualTo(extendRole);


    }


    /**
     * Method under test: {@link UserRoleService#extendRole(ExtendRole)}
     */
    @Test
    void testExtendRole2() throws UsernameNotFoundException {
        // Arrange
        String pageName = PAGE.ROLE;
        String privilegeName = PRIVILEGE.READ;

        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(username);

        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User with email " + extendRole.getUsername() + " does not exists!");

        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.extendRole(extendRole));

        // Assert
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
        String pageName = PAGE.ROLE;
        String privilegeName = PRIVILEGE.READ;

        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(username);

        user.setRoles(null);

        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User does not have role(s) related to this privileges!");

        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.extendRole(extendRole));

        // Assert
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
        RevokeExtendPrivilege actualRevokeExtendedPrivilegesResult = this.userRoleService
                .revokeExtendedPrivileges(revokeExtendPrivilege);

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verify(userRepository, times(1)).save(user1);
        verify(rolePagesPrivilegesService, times(1)).deleteById(rolePagesPrivileges1.getId());

        assertThat(actualRevokeExtendedPrivilegesResult)
                .isNotNull()
                .isEqualTo(revokeExtendPrivilege);
    }

    /**
     * Method under test: {@link UserRoleService#revokeExtendedPrivileges(RevokeExtendPrivilege)}
     */
    @Test
    void testRevokeExtendedPrivileges2() throws UsernameNotFoundException {
        // Arrange
        String pageName = PAGE.ROLE;
        String privilegeName = PRIVILEGE.READ;


        RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege();
        revokeExtendPrivilege.setUsername(username);
        UsernameNotFoundException usernameNotFoundException = new
                UsernameNotFoundException("User with email " + revokeExtendPrivilege.getUsername() + " does not exists!");

        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act
        assertThrows(UsernameNotFoundException.class, () -> this.userRoleService.revokeExtendedPrivileges(revokeExtendPrivilege));

        // Assert
        verify(userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(rolePagesPrivilegesService, roleRepository, pagesPrivilegesRepository);

    }
}

