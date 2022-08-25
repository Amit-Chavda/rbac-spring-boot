package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;
    @InjectMocks
    private PageService pageService;

    /**
     * Method under test: {@link PageService#findByName(String)}
     */
    @Test
    void testFindByName() throws NoSuchElementException {
        // Arrange
        String name = PAGE.USER;
        PageDto expectedFindByNameResult = new PageDto(name);
        Page page = PageMapper.toPage(expectedFindByNameResult);
        when(pageRepository.findByName(name)).thenReturn(Optional.of(page));

        // Act
        PageDto actualFindByNameResult = this.pageService.findByName(name);

        // Assert
        verify(pageRepository, times(1)).findByName(name);

        assertEquals(actualFindByNameResult, expectedFindByNameResult);
        assertEquals(actualFindByNameResult.getName(), expectedFindByNameResult.getName());
    }

    /**
     * Method under test: {@link PageService#findByName(String)}
     */
    @Test
    void testFindByName2() throws NoSuchElementException {
        // Arrange
        String name = "DEMO";
        when(pageRepository.findByName(name)).thenReturn(Optional.empty());

        // Act and  Assert
        assertThrows(NoSuchElementException.class, () -> this.pageService.findByName(name));
        verify(pageRepository, times(1)).findByName(name);
    }

    /**
     * Method under test: {@link PageService#add(PageDto)}
     */
    @Test
    void testAdd() {
        // Arrange
        PageDto expectedAddResult = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(expectedAddResult);
        when(this.pageRepository.save(page)).thenReturn(page);

        // Act
        PageDto actualAddResult = this.pageService.add(expectedAddResult);

        // Assert
        verify(pageRepository, times(1)).save(page);
        assertEquals(actualAddResult, expectedAddResult);
        assertEquals(actualAddResult.getName(), expectedAddResult.getName());
    }

    /**
     * Method under test: {@link PageService#addOrGet(PageDto)}
     */
    @Test
    void testAddOrGet() {
        // Arrange
        String name = PAGE.PRODUCT;
        PageDto expectedAddResult = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(expectedAddResult);

        when(pageRepository.findByName(name)).thenReturn(Optional.of(page));

        // Act
        PageDto actualAddOrGetResult = this.pageService.addOrGet(expectedAddResult);

        // Assert
        verify(pageRepository, times(1)).findByName(name);
        assertEquals(actualAddOrGetResult, expectedAddResult);
        assertEquals(actualAddOrGetResult.getName(), expectedAddResult.getName());
    }

    /**
     * Method under test: {@link PageService#addOrGet(PageDto)}
     */
    @Test
    void testAddOrGet2() {
        // Arrange
        String name = PAGE.PRODUCT;
        PageDto expectedAddResult = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(expectedAddResult);
        when(pageRepository.save(page)).thenReturn(page);

        // Act
        PageDto actualAddOrGetResult = this.pageService.addOrGet(expectedAddResult);

        // Assert
        verify(pageRepository, times(1)).save(page);
        assertEquals(actualAddOrGetResult, expectedAddResult);
        assertEquals(actualAddOrGetResult.getName(), expectedAddResult.getName());
    }

    /**
     * Method under test: {@link PageService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        List<Page> pageList = new ArrayList<>();
        pageList.add(new Page(PAGE.PRODUCT));
        pageList.add(new Page(PAGE.USER));
        Collection<PageDto> expectedFindAllResult = PageMapper.toPageDtos(pageList);
        when(pageRepository.findAll()).thenReturn(pageList);

        // Act
        Collection<PageDto> actualFindAllResult = this.pageService.findAll();

        // Assert
        verify(pageRepository, times(1)).findAll();
        assertEquals(actualFindAllResult, expectedFindAllResult);
    }

    /**
     * Method under test: {@link PageService#remove(PageDto)}
     */
    @Test
    void testRemove() {
        // Arrange
        String name = PAGE.PRODUCT;
        PageDto pageDto = new PageDto(name);
        Page page = new Page(name);
        when(pageRepository.findByName(name)).thenReturn(Optional.of(page));

        // Act
        PageDto actualRemoveResult = this.pageService.remove(pageDto);

        // Assert
        verify(pageRepository, times(1)).findByName(name);
        verify(pageRepository, times(1)).delete(page);
        assertEquals(actualRemoveResult, pageDto);
    }

    @Test
    void testRemove2() {
        // Arrange
        String name = "HOME";
        PageDto pageDto = new PageDto(name);
        when(pageRepository.findByName(name)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.pageService.remove(pageDto));
        verify(pageRepository, times(1)).findByName(name);
        verifyNoMoreInteractions(pageRepository);
    }
}

