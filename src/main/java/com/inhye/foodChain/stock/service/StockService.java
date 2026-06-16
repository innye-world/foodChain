package com.inhye.foodChain.stock.service;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockStatus;
import com.inhye.foodChain.stock.repository.StockRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<Stock> findAllStocksOrderByFefo() {
		return stockRepository.findAllOrderByFefo();
	}

	@Transactional
	public Stock registerStock(
			String productId,
			String lotNo,
			LocalDate mfgDate,
			LocalDate expiryDate,
			int amount,
			BigDecimal currentTemperature) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

		// 현재 온도가 제품의 적정온도 구간에 들어가 있지않으면 stockStatus는 hold로 저장한다.
		// Double은 부동소수라서 소숫점이 있는 온도, 무게 객체에는 적합하지 않음
		BigDecimal minTemperature = product.getMinTemperature();
		BigDecimal maxTemperature = product.getMaxTemperature();
		StockStatus stockStatus = StockStatus.AVAILABLE;

		if(currentTemperature.compareTo(minTemperature) < 0
				|| currentTemperature.compareTo(maxTemperature) > 0) {
			stockStatus = StockStatus.HOLD;
		}

		Stock stock = Stock.builder()
				.product(product)
				.lotNo(lotNo)
				.mfgDate(mfgDate)
				.expiryDate(expiryDate)
				.receivedAt(LocalDateTime.now())
				.amount(amount)
				.stockStatus(stockStatus)
				.build();

		return stockRepository.save(stock);
	}
}
