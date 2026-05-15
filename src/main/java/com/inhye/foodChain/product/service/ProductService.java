package com.inhye.foodChain.product.service;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.domain.ProductType;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.product.repository.ProductTypeRepository;
import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductTypeRepository productTypeRepository;
	private final ProductRepository productRepository;

	@Transactional
	public Product register(
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
