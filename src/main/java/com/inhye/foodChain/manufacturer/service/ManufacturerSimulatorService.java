package com.inhye.foodChain.manufacturer.service;

import com.inhye.foodChain.manufacturer.dto.ManufacturerQrPayload;
import com.inhye.foodChain.product.service.ProductService;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManufacturerSimulatorService {

	private static final int MIN_AMOUNT = 1;
	private static final int MAX_AMOUNT = 500;

	private final ProductService productService;

	@Transactional(readOnly = true)
	public ManufacturerQrPayload generateRandomQrPayload() {
		String productId = productService.findRandomProduct().getProductId();
		LocalDate mfgDate = LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 31));
		LocalDate expiryDate = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(1, 366));
		if (!expiryDate.isAfter(mfgDate)) {
			expiryDate = mfgDate.plusDays(ThreadLocalRandom.current().nextInt(7, 181));
		}
		int amount = ThreadLocalRandom.current().nextInt(MIN_AMOUNT, MAX_AMOUNT + 1);
		return new ManufacturerQrPayload(productId, mfgDate, expiryDate, amount);
	}
}
