package com.inhye.foodChain.product.repository;

import com.inhye.foodChain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
