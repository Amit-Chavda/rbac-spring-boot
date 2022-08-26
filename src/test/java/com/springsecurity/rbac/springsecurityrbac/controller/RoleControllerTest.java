package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springsecurity.rbac.springsecurityrbac.dto.RoleDto;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;


    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private RoleDto roleDto;
    private String baserUrl;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        roleDto = new RoleDto();
        roleDto.setName("SOME ROLE NAME");
        roleDto.setPagePrivilegeMap(Collections.emptyMap());

        baserUrl = "/role";
        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }

    /**
     * Method under test: {@link RoleController#createRole(RoleDto)}
     */
    @Test
    void testCreateRole() throws Exception {
        // Arrange
        when(roleService.createRole(roleDto)).thenReturn(roleDto);

        requestBuilder = post("/role/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(roleDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).createRole(roleDto);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(roleDto)));
    }

    /**
     * Method under test: {@link RoleController#createRole(RoleDto)}
     */
    @Test
    void testCreateRole2() throws Exception {
        // Arrange
        when(roleService.createRole(roleDto)).thenThrow(new RoleAlreadyExistException("Role " + roleDto.getName() + " already exist!"));

        requestBuilder = post(baserUrl + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(roleDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).createRole(roleDto);

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("already exist"));
                });
    }

    /**
     * Method under test: {@link RoleController#findAllRoles()}
     */
    @Test
    void testFindAllRoles() throws Exception {
        // Arrange
        when(roleService.findAll()).thenReturn(List.of(roleDto));
        requestBuilder = get(baserUrl + "/findAll");

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).findAll();

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(List.of(roleDto))));
    }

    /**
     * Method under test: {@link RoleController#findRoleByName(String)}
     */
    @Test
    void testFindRoleByName() throws Exception {
        // Arrange
        String name = roleDto.getName();
        when(roleService.findByName(name)).thenReturn(roleDto);
        requestBuilder = get(baserUrl + "/findByName").param("name", name);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).findByName(name);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(roleDto)));
    }

    /**
     * Method under test: {@link RoleController#findRoleByName(String)}
     */
    @Test
    void testFindRoleByName2() throws Exception {
        // Arrange
        String name = roleDto.getName();
        when(roleService.findByName(name)).thenThrow(new RoleNotFoundException("Role with name " + name + " not found"));
        requestBuilder = get(baserUrl + "/findByName").param("name", name);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).findByName(name);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link RoleController#updateRole(RoleDto)}
     */
    @Test
    void testUpdateRole() throws Exception {
        // Arrange
        when(roleService.updateRole(roleDto)).thenReturn(roleDto);
        requestBuilder = put(baserUrl+"/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(roleDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).updateRole(roleDto);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(roleDto)));
    }

    /**
     * Method under test: {@link RoleController#updateRole(RoleDto)}
     */
    @Test
    void testUpdateRole2() throws Exception {
        // Arrange
        when(roleService.updateRole(roleDto)).thenThrow(new RoleNotFoundException("Role with name " + roleDto.getName() + " not found"));
        requestBuilder = put("/role/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(roleDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).updateRole(roleDto);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link RoleController#updateRole(RoleDto)}
     */
    @Test
    void testUpdateRole3() throws Exception {
        // Arrange
        when(roleService.updateRole(roleDto)).thenThrow(new NoSuchElementException("Page not found"));
        requestBuilder = put("/role/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(roleDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(roleService, times(1)).updateRole(roleDto);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }
}

