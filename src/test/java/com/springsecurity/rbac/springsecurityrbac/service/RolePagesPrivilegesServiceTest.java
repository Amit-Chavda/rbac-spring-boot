package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.repository.RolePagesPrivilegesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RolePagesPrivilegesServiceTest {

    @Mock
    private RolePagesPrivilegesRepository rolePagesPrivilegesRepository;

    @InjectMocks
    private RolePagesPrivilegesService rolePagesPrivilegesService;

    /**
     * Method under test: {@link RolePagesPrivilegesService#addOrGet(RolePagesPrivileges)}
     */
    @Test
    void testAddOrGet() {
        // Arrange

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        Role role = new Role();
        role.setId(1L);
        role.setCreatedAt(LocalDateTime.now());
        role.setName("ADMIN");

        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);
        when(rolePagesPrivilegesRepository.existById(role.getId(), pagesPrivileges.getId())).thenReturn(true);
        when(rolePagesPrivilegesRepository.findById(role.getId(), pagesPrivileges.getId())).thenReturn(rolePagesPrivileges);

        // Act
        RolePagesPrivileges actualAddOrGetResult = this.rolePagesPrivilegesService.addOrGet(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).existById(role.getId(), pagesPrivileges.getId());
        verify(rolePagesPrivilegesRepository, times(1)).findById(role.getId(), pagesPrivileges.getId());
        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(rolePagesPrivileges);
    }


    /**
     * Method under test: {@link RolePagesPrivilegesService#addOrGet(RolePagesPrivileges)}
     */
    @Test
    void testAddOrGet2() {
        // Arrange
        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        Role role = new Role();
        role.setId(1L);
        role.setCreatedAt(LocalDateTime.now());
        role.setName("ADMIN");

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
        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(rolePagesPrivileges);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#addSpecialPrivileges(RolePagesPrivileges)}
     */
    @Test
    void testAddSpecialPrivileges() {
        // Arrange
        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        User user = new User();
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword("password");
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);


        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setUser(user);
        when(rolePagesPrivilegesRepository.save(rolePagesPrivileges)).thenReturn(rolePagesPrivileges);


        // Act
        RolePagesPrivileges actualAddSpecialPrivilegesResult = rolePagesPrivilegesService.addSpecialPrivileges(rolePagesPrivileges);

        // Assert
        verify(rolePagesPrivilegesRepository, times(1)).save(rolePagesPrivileges);
        assertThat(actualAddSpecialPrivilegesResult)
                .isNotNull()
                .isEqualTo(rolePagesPrivileges);
    }

    /**
     * Method under test: {@link RolePagesPrivilegesService#deleteByRoleId(RolePagesPrivileges)}
     */
    @Test
    void testDelete() {
        // Arrange
        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(new Page(PAGE.USER));
        pagesPrivileges.setPrivilege(new Privilege(PRIVILEGE.READ));

        Role role = new Role();
        role.setId(1L);
        role.setCreatedAt(LocalDateTime.now());
        role.setName("ADMIN");

        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();
        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);

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

