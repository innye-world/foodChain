package com.inhye.foodChain.manufacturer.dto;

import java.time.LocalDate;

public record ManufacturerQrPayload(
		String productId,
		LocalDate mfgDate,
		LocalDate expiryDate,
		int amount) {
}
