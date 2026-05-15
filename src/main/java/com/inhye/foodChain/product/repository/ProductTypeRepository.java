package com.inhye.foodChain.product.repository;

import com.inhye.foodChain.product.domain.ProductType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

	Optional<ProductType> findByTypeCode(String typeCode);
}
