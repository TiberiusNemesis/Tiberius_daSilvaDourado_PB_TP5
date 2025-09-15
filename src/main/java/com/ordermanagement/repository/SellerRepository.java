package com.ordermanagement.repository;

import com.ordermanagement.model.Seller;
import java.util.Optional;

public interface SellerRepository extends UserRepository<Seller> {
    Optional<Seller> findByCnpj(String cnpj);
}