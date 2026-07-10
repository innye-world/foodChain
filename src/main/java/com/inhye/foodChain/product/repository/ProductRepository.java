package com.inhye.foodChain.product.repository;

import com.inhye.foodChain.product.domain.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String> {

	@Query("SELECT p FROM Product p JOIN FETCH p.productType ORDER BY p.createdAt ASC")
	List<Product> findAllWithProductType();

	@Query(
			value = "SELECT p FROM Product p JOIN FETCH p.productType ORDER BY p.createdAt ASC",
			countQuery = "SELECT count(p) FROM Product p")
	Page<Product> findAllWithProductType(Pageable pageable);

	@Query("SELECT p FROM Product p JOIN FETCH p.productType WHERE p.productId = :productId")
	Optional<Product> findByIdWithProductType(@Param("productId") String productId);

	@Query(
			"""
			SELECT p FROM Product p JOIN FETCH p.productType pt
			WHERE pt.typeCode = :typeCode
			ORDER BY p.productId
			""")
	List<Product> findByTypeCode(@Param("typeCode") String typeCode);

	@Query(
			"""
			SELECT count(p) FROM Product p
			WHERE p.createdAt >= :weekStart
			AND p.createdAt < :weekEnd
			""")
    int countNewProductsInThisWeek(LocalDateTime weekStart, LocalDateTime weekEnd);

	/** 제조사 QR 시뮬레이터: 임의 상품 1건 */
	@Query(value = "SELECT p.product_id FROM product p ORDER BY RAND() LIMIT 1", nativeQuery = true)
	Optional<String> findRandomProductId();
}
