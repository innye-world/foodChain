package com.inhye.foodChain.stock.service;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockStatus;
import com.inhye.foodChain.stock.repository.StockRepository;
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
			int amount) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

		Stock stock = Stock.builder()
				.product(product)
				.lotNo(lotNo)
				.mfgDate(mfgDate)
				.expiryDate(expiryDate)
				.receivedAt(LocalDateTime.now())
				.amount(amount)
				.stockStatus(StockStatus.PENDING_INSPECTION)
				.build();

		return stockRepository.save(stock);
	}
}
