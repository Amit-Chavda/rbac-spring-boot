package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.entity.JwtUserRequest;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.security.UserDetailsImpl;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JwtUserControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private JwtUserController jwtUserController;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private JwtUserRequest jwtUserRequest;
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private String baserUrl;

    @BeforeEach
    void setup() {
        jwtUserRequest = new JwtUserRequest();
        jwtUserRequest.setEmail("admin@test.com");
        jwtUserRequest.setPassword("admin");

        mapper = new ObjectMapper();

        baserUrl = "/token";

        mockMvc = MockMvcBuilders.standaloneSetup(jwtUserController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }


    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken() throws Exception {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl(new User());
        when(userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(userDetails);
        requestBuilder = get(baserUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(jwtUserRequest));


        // Act
        resultActions = mockMvc.perform(requestBuilder);

        //Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.expiry", notNullValue()));
    }

    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken2() throws Exception {
        // Arrange
        jwtUserRequest.setPassword("123");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        requestBuilder = get(baserUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(jwtUserRequest));


        // Act
        resultActions = mockMvc.perform(requestBuilder);

        //Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException));
    }

    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken3() throws Exception {
        // Arrange
        jwtUserRequest.setEmail("admin@testing.com");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
        requestBuilder = get(baserUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(jwtUserRequest));


        // Act
        resultActions = mockMvc.perform(requestBuilder);

        //Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof BadCredentialsException);
                })
                .andExpect(result -> {
                    assertEquals("Bad credentials", Objects.requireNonNull(result.getResolvedException()).getMessage() + "");
                });
    }
}

