package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Product register(@RequestBody ProductRegisterRequest request) {
		return productService.register(
				request.productTypeId(),
				request.productName(),
				request.storageType(),
				request.minTemperature(),
				request.maxTemperature());
	}

	public record ProductRegisterRequest(
			Long productTypeId,
			String productName,
			StorageType storageType,
			BigDecimal minTemperature,
			BigDecimal maxTemperature) {}
}
