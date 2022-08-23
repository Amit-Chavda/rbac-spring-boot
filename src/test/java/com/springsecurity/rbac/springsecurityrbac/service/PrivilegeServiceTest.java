package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivilegeServiceTest {

    @Mock
    private PrivilegeRepository privilegeRepository;
    @InjectMocks
    private PrivilegeService privilegeService;

    private String privilegeName;
    private Privilege privilege;
    private PrivilegeDto privilegeDto;

    @BeforeEach
    void setup() {
        privilegeName = PRIVILEGE.READ;
        privilege = new Privilege(privilegeName);
        privilegeDto = new PrivilegeDto(privilegeName);
    }


    /**
     * Method under test: {@link PrivilegeService#findByName(String)}
     */
    @Test
    void testFindByName1() {
        // Arrange
        when(privilegeRepository.findByName(privilegeName)).thenReturn(Optional.of(privilege));

        // Act
        PrivilegeDto actualFindByNameResult = this.privilegeService.findByName(privilegeName);

        // Assert
        verify(privilegeRepository, times(1)).findByName(privilegeName);
        assertEquals(actualFindByNameResult.getName(), privilegeDto.getName());
    }

    /**
     * Method under test: {@link PrivilegeService#findByName(String)}
     */
    @Test
    void testFindByName2() {
        // Arrange
        when(privilegeRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.privilegeService.findByName(privilegeName));
        verify(privilegeRepository, times(1)).findByName(privilegeName);
    }

    /**
     * Method under test: {@link PrivilegeService#add(PrivilegeDto)}
     */
    @Test
    void testAdd() {
        // Arrange
        when(privilegeRepository.save(privilege)).thenReturn(privilege);

        // Act
        PrivilegeDto actualAddResult = this.privilegeService.add(privilegeDto);

        // Assert
        verify(privilegeRepository, times(1)).save(privilege);
        assertEquals(actualAddResult, privilegeDto);
    }

    /**
     * Method under test: {@link PrivilegeService#addOrGet(PrivilegeDto)}
     */
    @Test
    void testAddOrGet1() {
        // Arrange
        when(privilegeRepository.findByName(privilegeName)).thenReturn(Optional.of(privilege));

        // Act
        PrivilegeDto actualAddOrGetResult = this.privilegeService.addOrGet(privilegeDto);

        // Assert
        verify(privilegeRepository, times(1)).findByName(privilegeName);
        assertEquals(actualAddOrGetResult, privilegeDto);
    }

    /**
     * Method under test: {@link PrivilegeService#addOrGet(PrivilegeDto)}
     */
    @Test
    void testAddOrGet2() {
        // Arrange
        when(privilegeRepository.findByName(privilegeName)).thenReturn(Optional.ofNullable(privilege));

        // Act
        PrivilegeDto actualAddOrGetResult = this.privilegeService.addOrGet(privilegeDto);

        // Assert
        verify(privilegeRepository, times(1)).findByName(privilegeName);
        verifyNoMoreInteractions(privilegeRepository);
        assertEquals(actualAddOrGetResult, privilegeDto);
    }

    /**
     * Method under test: {@link PrivilegeService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        Collection<PrivilegeDto> expectedFindAllResult = Arrays.asList(privilegeDto);
        when(privilegeRepository.findAll()).thenReturn(PrivilegeMapper.toPrivileges(expectedFindAllResult));

        // Act
        Collection<PrivilegeDto> actualFindAllResult = this.privilegeService.findAll();

        // Assert
        verify(privilegeRepository, times(1)).findAll();
        assertEquals(actualFindAllResult, expectedFindAllResult);
        assertEquals(actualFindAllResult.size(), expectedFindAllResult.size());
    }

    /**
     * Method under test: {@link PrivilegeService#remove(PrivilegeDto)}
     */
    @Test
    void testRemove1() throws NoSuchElementException {
        // Arrange
        when(privilegeRepository.findByName(privilegeName)).thenReturn(Optional.of(privilege));

        // Act
        PrivilegeDto actualRemoveResult = this.privilegeService.remove(privilegeDto);

        // Assert
        verify(privilegeRepository, times(1)).findByName(privilegeName);
        verify(privilegeRepository, times(1)).delete(privilege);
        verifyNoMoreInteractions(privilegeRepository);
        assertEquals(actualRemoveResult, privilegeDto);
    }

    /**
     * Method under test: {@link PrivilegeService#remove(PrivilegeDto)}
     */
    @Test
    void testRemove2() throws NoSuchElementException {
        // Arrange
        when(privilegeRepository.findByName(privilegeDto.getName())).thenReturn(Optional.empty());


        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.privilegeService.remove(privilegeDto));
        verify(privilegeRepository, times(1)).findByName(privilegeName);
        verifyNoMoreInteractions(privilegeRepository);

    }
}

