package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PagesPrivilegesServiceTest {

    @Mock
    PagesPrivilegesRepository pagesPrivilegesRepository;
    @InjectMocks
    private PagesPrivilegesService pagesPrivilegesService;

    /**
     * Method under test: {@link PagesPrivilegesService#addOrGet(PagesPrivileges)}
     */
    @Test
    void testAddOrGet() {
        Page page = new Page(PAGE.PRODUCT);
        Privilege privilege = new Privilege(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPage(page);
        pagesPrivileges.setPrivilege(privilege);
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(true);
        when(pagesPrivilegesRepository.findByName(privilege.getName(), page.getName())).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualAddOrGetResult = this.pagesPrivilegesService.addOrGet(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).findByName(privilege.getName(), page.getName());

        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(pagesPrivileges);

        assertThat(actualAddOrGetResult.getPrivilege())
                .isNotNull()
                .isEqualTo(privilege);

        assertThat(actualAddOrGetResult.getPage())
                .isNotNull()
                .isEqualTo(page);
    }


    /**
     * Method under test: {@link PagesPrivilegesService#addOrGet(PagesPrivileges)}
     */
    @Test
    void testAddOrGet2() {
        Page page = new Page(PAGE.PRODUCT);
        Privilege privilege = new Privilege(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPage(page);
        pagesPrivileges.setPrivilege(privilege);
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(false);
        when(pagesPrivilegesRepository.save(pagesPrivileges)).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualAddOrGetResult = this.pagesPrivilegesService.addOrGet(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).save(pagesPrivileges);

        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(pagesPrivileges);

        assertThat(actualAddOrGetResult.getPrivilege())
                .isNotNull()
                .isEqualTo(privilege);

        assertThat(actualAddOrGetResult.getPage())
                .isNotNull()
                .isEqualTo(page);
    }

    /**
     * Method under test: {@link PagesPrivilegesService#findByName(PagesPrivileges)}
     */
    @Test
    void testFindByName() throws NoSuchElementException {
        // Arrange
        Page page = new Page(PAGE.PRODUCT);
        Privilege privilege = new Privilege(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPage(page);
        pagesPrivileges.setPrivilege(privilege);
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(true);
        when(pagesPrivilegesRepository.findByName(privilege.getName(), page.getName())).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualFindByNameResult = this.pagesPrivilegesService.findByName(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).findByName(privilege.getName(), page.getName());

        assertThat(actualFindByNameResult)
                .isNotNull()
                .isEqualTo(pagesPrivileges);

        assertThat(actualFindByNameResult.getPrivilege())
                .isNotNull()
                .isEqualTo(privilege);

        assertThat(actualFindByNameResult.getPage())
                .isNotNull()
                .isEqualTo(page);

    }

    /**
     * Method under test: {@link PagesPrivilegesService#findByName(PagesPrivileges)}
     */
    @Test
    void testFindByName2() throws NoSuchElementException {
        // Arrange
        Page page = new Page(PAGE.PRODUCT);
        Privilege privilege = new Privilege(PRIVILEGE.READ);

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPage(page);
        pagesPrivileges.setPrivilege(privilege);

        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(false);


        // Act // Assert
        assertThrows(NoSuchElementException.class, () -> this.pagesPrivilegesService.findByName(pagesPrivileges));

        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
    }
}

