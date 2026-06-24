package com.inhye.foodChain.product.service;

import com.inhye.foodChain.common.exception.ResourceNotFoundException;
import com.inhye.foodChain.common.web.PaginationConstants;
import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.domain.ProductType;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.product.repository.ProductTypeRepository;
import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	@Transactional(readOnly = true)
	public Page<ProductType> findProductTypesPage(int page) {
		return productTypeRepository.findAll(
				PageRequest.of(Math.max(page, 0), PaginationConstants.PAGE_SIZE, Sort.by("typeCode")));
	}

	@Transactional(readOnly = true)
	public List<Product> findAllProducts() {
		return productRepository.findAllWithProductType();
	}

	@Transactional(readOnly = true)
	public Page<Product> findProductsPage(int page) {
		return productRepository.findAllWithProductType(
				PageRequest.of(Math.max(page, 0), PaginationConstants.PAGE_SIZE));
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
	public List<Product> findProductsByTypeCode(String typeCode) {
		return productRepository.findByTypeCode(typeCode.trim().toUpperCase());
	}

	@Transactional(readOnly = true)
	public Product findProduct(String productId) {
		return productRepository
				.findByIdWithProductType(productId)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다: " + productId));
	}

	@Transactional
	public Product registerProduct(
			Long productTypeId,
			String productName,
			StorageType storageType,
			BigDecimal minTemperature,
			BigDecimal maxTemperature,
			int warningThresholdDays,
			BigDecimal warningThresholdPct) {
		ProductType productType =
				productTypeRepository
						.findById(productTypeId)
						.orElseThrow(
								() -> new ResourceNotFoundException("상품 유형을 찾을 수 없습니다: " + productTypeId));

		String productId = productType.issueNextProductId();
		productTypeRepository.save(productType);

		Product product = Product.builder()
				.productId(productId)
				.productType(productType)
				.productName(productName)
				.storageType(storageType)
				.minTemperature(minTemperature)
				.maxTemperature(maxTemperature)
				.warningThresholdDays(warningThresholdDays)
				.warningThresholdPct(warningThresholdPct)
				.build();

		return productRepository.save(product);
	}
}
