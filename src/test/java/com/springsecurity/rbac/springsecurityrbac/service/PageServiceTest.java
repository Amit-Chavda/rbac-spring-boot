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

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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
        assertThat(actualFindByNameResult)
                .isNotNull()
                .isEqualTo(expectedFindByNameResult);

        assertThat(actualFindByNameResult.getName())
                .isNotNull()
                .isEqualTo(expectedFindByNameResult.getName());
    }

    /**
     * Method under test: {@link PageService#findByName(String)}
     */
    @Test
    void testFindByName2() throws NoSuchElementException {
        // Arrange
        String name = "DEMO";
        NoSuchElementException noSuchElementException = new NoSuchElementException("Page with name " + name + " not found!");

        when(pageRepository.findByName(name)).thenThrow(noSuchElementException);

        // Act and  Assert
        assertThrows(NoSuchElementException.class, () -> this.pageService.findByName(name));
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
        assertThat(actualAddResult)
                .isNotNull()
                .isEqualTo(expectedAddResult);

        assertThat(actualAddResult.getName())
                .isNotNull()
                .isEqualTo(expectedAddResult.getName());

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


        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(expectedAddResult);

        assertThat(actualAddOrGetResult.getName())
                .isNotNull()
                .isEqualTo(expectedAddResult.getName());
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

        //when(pageRepository.findByName(name)).thenReturn(Optional.of(null));
        when(pageRepository.save(page)).thenReturn(page);


        // Act
        PageDto actualAddOrGetResult = this.pageService.addOrGet(expectedAddResult);

        // Assert
        verify(pageRepository,times(1)).save(page);


        assertThat(actualAddOrGetResult)
                .isNotNull()
                .isEqualTo(expectedAddResult);

        assertThat(actualAddOrGetResult.getName())
                .isNotNull()
                .isEqualTo(expectedAddResult.getName());
    }

    /**
     * Method under test: {@link PageService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        Page page1 = new Page(PAGE.PRODUCT);
        Page page2 = new Page(PAGE.USER);
        Collection<PageDto> expectedFindAllResult = PageMapper.toPageDtos(Arrays.asList(page1, page2));

        when(pageRepository.findAll()).thenReturn(Arrays.asList(page1, page2));

        // Act
        Collection<PageDto> actualFindAllResult = this.pageService.findAll();

        // Assert
        assertThat(actualFindAllResult)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedFindAllResult);

    }

    /**
     * Method under test: {@link PageService#remove(PageDto)}
     */
    @Test
    void testRemove() {
        // Arrange
        String name = PAGE.PRODUCT;
        PageDto pageDto = new PageDto(PAGE.PRODUCT);
        Page page = PageMapper.toPage(pageDto);

        when(pageRepository.findByName(name)).thenReturn(Optional.of(page));

        // Act
        PageDto actualRemoveResult = this.pageService.remove(pageDto);

        // Assert
        assertThat(actualRemoveResult)
                .isNotNull()
                .isEqualTo(pageDto);
    }

    @Test
    void testRemove2() {
        // Arrange
        String name = "HOME";
        PageDto pageDto = new PageDto(name);
        NoSuchElementException noSuchElementException = new NoSuchElementException("Page with name " + name + " not found!");

        doThrow(noSuchElementException).when(pageRepository).findByName(anyString());

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> this.pageService.remove(pageDto));
    }
}

