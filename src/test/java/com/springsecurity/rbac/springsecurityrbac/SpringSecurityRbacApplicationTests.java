package com.springsecurity.rbac.springsecurityrbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.annotation.WithMockCustomUser;
import com.springsecurity.rbac.springsecurityrbac.controller.JwtUserController;
import com.springsecurity.rbac.springsecurityrbac.controller.PageController;
import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.entity.JwtUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Objects;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringSecurityRbacApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private String tokenBaserUrl;
    private String pageBaseUrl;

    private JwtUserRequest jwtUserRequest;

    private PageDto pageDto;

    private final String pageCode = "ROLE";

    @BeforeEach
    void setup() {
        jwtUserRequest = new JwtUserRequest();
        jwtUserRequest.setEmail("admin@test.com");
        jwtUserRequest.setPassword("admin");
        tokenBaserUrl = "/token";
        pageBaseUrl = "/page";

        mapper = new ObjectMapper();

        pageDto = new PageDto();
        pageDto.setName("Some Page Name");
    }

    /**
     * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
     */
    @Test
    void testGenerateToken() throws Exception {
        // Arrange
        requestBuilder = get(tokenBaserUrl)
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
        requestBuilder = get(tokenBaserUrl)
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
        requestBuilder = get(tokenBaserUrl)
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

    //Class under test: PageController

    /**
     * Method under test: {@link PageController#createPage(PageDto)}
     */
    @Test
    @WithMockCustomUser
    void testCreatePage() throws Exception {
        // Arrange
        requestBuilder = post(pageBaseUrl + "/create?pageCode=" + pageCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(pageDto)));

    }

    /**
     * Method under test: {@link PageController#findAllPages()}
     */
    @Test
    @WithMockCustomUser
    void testFindAllPages() throws Exception {
        // Arrange
        requestBuilder = get(pageBaseUrl + "/findAll?pageCode=" + pageCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }

    /**
     * Method under test: {@link PageController#findPageByName(String)}
     */
    @Test
    @WithMockCustomUser
    void testFindPageByName() throws Exception {
        // Arrange
        String name = "ROLE";
        pageDto.setName(name);
        requestBuilder = get(pageBaseUrl + "/findByName?pageCode=" + pageCode + "&name=" + name)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(pageDto)));

    }

    /**
     * Method under test: {@link PageController#findPageByName(String)}
     */
    @Test
    @WithMockCustomUser
    void testFindPageByName2() throws Exception {
        // Arrange
        String name = "SOME PAGE";
        pageDto.setName(name);
        requestBuilder = get(pageBaseUrl + "/findByName?pageCode=" + pageCode + "&name=" + name)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("Page with name " + name + " not found"));
                });

    }

    /**
     * Method under test: {@link PageController#removePage(PageDto)}
     */
    @Test
    @WithMockCustomUser
    void testRemovePage() throws Exception {
        // Arrange
        pageDto.setName("SOME ROLE");

        //insert some page before to delete it later
        requestBuilder = post(pageBaseUrl + "/create?pageCode=" + pageCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));
        mockMvc.perform(requestBuilder);

        requestBuilder = delete(pageBaseUrl + "/remove?pageCode=" + pageCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));


        // Act
        resultActions = mockMvc.perform(requestBuilder);


        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(pageDto)));
    }

    /**
     * Method under test: {@link PageController#removePage(PageDto)}
     */
    @Test
    @WithMockCustomUser
    void testRemovePage2() throws Exception {
        // Arrange
        pageDto.setName("SOME ROLE");

        requestBuilder = delete(pageBaseUrl + "/remove?pageCode=" + pageCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));


        // Act
        resultActions = mockMvc.perform(requestBuilder);


        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("Page with name " + pageDto.getName() + " not found"));
                });
    }


}
