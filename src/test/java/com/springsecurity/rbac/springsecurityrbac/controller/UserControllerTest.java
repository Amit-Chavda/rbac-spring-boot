package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springsecurity.rbac.springsecurityrbac.dto.UserDto;
import com.springsecurity.rbac.springsecurityrbac.exception.UserAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


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

        baserUrl = "/user";
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }


    /**
     * Method under test: {@link UserController#createUser(UserDto)}
     */
    @Test
    void testCreateUser() throws Exception {
        // Arrange
        when(userService.createUser(userDto)).thenReturn(userDto);

        requestBuilder = post(baserUrl + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userService, times(1)).createUser(userDto);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));

    }

    /**
     * Method under test: {@link UserController#createUser(UserDto)}
     */
    @Test
    void testCreateUser2() throws Exception {
        // Arrange
        when(userService.createUser(userDto)).thenThrow(new UserAlreadyExistException("User with " + userDto.getEmail() + " already exist!"));

        requestBuilder = post(baserUrl + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userService, times(1)).createUser(userDto);

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("already exist"));
                });

    }

    /**
     * Method under test: {@link UserController#deleteByEmail(String)}
     */
    @Test
    void testDeleteByEmail() throws Exception {
        // Arrange
        String email = userDto.getEmail();
        when(userService.deleteByEmail(email)).thenReturn(userDto);
        requestBuilder = delete("/user/deleteByEmail").param("email", email);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userService, times(1)).deleteByEmail(email);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userDto)));
    }

    /**
     * Method under test: {@link UserController#deleteByEmail(String)}
     */
    @Test
    void testDeleteByEmail2() throws Exception {
        // Arrange
        String email = userDto.getEmail();
        when(userService.deleteByEmail(email)).thenThrow(new UsernameNotFoundException("User with email " + email + " not found"));
        requestBuilder = delete("/user/deleteByEmail").param("email", email);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userService, times(1)).deleteByEmail(email);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });
    }

    /**
     * Method under test: {@link UserController#getAllUsers()}
     */
    @Test
    void testGetAllUsers() throws Exception {
        // Arrange
        when(userService.findAll()).thenReturn(List.of(userDto));
        requestBuilder = get("/user/findAll");

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(userService, times(1)).findAll();
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(List.of(userDto))));
    }
}

