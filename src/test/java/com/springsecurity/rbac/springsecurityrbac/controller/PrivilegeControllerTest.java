package com.springsecurity.rbac.springsecurityrbac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.handler.GlobalResponseEntityExceptionHandler;
import com.springsecurity.rbac.springsecurityrbac.service.PrivilegeService;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PrivilegeControllerTest {

    @Mock
    private PrivilegeService privilegeService;

    @InjectMocks
    private PrivilegeController privilegeController;

    private ObjectMapper mapper;

    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    private PrivilegeDto privilegeDto;
    private String baserUrl;

    @BeforeEach
    void setup() {
        privilegeDto = new PrivilegeDto();
        privilegeDto.setName("Some Privilege Name");
        mapper = new ObjectMapper();
        baserUrl = "/privilege";
        mockMvc = MockMvcBuilders.standaloneSetup(privilegeController)
                .setControllerAdvice(new GlobalResponseEntityExceptionHandler())
                .build();
    }


    /**
     * Method under test: {@link PrivilegeController#createPrivilege(PrivilegeDto)}
     */
    @Test
    void testCreatePrivilege() throws Exception {
        // Arrange
        when(privilegeService.add(privilegeDto)).thenReturn(privilegeDto);
        requestBuilder = post(baserUrl + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(privilegeDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).add(privilegeDto);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(privilegeDto)));
    }

    /**
     * Method under test: {@link PrivilegeController#findAllPrivileges()}
     */
    @Test
    void testFindAllPrivileges() throws Exception {
        // Arrange
        when(privilegeService.findAll()).thenReturn(List.of(privilegeDto));
        requestBuilder = get(baserUrl + "/findAll")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).findAll();

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)))
                .andExpect(content().string(mapper.writeValueAsString(List.of(privilegeDto))));
    }

    /**
     * Method under test: {@link PrivilegeController#findPrivilegeByName(String)}
     */
    @Test
    void testFindPrivilegeByName() throws Exception {
        // Arrange
        String name = privilegeDto.getName();
        when(privilegeService.findByName(name)).thenReturn(privilegeDto);
        requestBuilder = get(baserUrl + "/findByName?name=" + name);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).findByName(name);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(privilegeDto)));

    }


    /**
     * Method under test: {@link PrivilegeController#findPrivilegeByName(String)}
     */
    @Test
    void testFindPrivilegeByName2() throws Exception {
        // Arrange
        String name = privilegeDto.getName();
        when(privilegeService.findByName(name)).thenThrow(new NoSuchElementException("Privilege with name " + name + " not found"));
        requestBuilder = get(baserUrl + "/findByName?name=" + name);

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).findByName(name);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException().getMessage().contains("not found"));
                });

    }

    /**
     * Method under test: {@link PrivilegeController#removePrivilege(PrivilegeDto)}
     */
    @Test
    void testRemovePrivilege() throws Exception {
        // Arrange
        when(privilegeService.remove(privilegeDto)).thenReturn(privilegeDto);
        requestBuilder = delete(baserUrl + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(privilegeDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).remove(privilegeDto);

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(privilegeDto)));

    }

    /**
     * Method under test: {@link PrivilegeController#removePrivilege(PrivilegeDto)}
     */
    @Test
    void testRemovePrivilege2() throws Exception {
        // Arrange
        when(privilegeService.remove(privilegeDto)).thenThrow(new NoSuchElementException("Privilege with name " + privilegeDto.getName() + " not found"));
        requestBuilder = delete(baserUrl + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(privilegeDto));

        // Act
        resultActions = mockMvc.perform(requestBuilder);

        // Assert
        verify(privilegeService, times(1)).remove(privilegeDto);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found"));
                });

    }
}

