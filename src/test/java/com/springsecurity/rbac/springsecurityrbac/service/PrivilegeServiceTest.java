package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivilegeServiceTest {

    @Mock
    private PrivilegeRepository privilegeRepository;
    @InjectMocks
    private PrivilegeService privilegeService;

    /**
     * Method under test: {@link PrivilegeService#findByName(String)}
     */
    @Test
    void testFindByName1() {
        // Arrange
        String privilegeName = PRIVILEGE.READ;
        PrivilegeDto expectedFindByNameResult = new PrivilegeDto();
        expectedFindByNameResult.setName(privilegeName);

        Privilege privilege = new Privilege(privilegeName);

        when(privilegeRepository.findByName(privilegeName)).thenReturn(Optional.of(privilege));

        // Act
        PrivilegeDto actualFindByNameResult = this.privilegeService.findByName(privilegeName);

        // Assert
        assertEquals(actualFindByNameResult.getName(), expectedFindByNameResult.getName());
    }

    /**
     * Method under test: {@link PrivilegeService#findByName(String)}
     */
    @Test
    void testFindByName2() {
        // Arrange
        String privilegeName = PRIVILEGE.READ;
        Privilege privilege = new Privilege(privilegeName);

        NoSuchElementException expectedFindByNameResult = new NoSuchElementException("Privilege with name " + PRIVILEGE.WRITE + " not found!");


        when(privilegeRepository.findByName(anyString())).thenReturn(Optional.of(privilege));
        when(privilegeService.findByName(anyString())).thenThrow(expectedFindByNameResult);


        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.privilegeService.findByName(PRIVILEGE.WRITE));
    }

    /**
     * Method under test: {@link PrivilegeService#add(PrivilegeDto)}
     */
    @Test
    void testAdd() {
        // Arrange
        PrivilegeDto expectedAddResult = new PrivilegeDto(PRIVILEGE.READ);
        Privilege privilege = new Privilege(PRIVILEGE.READ);
        when(privilegeRepository.save(privilege)).thenReturn(privilege);

        // Act
        PrivilegeDto actualAddResult = this.privilegeService.add(expectedAddResult);

        // Assert
        assertEquals(actualAddResult, expectedAddResult);
        assertEquals(actualAddResult.getName(), expectedAddResult.getName());
    }

    /**
     * Method under test: {@link PrivilegeService#addOrGet(PrivilegeDto)}
     */
    @Test
    void testAddOrGet1() {
        // Arrange
        PrivilegeDto expectedAddOrGetResult = new PrivilegeDto(PRIVILEGE.WRITE);
        Privilege privilege = new Privilege(PRIVILEGE.WRITE);

        when(privilegeRepository.findByName(PRIVILEGE.WRITE)).thenReturn(Optional.of(privilege));

        // Act
        PrivilegeDto actualAddOrGetResult = this.privilegeService.addOrGet(expectedAddOrGetResult);

        // Assert
        assertEquals(actualAddOrGetResult, expectedAddOrGetResult);
        assertEquals(actualAddOrGetResult.getName(), expectedAddOrGetResult.getName());
    }

    /**
     * Method under test: {@link PrivilegeService#addOrGet(PrivilegeDto)}
     */
    @Test
    void testAddOrGet2() {
        // Arrange
        PrivilegeDto expectedAddOrGetResult = new PrivilegeDto(PRIVILEGE.WRITE);
        Privilege privilege = new Privilege(PRIVILEGE.WRITE);

        when(privilegeRepository.save(privilege)).thenReturn(privilege);

        // Act
        PrivilegeDto actualAddOrGetResult = this.privilegeService.addOrGet(expectedAddOrGetResult);

        // Assert
        assertEquals(actualAddOrGetResult, expectedAddOrGetResult);
        assertEquals(actualAddOrGetResult.getName(), expectedAddOrGetResult.getName());
    }

    /**
     * Method under test: {@link PrivilegeService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        PrivilegeDto write = new PrivilegeDto(PRIVILEGE.WRITE);
        PrivilegeDto read = new PrivilegeDto(PRIVILEGE.READ);
        PrivilegeDto update = new PrivilegeDto(PRIVILEGE.UPDATE);
        PrivilegeDto delete = new PrivilegeDto(PRIVILEGE.DELETE);
        Collection<PrivilegeDto> expectedFindAllResult = Arrays.asList(read, write, update, delete);

        when(privilegeRepository.findAll()).thenReturn(PrivilegeMapper.toPrivileges(expectedFindAllResult));

        // Act
        Collection<PrivilegeDto> actualFindAllResult = this.privilegeService.findAll();

        // Assert
        assertEquals(actualFindAllResult, expectedFindAllResult);
        assertEquals(actualFindAllResult.size(), expectedFindAllResult.size());
        assertEquals(actualFindAllResult.isEmpty(), expectedFindAllResult.isEmpty());
    }

    /**
     * Method under test: {@link PrivilegeService#remove(PrivilegeDto)}
     */
    @Test
    void testRemove1() throws NoSuchElementException {
        // Arrange
        PrivilegeDto privilegeDto = new PrivilegeDto(PRIVILEGE.READ);
        Privilege privilege = PrivilegeMapper.toPrivilege(privilegeDto);

        when(privilegeRepository.findByName(privilegeDto.getName())).thenReturn(Optional.of(privilege));
        // Act
        PrivilegeDto actualRemoveResult = this.privilegeService.remove(privilegeDto);

        // Assert
        assertThat(actualRemoveResult)
                .isEqualTo(privilegeDto)
                .isNotNull();

        assertThat(actualRemoveResult.getName())
                .isEqualTo(privilegeDto.getName())
                .isNotNull();
    }

    /**
     * Method under test: {@link PrivilegeService#remove(PrivilegeDto)}
     */
    @Test
    void testRemove2() throws NoSuchElementException {
        // Arrange
        PrivilegeDto privilegeDto = new PrivilegeDto("CREATE");
        NoSuchElementException expectedRemoveResult = new NoSuchElementException("Privilege with name " + privilegeDto.getName() + " not found");
        when(privilegeRepository.findByName(privilegeDto.getName())).thenThrow(expectedRemoveResult);


        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.privilegeService.remove(privilegeDto));

    }
}

