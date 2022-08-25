package com.springsecurity.rbac.springsecurityrbac.repository;

import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PagesPrivilegesTest {


    @Mock
    private PagesPrivilegesRepository repository;

    String rolePage;
    String productPage;
    String userPage;

    String writePrivilege;
    String readPrivilege;
    String updatePrivilege;
    String deletePrivilege;

    @BeforeEach
    void setup() {
        rolePage = "ROLE";
        productPage = "PRODUCT";
        userPage = "USER";

        writePrivilege = "WRITE";
        readPrivilege = "READ";
        updatePrivilege = "UPDATE";
        deletePrivilege = "DELETE";
    }

    @Test
    void existsByNameTest() {
        //Arrange
        boolean expected = true;


        when(repository.existsByName(readPrivilege, userPage)).thenReturn(expected);


        //Act
        boolean actual = repository.existsByName(readPrivilege, userPage);

        //Assert
        Assertions.assertEquals(expected, actual);

    }

    @Test
    void existsByNameTest2() {
        //Arrange
        boolean expected = false;
        when(repository.existsByName(deletePrivilege, userPage)).thenReturn(expected);


        //Act
        boolean actual = repository.existsByName(deletePrivilege, userPage);

        //Assert
        Assertions.assertEquals(expected, actual);

    }

    @Test
    void findByNameTest() {
        //Arrange
        Page rolePage = new Page(this.rolePage);
        Privilege readPrivilege = new Privilege(this.readPrivilege);
        PagesPrivileges expected = new PagesPrivileges();
        expected.setPage(rolePage);
        expected.setPrivilege(readPrivilege);

        when(repository.findByName(this.readPrivilege, this.rolePage)).thenReturn(expected);

        //Act
        PagesPrivileges actual = repository.findByName(this.readPrivilege, this.rolePage);

        //Assert
        assertEquals(actual.getPage().getName(), expected.getPage().getName());
        assertEquals(actual.getPrivilege().getName(), expected.getPrivilege().getName());

        assertEquals(actual, expected);

    }
}
