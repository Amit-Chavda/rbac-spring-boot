package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.entity.JwtUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Objects;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.yml")
class JwtUserControllerTest {
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
    }


    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken() throws Exception {
        // Arrange
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
        requestBuilder = get(baserUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(jwtUserRequest));


        // Act
        resultActions = mockMvc.perform(requestBuilder);

        //Assert
        resultActions
                .andExpect(status().isBadRequest());
    }

    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken3() throws Exception {
        // Arrange
        jwtUserRequest.setEmail("admin@testing.com");
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

