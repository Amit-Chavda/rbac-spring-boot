package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PagesPrivilegesServiceTest {

    @Mock
    PagesPrivilegesRepository pagesPrivilegesRepository;
    @InjectMocks
    private PagesPrivilegesService pagesPrivilegesService;

    private PagesPrivileges pagesPrivileges;
    private Page page;
    private Privilege privilege;

    @BeforeEach
    void setup() {
        page = new Page(PAGE.PRODUCT);
        privilege = new Privilege(PRIVILEGE.READ);

        pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPage(page);
        pagesPrivileges.setPrivilege(privilege);

    }


    /**
     * Method under test: {@link PagesPrivilegesService#addOrGet(PagesPrivileges)}
     */
    @Test
    void testAddOrGet() {
        //Arrange
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(true);
        when(pagesPrivilegesRepository.findByName(privilege.getName(), page.getName())).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualAddOrGetResult = pagesPrivilegesService.addOrGet(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).findByName(privilege.getName(), page.getName());

        assertEquals(actualAddOrGetResult, pagesPrivileges);
    }


    /**
     * Method under test: {@link PagesPrivilegesService#addOrGet(PagesPrivileges)}
     */
    @Test
    void testAddOrGet2() {
        //Arrange
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(false);
        when(pagesPrivilegesRepository.save(pagesPrivileges)).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualAddOrGetResult = this.pagesPrivilegesService.addOrGet(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).save(pagesPrivileges);

        assertEquals(actualAddOrGetResult, pagesPrivileges);
    }

    /**
     * Method under test: {@link PagesPrivilegesService#findByName(PagesPrivileges)}
     */
    @Test
    void testFindByName() throws NoSuchElementException {
        // Arrange
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(true);
        when(pagesPrivilegesRepository.findByName(privilege.getName(), page.getName())).thenReturn(pagesPrivileges);

        // Act
        PagesPrivileges actualFindByNameResult = this.pagesPrivilegesService.findByName(pagesPrivileges);

        // Assert
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
        verify(pagesPrivilegesRepository, times(1)).findByName(privilege.getName(), page.getName());

        assertEquals(actualFindByNameResult, pagesPrivileges);
    }

    /**
     * Method under test: {@link PagesPrivilegesService#findByName(PagesPrivileges)}
     */
    @Test
    void testFindByName2() throws NoSuchElementException {
        // Arrange
        when(pagesPrivilegesRepository.existsByName(privilege.getName(), page.getName())).thenReturn(false);


        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.pagesPrivilegesService.findByName(pagesPrivileges));
        verify(pagesPrivilegesRepository, times(1)).existsByName(privilege.getName(), page.getName());
    }
}

