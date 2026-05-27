package com.inhye.foodChain.product.service;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.domain.ProductType;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.product.repository.ProductTypeRepository;
import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductTypeRepository productTypeRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<ProductType> findAllProductTypes() {
		return productTypeRepository.findAll(Sort.by("typeCode"));
	}

	@Transactional
	public ProductType registerProductType(String typeCode, String typeName) {
		ProductType productType = ProductType.builder()
				.typeCode(typeCode.trim().toUpperCase())
				.typeName(typeName.trim())
				.seq(0)
				.build();
		return productTypeRepository.save(productType);
	}

	@Transactional(readOnly = true)
	public List<Product> findAllProducts() {
		return productRepository.findAllWithProductType();
	}

	@Transactional(readOnly = true)
	public List<Product> findProductsByTypeCode(String typeCode) {
		return productRepository.findByTypeCode(typeCode.trim().toUpperCase());
	}

	@Transactional(readOnly = true)
	public Product findProduct(String productId) {
		return productRepository
				.findByIdWithProductType(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
	}

	@Transactional
	public Product registerProduct(
			Long productTypeId,
			String productName,
			StorageType storageType,
			BigDecimal minTemperature,
			BigDecimal maxTemperature) {
		ProductType productType =
				productTypeRepository.findById(productTypeId).orElseThrow();

		String productId = productType.issueNextProductId();
		productTypeRepository.save(productType);

		Product product = Product.builder()
				.productId(productId)
				.productType(productType)
				.productName(productName)
				.storageType(storageType)
				.minTemperature(minTemperature)
				.maxTemperature(maxTemperature)
				.build();

		return productRepository.save(product);
	}
}
