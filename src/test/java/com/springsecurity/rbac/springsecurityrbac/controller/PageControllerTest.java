package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.annotation.WithMockCustomUser;
import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Objects;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "/application-test.yml")
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private PageDto pageDto;
    private String baserUrl;

    private final String pageCode = "ROLE";

    @BeforeEach
    void setup() {
        pageDto = new PageDto();
        pageDto.setName("Some Page Name");

        mapper = new ObjectMapper();
        baserUrl = "/page";
    }

    /**
     * Method under test: {@link PageController#createPage(PageDto)}
     */
    @Test
    @WithMockCustomUser
    void testCreatePage() throws Exception {
        // Arrange
        requestBuilder = post(baserUrl + "/create?pageCode=" + pageCode)
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
        requestBuilder = get(baserUrl + "/findAll?pageCode=" + pageCode)
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
        requestBuilder = get(baserUrl + "/findByName?pageCode=" + pageCode + "&name=" + name)
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
        requestBuilder = get(baserUrl + "/findByName?pageCode=" + pageCode + "&name=" + name)
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
        requestBuilder = post(baserUrl + "/create?pageCode=" + pageCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));
        mockMvc.perform(requestBuilder);

        requestBuilder = delete(baserUrl + "/remove?pageCode=" + pageCode)
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

        requestBuilder = delete(baserUrl + "/remove?pageCode=" + pageCode)
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

