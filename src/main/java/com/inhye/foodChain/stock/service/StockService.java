package com.inhye.foodChain.stock.service;

import com.inhye.foodChain.common.exception.ResourceNotFoundException;
import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.stock.domain.MovementType;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockMovement;
import com.inhye.foodChain.stock.domain.StockStatus;
import com.inhye.foodChain.stock.repository.StockMovementRepository;
import com.inhye.foodChain.stock.repository.StockRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;
	private final StockMovementRepository stockMovementRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<Stock> findAllStocksOrderByFefo() {
		return stockRepository.findAllOrderByFefo();
	}

	/**
	 * 입고 등록 시에는 무조건 STOCK, STOCK_MOVEMENT 테이블 모두에 데이터를 넣는다.
	 * @param productId
	 * @param lotNo
	 * @param mfgDate
	 * @param expiryDate
	 * @param amount
	 * @param currentTemperature
	 * @return
	 */
	@Transactional
	public Stock registerStock(
			String productId,
			String lotNo,
			LocalDate mfgDate,
			LocalDate expiryDate,
			int amount,
			BigDecimal currentTemperature) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다: " + productId));

		// 현재 온도가 제품의 적정온도 구간에 들어가 있지 않으면 stockStatus는 hold로 저장한다.
		// Double은 부동소수라서 소숫점이 있는 온도, 무게 객체에는 적합하지 않음
		BigDecimal minTemperature = product.getMinTemperature();
		BigDecimal maxTemperature = product.getMaxTemperature();
		StockStatus stockStatus = StockStatus.AVAILABLE;
		MovementType movementType = MovementType.INBOUND;
		String reason = "재고 입고";

		if(currentTemperature.compareTo(minTemperature) < 0
				|| currentTemperature.compareTo(maxTemperature) > 0) {
			stockStatus = StockStatus.HOLD;
		}

		// 1. 먼저 재고 관리에 데이터 추가
		Stock stock =
				stockRepository.save(
						Stock.builder()
								.product(product)
								.lotNo(lotNo)
								.mfgDate(mfgDate)
								.expiryDate(expiryDate)
								.receivedAt(LocalDateTime.now())
								.amount(amount)
								.stockStatus(stockStatus)
								.build());

		// 2. 재고 히스토리 데이터에도 추가 (자주 이슈가 발생하는 배치의 원인 트래킹을 위한 과정)
		if (stockStatus == StockStatus.HOLD) {
			movementType = MovementType.HOLD;
			reason = "입고 구역 온도 부적합: " + currentTemperature
							+ "℃ (적정 "
							+ minTemperature
							+ "~"
							+ maxTemperature
							+ "℃)";
		}

		saveMovement(stock, movementType, amount, reason);

		return stock;
	}

	/**
	 * AVAILABLE·WARNING 재고의 유통기한을 검사해 WARNING·EXPIRED로 갱신한다.
	 * HOLD는 품질 해제 후 AVAILABLE이 되면 다음 배치에서 처리한다.
	 */
	@Transactional
	public void updateStockStatusesByExpiry() {
		List<Stock> stockList = stockRepository.findActiveStocksForExpiryUpdate();
		LocalDate today = LocalDate.now();

		for (Stock stock : stockList) {
			Product product = stock.getProduct();
			LocalDate expiryDate = stock.getExpiryDate();

			if (!today.isBefore(expiryDate)) {
				transitionStatus(stock, StockStatus.EXPIRED, "유통기한 도래: " + expiryDate);
				continue;
			}

			if (isWithinWarningDays(today, expiryDate, product.getWarningThresholdDays())
					|| isWithinWarningRemainingPct(
							today, stock.getMfgDate(), expiryDate, product.getWarningThresholdPct())) {
				transitionStatus(stock, StockStatus.WARNING, "유통기한 임박 (만료일: " + expiryDate + ")");
			}
		}
	}

	private boolean isWithinWarningDays(LocalDate today, LocalDate expiryDate, int warningThresholdDays) {
		long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
		return daysUntilExpiry <= warningThresholdDays;
	}

	private boolean isWithinWarningRemainingPct(
			LocalDate today, LocalDate mfgDate, LocalDate expiryDate, BigDecimal warningThresholdPct) {
		long totalShelfLifeDays = ChronoUnit.DAYS.between(mfgDate, expiryDate);
		if (totalShelfLifeDays <= 0) {
			return false;
		}
		long remainingDays = ChronoUnit.DAYS.between(today, expiryDate);
		BigDecimal remainingPct = BigDecimal.valueOf(remainingDays)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(totalShelfLifeDays), 1, RoundingMode.HALF_UP);
		return remainingPct.compareTo(warningThresholdPct) <= 0;
	}

	private void transitionStatus(Stock stock, StockStatus nextStatus, String reason) {
		if (stock.getStockStatus() == nextStatus) {
			return;
		}
		stock.updateStatus(nextStatus);
		saveMovement(stock, MovementType.ADJUSTMENT, stock.getAmount(), reason);
	}

	private void saveMovement(Stock stock, MovementType movementType, int quantity, String reason) {
		stockMovementRepository.save(
				StockMovement.builder()
						.stock(stock)
						.productId(stock.getProductId())
						.movementType(movementType)
						.quantity(quantity)
						.reason(reason)
						.build());
	}
}
