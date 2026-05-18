package com.inhye.foodChain.product.repository;

import com.inhye.foodChain.product.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String> {

	@Query("SELECT p FROM Product p JOIN FETCH p.productType ORDER BY p.createdAt ASC")
	List<Product> findAllWithProductType();

	@Query("SELECT p FROM Product p JOIN FETCH p.productType WHERE p.productId = :productId")
	Optional<Product> findByIdWithProductType(@Param("productId") String productId);
}
