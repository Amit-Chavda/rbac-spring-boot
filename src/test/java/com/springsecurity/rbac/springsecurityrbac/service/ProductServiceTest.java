package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.Product;
import com.springsecurity.rbac.springsecurityrbac.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    /**
     * Method under test: {@link ProductService#saveProduct(Product)}
     */
    @Test
    void testSaveProduct() {
        // Arrange
        Product product = new Product();
        product.setId(123L);
        product.setName("Name");
        product.setPictureUrl("https://example.org/example");
        product.setPrice(10.0d);
        when(productRepository.save((Product) any())).thenReturn(product);

        Product product1 = new Product();
        product1.setId(123L);
        product1.setName("Name");
        product1.setPictureUrl("https://example.org/example");
        product1.setPrice(10.0d);

        // Act and Assert
        assertSame(product, productService.saveProduct(product1));
        verify(productRepository).save((Product) any());
    }

    /**
     * Method under test: {@link ProductService#findById(long)}
     */
    @Test
    void testFindById() {
        // Arrange
        Product product = new Product();
        product.setId(123L);
        product.setName("Name");
        product.setPictureUrl("https://example.org/example");
        product.setPrice(10.0d);
        Optional<Product> ofResult = Optional.of(product);
        when(productRepository.findById((Long) any())).thenReturn(ofResult);

        // Act and Assert
        assertSame(product, productService.findById(123L));
        verify(productRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link ProductService#findById(long)}
     */
    @Test
    void testFindById2() {
        // Arrange
        when(productRepository.findById((Long) any())).thenReturn(Optional.empty());

        // Act
        productService.findById(123L);

        // Assert
        verify(productRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link ProductService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> actualFindAllResult = productService.findAll();

        // Assert
        assertSame(productList, actualFindAllResult);
        assertTrue(actualFindAllResult.isEmpty());
        verify(productRepository).findAll();
    }

    /**
     * Method under test: {@link ProductService#removeById(long)}
     */
    @Test
    void testRemoveById() {
        // Arrange
        doNothing().when(productRepository).deleteById((Long) any());
        when(productRepository.existsById((Long) any())).thenReturn(true);

        // Act
        productService.removeById(123L);

        // Assert that nothing has changed
        verify(productRepository).existsById((Long) any());
        verify(productRepository).deleteById((Long) any());
        assertTrue(productService.findAll().isEmpty());
    }

    /**
     * Method under test: {@link ProductService#removeById(long)}
     */
    @Test
    void testRemoveById2() {
        // Arrange
        when(productRepository.existsById((Long) any())).thenReturn(false);

        // Act
        productService.removeById(123L);

        // Assert that nothing has changed
        verify(productRepository).existsById((Long) any());
        assertTrue(productService.findAll().isEmpty());
    }
}

