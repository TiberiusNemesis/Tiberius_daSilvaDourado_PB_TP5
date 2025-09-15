package com.ordermanagement.repository;

import com.ordermanagement.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository<T extends User> {
    T save(T user);
    Optional<T> findById(Long id);
    Optional<T> findByEmail(String email);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsByEmail(String email);
}