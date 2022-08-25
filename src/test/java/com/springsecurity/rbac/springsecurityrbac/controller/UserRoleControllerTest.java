package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.exception.RoleNotFoundException;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserRoleControllerTest {
    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserRoleController userRoleController;
    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;
    private UserDto userDto;
    private String baserUrl;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        userDto = new UserDto();
        userDto.setFirstName("tony");
        userDto.setLastName("stark");
        userDto.setEnabled(true);
        userDto.setPassword("12345");
        userDto.setEmail("tony@test.com");
        userDto.setRoles(Collections.emptyList());
        userDto.setSpecialPagesPrivileges(Collections.emptyList());

        baserUrl = "/userRole";
        mockMvc = MockMvcBuilders.standaloneSetup(userRoleController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }


    /**
     * Method under test: {@link UserRoleController#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole() throws Exception {
        // Arrange
        AssignRole assignRole = new AssignRole();
        assignRole.setRoleNames(Collections.emptyList());
        assignRole.setUsername(userDto.getEmail());

        when(userRoleService.assignRole(assignRole)).thenReturn(userDto);
        requestBuilder = post(baserUrl + "/assignRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(assignRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).assignRole(assignRole);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));
    }

    /**
     * Method under test: {@link UserRoleController#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole2() throws Exception {
        // Arrange
        AssignRole assignRole = new AssignRole();
        assignRole.setRoleNames(Collections.emptyList());
        assignRole.setUsername(userDto.getEmail());

        when(userRoleService.assignRole(assignRole)).thenThrow(new UsernameNotFoundException("User with email " + assignRole.getUsername() + " not found"));
        requestBuilder = post(baserUrl + "/assignRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(assignRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).assignRole(assignRole);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link UserRoleController#assignRole(AssignRole)}
     */
    @Test
    void testAssignRole3() throws Exception {
        // Arrange
        String roleName = "SOME ROLE";
        AssignRole assignRole = new AssignRole();
        assignRole.setRoleNames(List.of(roleName));
        assignRole.setUsername(userDto.getEmail());

        when(userRoleService.assignRole(assignRole)).thenThrow(new RoleNotFoundException("Role with name " + roleName + " not found"));
        requestBuilder = post(baserUrl + "/assignRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(assignRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).assignRole(assignRole);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link UserRoleController#extendRole(ExtendRole)}
     */
    @Test
    void testExtendRole() throws Exception {
        // Arrange
        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(userDto.getEmail());
        extendRole.setPagesPrivilegesDtos(Collections.emptyList());

        when(userRoleService.extendRole(extendRole)).thenReturn(userDto);
        requestBuilder = post(baserUrl + "/extendRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(extendRole));
        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).extendRole(extendRole);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));
    }


    /**
     * Method under test: {@link UserRoleController#extendRole(ExtendRole)}
     */
    @Test
    void testExtendRole2() throws Exception {
        // Arrange
        ExtendRole extendRole = new ExtendRole();
        extendRole.setUsername(userDto.getEmail());
        extendRole.setPagesPrivilegesDtos(Collections.emptyList());

        when(userRoleService.extendRole(extendRole)).thenThrow(new UsernameNotFoundException("User with email " + extendRole.getUsername() + " not found"));
        requestBuilder = post(baserUrl + "/extendRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(extendRole));
        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).extendRole(extendRole);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }


    /**
     * Method under test: {@link UserRoleController#revokeExtendedPrivileges(RevokeExtendPrivilege)}
     */
    @Test
    void testRevokeExtendedPrivileges() throws Exception {
        // Arrange
        RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege();
        revokeExtendPrivilege.setSpecialPrivilegesMap(Collections.emptyMap());
        revokeExtendPrivilege.setUsername(userDto.getEmail());
        when(userRoleService.revokeExtendedPrivileges(revokeExtendPrivilege)).thenReturn(userDto);

        requestBuilder = put(baserUrl + "/revokeExtendedPrivileges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(revokeExtendPrivilege));
        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).revokeExtendedPrivileges(revokeExtendPrivilege);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));

    }


    /**
     * Method under test: {@link UserRoleController#revokeExtendedPrivileges(RevokeExtendPrivilege)}
     */
    @Test
    void testRevokeExtendedPrivileges2() throws Exception {
        // Arrange
        RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege();
        revokeExtendPrivilege.setSpecialPrivilegesMap(Collections.emptyMap());
        revokeExtendPrivilege.setUsername(userDto.getEmail());
        when(userRoleService.revokeExtendedPrivileges(revokeExtendPrivilege)).thenThrow(new UsernameNotFoundException("User with email " + revokeExtendPrivilege.getUsername() + " not found"));

        requestBuilder = put(baserUrl + "/revokeExtendedPrivileges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(revokeExtendPrivilege));
        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).revokeExtendedPrivileges(revokeExtendPrivilege);
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });

    }

    /**
     * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole() throws Exception {
        // Arrange
        String roleName = "SOME ROLE";
        RevokeRole revokeRole = new RevokeRole();
        revokeRole.setRoleNames(List.of(roleName));
        revokeRole.setUsername(userDto.getEmail());

        when(userRoleService.revokeRole(revokeRole)).thenReturn(userDto);
        requestBuilder = put(baserUrl + "/revokeRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(revokeRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).revokeRole(revokeRole);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));
    }

    /**
     * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole2() throws Exception {
        // Arrange
        String roleName = "SOME ROLE";
        RevokeRole revokeRole = new RevokeRole();
        revokeRole.setRoleNames(List.of(roleName));
        revokeRole.setUsername(userDto.getEmail());

        when(userRoleService.revokeRole(revokeRole)).thenThrow(new UsernameNotFoundException("User with email " + revokeRole.getUsername() + " not found"));
        requestBuilder = put(baserUrl + "/revokeRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(revokeRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).revokeRole(revokeRole);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
     */
    @Test
    void testRevokeRole3() throws Exception {
        // Arrange
        String roleName = "SOME ROLE";
        RevokeRole revokeRole = new RevokeRole();
        revokeRole.setRoleNames(List.of(roleName));
        revokeRole.setUsername(userDto.getEmail());

        when(userRoleService.revokeRole(revokeRole)).thenThrow(new RoleNotFoundException("Role with name " + roleName + " not found"));
        requestBuilder = put(baserUrl + "/revokeRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(revokeRole));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userRoleService, times(1)).revokeRole(revokeRole);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }
}

