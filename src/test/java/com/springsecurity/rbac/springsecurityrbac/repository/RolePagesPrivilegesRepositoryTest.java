package com.springsecurity.rbac.springsecurityrbac.repository;

import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RolePagesPrivilegesRepositoryTest {

    @Mock
    private RolePagesPrivilegesRepository repository;

    @Test
    void existByIdTest1() {
        //Arrange
        long roleId = 1L;
        long pagesPrivilegesId = 1L;
        boolean expected = true;
        when(repository.existById(roleId, pagesPrivilegesId)).thenReturn(expected);

        //Act
        boolean actual = repository.existById(roleId, pagesPrivilegesId);

        //Assert
        assertEquals(actual, expected);
    }

    @Test
    void existByIdTest2() {
        //Arrange
        long roleId = 2L;
        long pagesPrivilegesId = 1L;
        boolean expected = false;
        when(repository.existById(roleId, pagesPrivilegesId)).thenReturn(expected);

        //Act
        boolean actual = repository.existById(roleId, pagesPrivilegesId);

        //Assert
        assertEquals(actual, expected);
    }

    @Test
    void findByIdTest1() {

        //Arrange
        Page rolePage = new Page();
        rolePage.setId(1L);
        rolePage.setName(PAGE.PRODUCT);


        Privilege readPrivilege = new Privilege();
        readPrivilege.setId(1L);
        readPrivilege.setName(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(rolePage);
        pagesPrivileges.setPrivilege(readPrivilege);

        Role role = new Role();
        role.setCreatedAt(LocalDateTime.now());
        role.setId(1L);
        role.setName("ADMIN");

        RolePagesPrivileges expectedRolePagesPrivileges = new RolePagesPrivileges();

        expectedRolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        expectedRolePagesPrivileges.setRole(role);

        long roleId = 1L;
        long pagesPrivilegesId = 1L;
        when(repository.findById(roleId, pagesPrivilegesId)).thenReturn(expectedRolePagesPrivileges);

        //Act
        RolePagesPrivileges actualRolePagesPrivileges = repository.findById(roleId, pagesPrivilegesId);

        //Assert
        assertEquals(actualRolePagesPrivileges, expectedRolePagesPrivileges);
        assertEquals(actualRolePagesPrivileges.getPagesPrivileges(), pagesPrivileges);
        assertEquals(actualRolePagesPrivileges.getPagesPrivileges().getPrivilege(), readPrivilege);
        assertEquals(actualRolePagesPrivileges.getPagesPrivileges().getPage(), rolePage);
        assertEquals(actualRolePagesPrivileges.getRole(), role);

    }
    @Test
    void findByIdTest2() {

        //Arrange
        Page rolePage = new Page();
        rolePage.setId(1L);
        rolePage.setName(PAGE.PRODUCT);


        Privilege readPrivilege = new Privilege();
        readPrivilege.setId(1L);
        readPrivilege.setName(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setId(1L);
        pagesPrivileges.setPage(rolePage);
        pagesPrivileges.setPrivilege(readPrivilege);

        Role role = new Role();
        role.setCreatedAt(LocalDateTime.now());
        role.setId(1L);
        role.setName("ADMIN");

        RolePagesPrivileges expectedRolePagesPrivileges = new RolePagesPrivileges();

        expectedRolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        expectedRolePagesPrivileges.setRole(role);

        long roleId = 1L;
        long pagesPrivilegesId = 1L;
//        when(repository.findById(roleId, pagesPrivilegesId)).thenReturn(expectedRolePagesPrivileges);

        //Act
        RolePagesPrivileges actualRolePagesPrivileges = repository.findById(roleId, 2L);

        //Assert
        assertNotEquals(actualRolePagesPrivileges, expectedRolePagesPrivileges);

    }
}
