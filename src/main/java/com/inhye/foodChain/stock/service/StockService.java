package com.inhye.foodChain.stock.service;

import com.inhye.foodChain.common.exception.ResourceNotFoundException;
import com.inhye.foodChain.common.web.PaginationConstants;
import com.inhye.foodChain.dashboard.dto.InoutChartResponse;
import com.inhye.foodChain.dashboard.dto.StockRatioByProductResponse;
import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.stock.domain.MovementType;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockListSort;
import com.inhye.foodChain.stock.domain.StockMovement;
import com.inhye.foodChain.stock.domain.StockStatus;
import com.inhye.foodChain.stock.repository.StockMovementRepository;
import com.inhye.foodChain.stock.repository.StockRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;
	private final StockMovementRepository stockMovementRepository;
	private final ProductRepository productRepository;

	// 대시보드 차트
	private static final DateTimeFormatter CHART_LABEL_FORMAT = DateTimeFormatter.ofPattern("MM-dd");
	private static final DateTimeFormatter LOT_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
	private static final List<MovementType> CHART_MOVEMENT_TYPES = List.of(MovementType.INBOUND, MovementType.OUTBOUND, MovementType.DISPOSAL);
	private static final Map<MovementType, String> CHART_COLORS = Map.of(
			MovementType.INBOUND, "#22c55e",
			MovementType.OUTBOUND, "#3b82f6",
			MovementType.DISPOSAL, "#ef4444");
	private static final List<String> TYPE_CHART_PALETTE = List.of(
			"#3b82f6", "#22c55e", "#f59e0b", "#9ca3af", "#8b5cf6", "#ec4899", "#14b8a6", "#f97316");

	@Transactional(readOnly = true)
	public List<Stock> findAllStocksOrderByFefo() {
		return stockRepository.findAllOrderByFefo();
	}

	@Transactional(readOnly = true)
	public Page<Stock> findStocksPage(int page) {
		return findStocksPage(page, null, null, null, StockListSort.FEFO);
	}

	@Transactional(readOnly = true)
	public Page<Stock> findStocksPage(int page, String typeCode, String productId, String status) {
		return findStocksPage(page, typeCode, productId, status, StockListSort.FEFO);
	}

	@Transactional(readOnly = true)
	public Page<Stock> findStocksPage(
			int page, String typeCode, String productId, String status, StockListSort sort) {
		var pageable = PageRequest.of(Math.max(page, 0), PaginationConstants.PAGE_SIZE);
		var resolvedTypeCode = blankToNull(typeCode);
		var resolvedProductId = blankToNull(productId);
		var statuses = resolveStatuses(status);
		if (sort == StockListSort.RECEIVED) {
			return stockRepository.findByFiltersOrderByReceivedDesc(
					resolvedTypeCode, resolvedProductId, statuses, pageable);
		}
		return stockRepository.findByFilters(resolvedTypeCode, resolvedProductId, statuses, pageable);
	}

	private static List<StockStatus> resolveStatuses(String status) {
		if (status == null || status.isBlank()) {
			return List.of(StockStatus.AVAILABLE, StockStatus.WARNING, StockStatus.HOLD);
		}
		return List.of(StockStatus.valueOf(status.trim()));
	}

	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	@Transactional(readOnly = true)
	public List<StockMovement> findAllMovementsOrderByCreatedAtDesc() {
		return stockMovementRepository.findAllOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public Page<StockMovement> findMovementsPage(int page) {
		return stockMovementRepository.findAllOrderByCreatedAtDesc(
				PageRequest.of(Math.max(page, 0), PaginationConstants.PAGE_SIZE));
	}

	@Transactional(readOnly = true)
	public Stock findStockById(Long stockId) {
		return stockRepository.findByIdWithProduct(stockId)
				.orElseThrow(() -> new ResourceNotFoundException("재고를 찾을 수 없습니다: " + stockId));
	}

	/**
	 * 입고 등록 시에는 무조건 STOCK, STOCK_MOVEMENT 테이블 모두에 데이터를 넣는다.
	 * LOT 번호는 상품·당일 기준 서버 자동 채번.
	 */
	@Transactional
	public Stock registerStock(
			String productId,
			LocalDate mfgDate,
			LocalDate expiryDate,
			int amount,
			BigDecimal currentTemperature,
			String inboundToken) {
		String token = blankToNull(inboundToken);
		if (token != null && stockRepository.existsByInboundToken(token)) {
			throw new IllegalStateException("이미 처리한 배치입니다.");
		}

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다: " + productId));

		// QR·수동 공통: 저장 직전 서버에서 LOT 채번 (클라이언트가 보낸 값 신뢰하지 않음)
		String lotNo = generateLotNo(productId);

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
								.inboundToken(token)
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
	 * 입고 LOT 자동 생성.
	 * 형식: LOT-{yyyyMMdd}-{일련번호 3자리} (상품·일자 기준, 기존 최대 번호 + 1)
	 */
	@Transactional(readOnly = true)
	public String generateLotNo(String productId) {
		String datePart = LocalDate.now().format(LOT_DATE_FORMAT);
		String prefix = "LOT-" + datePart + "-";
		List<String> existingLotNos =
				stockRepository.findLotNosByProductIdAndLotNoStartingWith(productId, prefix + "%");

		int maxSeq = 0;
		for (String existingLotNo : existingLotNos) {
			if (existingLotNo == null || !existingLotNo.startsWith(prefix)) {
				continue;
			}
			String seqPart = existingLotNo.substring(prefix.length());
			if (seqPart.length() != 3) {
				continue;
			}
			try {
				maxSeq = Math.max(maxSeq, Integer.parseInt(seqPart));
			} catch (NumberFormatException ignored) {
				// 수동으로 넣은 비정형 LOT는 채번에서 제외
			}
		}

		int nextSeq = maxSeq + 1;
		if (nextSeq > 999) {
			throw new IllegalStateException("오늘 해당 상품의 LOT 일련번호 한도를 초과했습니다: " + productId);
		}
		return prefix + String.format("%03d", nextSeq);
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

	/**
	 * HOLD → AVAILABLE (유통기한 경과 시 EXPIRED). 수량 변동 없음.
	 */
	@Transactional
	public Stock releaseStock(Long stockId) {
		Stock stock = findHoldStock(stockId);
		LocalDate today = LocalDate.now();

		if (!today.isBefore(stock.getExpiryDate())) {
			stock.updateStatus(StockStatus.EXPIRED);
			saveMovement(stock, MovementType.RELEASE, 0, "보류 해제 시 유통기한 경과: " + stock.getExpiryDate());
		} else {
			stock.updateStatus(StockStatus.AVAILABLE);
			saveMovement(stock, MovementType.RELEASE, 0, "품질 보류 해제");
		}
		return stock;
	}

	/** HOLD → DISPOSED, 수량 0. */
	@Transactional
	public Stock disposeStock(Long stockId) {
		Stock stock = findHoldStock(stockId);
		int disposedQuantity = stock.getAmount();

		stock.setStockStatus(StockStatus.DISPOSED);
		stock.setAmount(0);
		saveMovement(stock, MovementType.DISPOSAL, -disposedQuantity, "보류 배치 폐기");
		return stock;
	}

	private Stock findHoldStock(Long stockId) {
		Stock stock = stockRepository.findByIdWithProduct(stockId)
				.orElseThrow(() -> new ResourceNotFoundException("재고를 찾을 수 없습니다: " + stockId));
		if (stock.getStockStatus() != StockStatus.HOLD) {
			throw new IllegalStateException("HOLD 상태인 배치만 처리 가능합니다.");
		}
		return stock;
	}

	public List<Stock> findAllAvailableAndWarningStocks(){
		return stockRepository.findAllAvailableAndWarningStocks();
	}

	public int findStockCountOfToday(){
		LocalDateTime dayStart = LocalDate.now().atStartOfDay();
		LocalDateTime dayEnd = dayStart.plusDays(1);
		int stockCount = 0;
		List<Stock> batchList = stockRepository.findBatchCountOfToday(dayStart, dayEnd);
		for(Stock batch : batchList){
			stockCount += batch.getAmount();
		}
		return stockCount;
	}

	@Transactional(readOnly = true)
	public InoutChartResponse getInoutChartData() {
		LocalDate startDate = LocalDate.now().minusDays(6);
		List<String> labels = startDate.datesUntil(startDate.plusDays(7))
				.map(date -> date.format(CHART_LABEL_FORMAT))
				.toList();

		int[][] totalsByType = new int[CHART_MOVEMENT_TYPES.size()][7];
		for (StockMovement movement : stockMovementRepository.findMovementsSince(
				startDate.atStartOfDay(), CHART_MOVEMENT_TYPES)) {
			int typeIdx = CHART_MOVEMENT_TYPES.indexOf(movement.getMovementType());
			int dayIdx = (int) ChronoUnit.DAYS.between(startDate, movement.getCreatedAt().toLocalDate());
			if (typeIdx < 0 || dayIdx < 0 || dayIdx >= 7) {
				continue;
			}
			totalsByType[typeIdx][dayIdx] += Math.abs(movement.getQuantity());
		}

		List<InoutChartResponse.Dataset> datasets = IntStream.range(0, CHART_MOVEMENT_TYPES.size())
				.mapToObj(i -> {
					MovementType type = CHART_MOVEMENT_TYPES.get(i);
					return new InoutChartResponse.Dataset(
							type.getDisplayName(),
							Arrays.stream(totalsByType[i]).boxed().toList(),
							CHART_COLORS.get(type));
				})
				.toList();

		return new InoutChartResponse(labels, datasets);
	}

	@Transactional(readOnly = true)
	public StockRatioByProductResponse getStockRatioByProductTypeData() {
		List<Object[]> rows = stockRepository.sumAmountGroupByProductType();

		List<String> labels = rows.stream().map(row -> (String) row[0]).toList();
		List<Integer> data = rows.stream().map(row -> ((Number) row[1]).intValue()).toList();
		List<String> colors = IntStream.range(0, labels.size())
				.mapToObj(i -> TYPE_CHART_PALETTE.get(i % TYPE_CHART_PALETTE.size()))
				.toList();

		return new StockRatioByProductResponse(
				labels,
				List.of(new StockRatioByProductResponse.Dataset(data, colors)));
	}
}
