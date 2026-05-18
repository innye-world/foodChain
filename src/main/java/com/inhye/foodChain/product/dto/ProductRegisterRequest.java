package com.inhye.foodChain.product.dto;

import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;

public record ProductRegisterRequest(
		Long productTypeId,
		String productName,
		StorageType storageType,
		BigDecimal minTemperature,
		BigDecimal maxTemperature) {}
