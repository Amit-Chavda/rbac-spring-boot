package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.dto.RoleDto;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import com.springsecurity.rbac.springsecurityrbac.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private PagesPrivilegesService pagesPrivilegesService;

    @Mock
    private RolePagesPrivilegesService rolePagesPrivilegesService;

    @InjectMocks
    private RoleService roleService;


    private Role role;
    private RoleDto roleDto;
    private String roleName;

    @BeforeEach
    void setup() {
        roleName = "ADMIN";
        roleDto = new RoleDto();
        roleDto.setName(roleName);
        roleDto.setPagePrivilegeMap(Collections.emptyMap());

        role = new Role();
        role.setId(10L);
        role.setName(roleName);
        role.setCreatedAt(LocalDateTime.now());
        role.setRolePagesPrivileges(Collections.emptyList());
    }


    /**
     * Method under test: {@link RoleService#findByName(String)}
     */
    @Test
    void testFindByName() throws RoleNotFoundException {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(true);
        when(roleRepository.findByName(roleName)).thenReturn(role);

        // Act
        RoleDto actualFindByNameResult = this.roleService.findByName(roleName);

        // Assert
        verify(roleRepository, times(1)).existsByName(roleName);
        verify(roleRepository, times(1)).findByName(roleName);
        assertThat(actualFindByNameResult).isEqualTo(roleDto);
    }


    /**
     * Method under test: {@link RoleService#findByName(String)}
     */
    @Test
    void testFindByName2() throws RoleNotFoundException {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(false);

        // Act
        assertThrows(RoleNotFoundException.class, () -> roleService.findByName(roleName));

        // Assert
        verify(roleRepository, times(1)).existsByName(roleName);
    }

    /**
     * Method under test: {@link RoleService#createRole(RoleDto)}
     */
    @Test
    void testCreateRole() throws RoleAlreadyExistException, NoSuchElementException {
        // Arrange
        PageDto pageDto = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(pageDto);
        page.setId(1L);

        PrivilegeDto privilegeDto = new PrivilegeDto(PRIVILEGE.READ);
        Privilege privilege = PrivilegeMapper.toPrivilege(privilegeDto);
        privilege.setId(1L);

        Map<PageDto, Collection<PrivilegeDto>> map = new HashMap<>();
        map.put(pageDto, List.of(privilegeDto));

        roleDto.setPagePrivilegeMap(map);

        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPrivilege(privilege);
        pagesPrivileges.setPage(page);
        pagesPrivileges.setId(1L);
        pagesPrivileges.setRolePagesPrivileges(List.of(rolePagesPrivileges));

        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);
        rolePagesPrivileges.setId(1L);


        role.setRolePagesPrivileges(List.of(rolePagesPrivileges));

        when(pageRepository.findByName(PAGE.PRODUCT)).thenReturn(Optional.of(page));
        when(privilegeRepository.findByName(anyString())).thenReturn(Optional.of(privilege));

        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(rolePagesPrivilegesService.addOrGet(any())).thenReturn(rolePagesPrivileges);

        when(roleRepository.save(new Role(role.getName()))).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        // Act
        RoleDto actualCreateRoleResult = roleService.createRole(roleDto);

        // Assert
        verify(privilegeRepository, times(1)).findByName(any());
        verify(pageRepository, times(1)).findByName(any());
        verify(roleRepository, times(2)).save(any());
        verify(rolePagesPrivilegesService, times(1)).addOrGet(any());
        verify(pagesPrivilegesService, times(1)).addOrGet(any());
        verifyNoMoreInteractions(privilegeRepository, pageRepository, rolePagesPrivilegesService, pagesPrivilegesService);

        assertAll(
                () -> assertEquals(actualCreateRoleResult.getName(), roleDto.getName()),
                () -> assertEquals(actualCreateRoleResult.getPagePrivilegeMap(), roleDto.getPagePrivilegeMap())
        );

    }

    /**
     * Method under test: {@link RoleService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        PageDto pageDto = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(pageDto);
        page.setId(1L);

        PrivilegeDto privilegeDto = new PrivilegeDto(PRIVILEGE.READ);
        Privilege privilege = PrivilegeMapper.toPrivilege(privilegeDto);
        privilege.setId(1L);

        Map<PageDto, Collection<PrivilegeDto>> map = new HashMap<>();
        map.put(pageDto, List.of(privilegeDto));

        roleDto.setPagePrivilegeMap(map);

        RolePagesPrivileges rolePagesPrivileges = new RolePagesPrivileges();

        PagesPrivileges pagesPrivileges = new PagesPrivileges();
        pagesPrivileges.setPrivilege(privilege);
        pagesPrivileges.setPage(page);
        pagesPrivileges.setId(1L);
        pagesPrivileges.setRolePagesPrivileges(List.of(rolePagesPrivileges));

        rolePagesPrivileges.setPagesPrivileges(pagesPrivileges);
        rolePagesPrivileges.setRole(role);
        rolePagesPrivileges.setId(1L);

        role.setRolePagesPrivileges(List.of(rolePagesPrivileges));

        when(roleRepository.findAll()).thenReturn(List.of(role));
        // Act
        Collection<RoleDto> actualFindAllResult = this.roleService.findAll();

        // Assert
        verify(roleRepository, times(1)).findAll();
        assertThat(actualFindAllResult).isEqualTo(List.of(roleDto));
    }

    /**
     * Method under test: {@link RoleService#updateRole(RoleDto)}
     */
    @Test
    @Disabled(value = "Need proper implementation in original method!")
    void testUpdateRole() {
        // Arrange
        // TODO: Populate arranged inputs
        RoleDto roleDto = null;

        // Act
        RoleDto actualUpdateRoleResult = this.roleService.updateRole(roleDto);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link RoleService#delete(RoleDto)}
     */
    @Test
    void testDelete() {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(true);
        when(roleRepository.findByName(roleName)).thenReturn(role);

        // Act
        this.roleService.delete(roleDto);

        // Assert
        verify(roleRepository, times(1)).existsByName(roleName);
        verify(roleRepository, times(1)).delete(role);
        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void testDelete2() {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(false);

        // Act and Assert
        assertThrows(RoleNotFoundException.class, () -> this.roleService.delete(roleDto));
        verify(roleRepository, times(1)).existsByName(roleName);
        verifyNoMoreInteractions(roleRepository);
    }
}

