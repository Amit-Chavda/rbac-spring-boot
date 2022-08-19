package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.Product;
import com.springsecurity.rbac.springsecurityrbac.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product findById(long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            logger.info("Product with id {} not found!", id);
            return new Product();
        }
        return product.get();
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void removeById(long id) {
        if (productRepository.existsById(id)) {
            logger.info("Product with id {} deleted!", id);
            productRepository.deleteById(id);
        }
        logger.info("Product with id {} not found!", id);
    }
}
