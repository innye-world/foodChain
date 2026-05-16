package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.dto.ProductRegisterRequest;
import com.inhye.foodChain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

	private final ProductService productService;

	@PostMapping("/type")
	public RedirectView registerProductType(
			@RequestParam String typeCode, @RequestParam String typeName) {
		productService.registerProductType(typeCode, typeName);
		return new RedirectView("/product/type");
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Product registerProduct(@RequestBody ProductRegisterRequest request) {
		return productService.registerProduct(
				request.productTypeId(),
				request.productName(),
				request.storageType(),
				request.minTemperature(),
				request.maxTemperature());
	}
}
