package com.ordermanagement.service;

import com.ordermanagement.model.Product;
import com.ordermanagement.model.Seller;
import com.ordermanagement.enums.ProductCategory;
import com.ordermanagement.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findByAvailable(true);
    }
    
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getProductsBySeller(Seller seller) {
        return productRepository.findBySeller(seller);
    }
    
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product createProduct(String name, String description, BigDecimal price, 
                               ProductCategory category, Seller seller) {
        Product product = new Product(name, description, price, category, seller);
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, String name, String description, BigDecimal price, 
                               ProductCategory category, String imageUrl) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setImageUrl(imageUrl);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found");
    }
    
    public void toggleProductAvailability(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setAvailable(!product.isAvailable());
            productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }
}