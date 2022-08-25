package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.PageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

    @Mock
    private PageService pageService;

    @InjectMocks
    private PageController pageController;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private PageDto pageDto;
    private String baserUrl;

    @BeforeEach
    void setup() {
        pageDto = new PageDto();
        pageDto.setName("Some Page Name");
        mapper = new ObjectMapper();
        baserUrl = "/page";
        mockMvc = MockMvcBuilders.standaloneSetup(pageController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }

    /**
     * Method under test: {@link PageController#createPage(PageDto)}
     */
    @Test
    void testCreatePage() throws Exception {
        // Arrange
        when(pageService.add(pageDto)).thenReturn(pageDto);

        requestBuilder = post(baserUrl + "/create")
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
    void testFindAllPages() throws Exception {
        // Arrange
        when(pageService.findAll()).thenReturn(List.of(pageDto));
        requestBuilder = get(baserUrl + "/findAll")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)))
                .andExpect(content().string(mapper.writeValueAsString(List.of(pageDto))));
    }

    /**
     * Method under test: {@link PageController#findPageByName(String)}
     */
    @Test
    void testFindPageByName() throws Exception {
        // Arrange
        String name = "ROLE";
        pageDto.setName(name);
        when(pageService.findByName(name)).thenReturn(pageDto);

        requestBuilder = get(baserUrl + "/findByName?name=" + name)
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
    void testFindPageByName2() throws Exception {
        // Arrange
        String name = "SOME PAGE";
        pageDto.setName(name);
        when(pageService.findByName(name)).thenThrow(new NoSuchElementException("Page with name " + name + " not found"));
        requestBuilder = get(baserUrl + "/findByName?name=" + name)
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
    void testRemovePage() throws Exception {
        // Arrange
        pageDto.setName("SOME ROLE");
        when(pageService.remove(pageDto)).thenReturn(pageDto);
        requestBuilder = delete(baserUrl + "/remove")
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
    void testRemovePage2() throws Exception {
        // Arrange
        pageDto.setName("SOME ROLE");
        when(pageService.remove(pageDto)).thenThrow(new NoSuchElementException("Page with name " + pageDto.getName() + " not found"));

        requestBuilder = delete(baserUrl + "/remove")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pageDto));
        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains("Page with name " + pageDto.getName() + " not found"));
                });
    }
}

