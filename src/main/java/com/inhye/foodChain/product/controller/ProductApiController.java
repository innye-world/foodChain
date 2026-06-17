package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.dto.ProductOptionDto;
import com.inhye.foodChain.product.dto.ProductRegisterRequest;
import com.inhye.foodChain.product.dto.ProductResponse;
import com.inhye.foodChain.product.dto.ProductTypeRegisterRequest;
import com.inhye.foodChain.product.dto.ProductTypeResponse;
import com.inhye.foodChain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품", description = "상품 유형·상품 마스터 API. 상품 ID는 유형 코드와 순번으로 자동 발급됩니다. (예: BAB-001)")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

	private final ProductService productService;

	@Operation(
			summary = "유형별 상품 목록",
			description = "상품 유형 코드(typeCode)에 속한 상품 목록을 반환합니다. 재고 등록 화면의 상품 선택 등에 사용합니다.")
	@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ProductOptionDto.class)))
	@GetMapping
	public List<ProductOptionDto> productsByTypeCode(
			@Parameter(description = "상품 유형 코드", example = "BAB", required = true)
					@RequestParam
					String typeCode) {
		return productService.findProductsByTypeCode(typeCode).stream()
				.map(p -> new ProductOptionDto(p.getProductId(), p.getProductName()))
				.toList();
	}

	@Operation(
			summary = "상품 유형 등록",
			description = "새 상품 유형을 등록합니다. typeCode는 대문자로 저장되며, 이후 상품 ID 접두어로 사용됩니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "201",
				description = "등록 성공",
				content = @Content(schema = @Schema(implementation = ProductTypeResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 오류"),
		@ApiResponse(responseCode = "409", description = "중복 typeCode"),
		@ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping("/type")
	@ResponseStatus(HttpStatus.CREATED)
	public ProductTypeResponse registerProductType(@Valid @RequestBody ProductTypeRegisterRequest request) {
		return ProductTypeResponse.from(
				productService.registerProductType(request.typeCode(), request.typeName()));
	}

	@Operation(
			summary = "상품 등록",
			description = "상품을 등록하고 productId를 자동 발급합니다. 보관 온도 범위와 storageType을 함께 지정합니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "201",
				description = "등록 성공",
				content = @Content(schema = @Schema(implementation = ProductResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 오류"),
		@ApiResponse(responseCode = "404", description = "상품 유형 미존재"),
		@ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponse registerProduct(@Valid @RequestBody ProductRegisterRequest request) {
		return ProductResponse.from(
				productService.registerProduct(
						request.productTypeId(),
						request.productName(),
						request.storageType(),
						request.minTemperature(),
						request.maxTemperature()));
	}
}
