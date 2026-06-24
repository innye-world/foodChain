package com.inhye.foodChain.common.scheduler;

import com.inhye.foodChain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockStatusScheduler {

	private final StockService stockService;

	@Scheduled(cron = "0 0 0 * * *")
	public void applyWarningAndExpired() {
		stockService.updateStockStatusesByExpiry();
	}
}
