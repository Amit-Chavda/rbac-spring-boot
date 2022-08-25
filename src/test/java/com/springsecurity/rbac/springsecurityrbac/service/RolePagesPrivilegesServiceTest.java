package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.repository.RolePagesPrivilegesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RolePagesPrivilegesServiceTest {

    @Mock
    private RolePagesPrivilegesRepository rolePagesPrivilegesRepository;

    @InjectMocks
    private RolePagesPrivilegesService rolePagesPrivilegesService;


    private PagesPrivileges pagesPrivileges;
    private Role role;
    private RolePagesPrivileges rolePagesPrivileges;

    @BeforeEach
    void setup() {
        pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        role = new Role();
        role.setId(1L);
        role.setCreatedAt(LocalDateTime.now());
        role.setName("ADMIN");

        rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#addOrGet(RolePagesPrivileges)}
     */
    @Test
    void testAddOrGet() {
        // Arrange
        when(rolePagesPrivilegesRepository.existById(role.getId(), pagesPrivileges.getId())).thenReturn(true);
        when(rolePagesPrivilegesRepository.findById(role.getId(), pagesPrivileges.getId())).thenReturn(rolePagesPrivileges);

        // Act
        RolePagesPrivileges actualAddOrGetResult = this.rolePagesPrivilegesService.addOrGet(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).existById(role.getId(), pagesPrivileges.getId());
        verify(rolePagesPrivilegesRepository, times(1)).findById(role.getId(), pagesPrivileges.getId());
        assertEquals(actualAddOrGetResult, rolePagesPrivileges);
    }


    /**
     * Method under test: {@link RolePagesPrivilegesService#addOrGet(RolePagesPrivileges)}
     */
    @Test
    void testAddOrGet2() {
        // Arrange
        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);
        when(rolePagesPrivilegesRepository.existById(role.getId(), pagesPrivileges.getId())).thenReturn(false);
        when(rolePagesPrivilegesRepository.save(rolePagesPrivileges)).thenReturn(rolePagesPrivileges);

        // Act
        RolePagesPrivileges actualAddOrGetResult = this.rolePagesPrivilegesService.addOrGet(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).existById(role.getId(), pagesPrivileges.getId());
        verify(rolePagesPrivilegesRepository, times(1)).save(rolePagesPrivileges);
        assertThat(actualAddOrGetResult).isEqualTo(rolePagesPrivileges);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#addSpecialPrivileges(RolePagesPrivileges)}
     */
    @Test
    void testAddSpecialPrivileges() {
        // Arrange
        User user = new User();
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword("password");
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);
        rolePagesPrivileges.setUser(user);

        when(rolePagesPrivilegesRepository.save(rolePagesPrivileges)).thenReturn(rolePagesPrivileges);

        // Act
        RolePagesPrivileges actualAddSpecialPrivilegesResult = rolePagesPrivilegesService.addSpecialPrivileges(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).save(rolePagesPrivileges);
        assertThat(actualAddSpecialPrivilegesResult).isEqualTo(rolePagesPrivileges);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#deleteByRoleId(RolePagesPrivileges)}
     */
    @Test
    void testDelete() {
        // Arrange
        when(rolePagesPrivilegesRepository.existById(role.getId(), pagesPrivileges.getId())).thenReturn(true);
        when(rolePagesPrivilegesRepository.findById(role.getId(), pagesPrivileges.getId())).thenReturn(rolePagesPrivileges);

        // Act
        this.rolePagesPrivilegesService.deleteByRoleId(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).existById(role.getId(), pagesPrivileges.getId());
        verify(rolePagesPrivilegesRepository, times(1)).findById(role.getId(), pagesPrivileges.getId());
        verify(rolePagesPrivilegesRepository, times(1)).delete(rolePagesPrivileges);
    }


    /**
     * Method under test: {@link RolePagesPrivilegesService#deleteByRoleId(RolePagesPrivileges)}
     */
    @Test
    void testDelete2() {
        // Arrange
        when(rolePagesPrivilegesRepository.existById(role.getId(), pagesPrivileges.getId())).thenReturn(false);

        // Act
        this.rolePagesPrivilegesService.deleteByRoleId(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).existById(role.getId(), pagesPrivileges.getId());
        verifyNoMoreInteractions(rolePagesPrivilegesRepository);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#deleteById(long)}
     */
    @Test
    void testDeleteById() {
        // Arrange
        long id = 10L;

        // Act
        this.rolePagesPrivilegesService.deleteById(id);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).deleteById(id);
    }
}

