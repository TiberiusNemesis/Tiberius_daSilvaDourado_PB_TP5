package com.ordermanagement.repository;

import com.ordermanagement.model.Product;
import com.ordermanagement.model.Seller;
import com.ordermanagement.enums.ProductCategory;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(ProductCategory category);
    List<Product> findBySeller(Seller seller);
    List<Product> findByAvailable(boolean available);
    List<Product> findByNameContaining(String name);
    void deleteById(Long id);
}