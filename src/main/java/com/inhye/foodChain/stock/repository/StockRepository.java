package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

	/** FEFO: 유통기한 → 입고일 순 */
	@Query(
			"""
			SELECT s FROM Stock s
			JOIN FETCH s.product
			ORDER BY s.expiryDate ASC, s.receivedAt ASC, s.stockId ASC
			""")
	List<Stock> findAllOrderByFefo();

	@Query(
			value =
					"""
			SELECT s FROM Stock s
			JOIN FETCH s.product
			ORDER BY s.expiryDate ASC, s.receivedAt ASC, s.stockId ASC
			""",
			countQuery = "SELECT count(s) FROM Stock s")
	Page<Stock> findAllOrderByFefo(Pageable pageable);

	@Query(
			value =
					"""
			SELECT s FROM Stock s
			JOIN FETCH s.product p
			JOIN p.productType pt
			WHERE (:typeCode IS NULL OR pt.typeCode = :typeCode)
			AND (:productId IS NULL OR p.productId = :productId)
			AND s.stockStatus IN :statuses
			ORDER BY s.expiryDate ASC, s.receivedAt ASC, s.stockId ASC
			""",
			countQuery =
					"""
			SELECT count(s) FROM Stock s
			JOIN s.product p
			JOIN p.productType pt
			WHERE (:typeCode IS NULL OR pt.typeCode = :typeCode)
			AND (:productId IS NULL OR p.productId = :productId)
			AND s.stockStatus IN :statuses
			""")
	Page<Stock> findByFilters(
			@Param("typeCode") String typeCode,
			@Param("productId") String productId,
			@Param("statuses") List<StockStatus> statuses,
			Pageable pageable);

	@Query(
			"""
			SELECT s FROM Stock s
			JOIN FETCH s.product p
			JOIN FETCH p.productType
			WHERE s.stockId = :stockId
			""")
	Optional<Stock> findByIdWithProduct(@Param("stockId") Long stockId);

	/** 유통기한 배치 대상: HOLD·종료 상태 제외, product는 threshold 조회용 */
	@Query(
			"""
			SELECT s FROM Stock s
			JOIN FETCH s.product
			WHERE s.stockStatus IN (
				com.inhye.foodChain.stock.domain.StockStatus.AVAILABLE,
				com.inhye.foodChain.stock.domain.StockStatus.WARNING
			)
			""")
	List<Stock> findActiveStocksForExpiryUpdate();

	/** 제품에 해당하는 재고 리스트 조회 */
	@Query(
			"""
			SELECT s FROM Stock s
			JOIN FETCH s.product p
			WHERE s.stockStatus IN (
				com.inhye.foodChain.stock.domain.StockStatus.AVAILABLE,
				com.inhye.foodChain.stock.domain.StockStatus.WARNING
			)
			AND p.productId = :productId
			ORDER BY CASE WHEN s.stockStatus = WARNING THEN 0 ELSE 1 END,
			s.expiryDate ASC, s.receivedAt ASC, s.stockId ASC
			""")
	List<Stock> findByproductId(@Param("productId") String productId);

	/** 대시보드에 들어갈 총 재고 수량 */
	@Query(
			"""
			SELECT s FROM Stock s
			WHERE s.stockStatus IN (
				com.inhye.foodChain.stock.domain.StockStatus.AVAILABLE,
				com.inhye.foodChain.stock.domain.StockStatus.WARNING
			)
			""")
	List<Stock> findAllAvailableAndWarningStocks();

	/** 대시보드에 들어갈 오늘 입고 배치 */
	@Query(
			"""
			SELECT s FROM Stock s
			WHERE s.receivedAt >= :dayStart
			AND s.receivedAt < :dayEnd
			""")
	List<Stock> findBatchCountOfToday(
			@Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

	/** 대시보드: 유형별 현재 재고 수량 합계 */
	@Query(
			"""
			SELECT pt.typeName, SUM(s.amount)
			FROM Stock s
			JOIN s.product p
			JOIN p.productType pt
			WHERE s.stockStatus IN (
				com.inhye.foodChain.stock.domain.StockStatus.AVAILABLE,
				com.inhye.foodChain.stock.domain.StockStatus.WARNING
			)
			GROUP BY pt.productTypeId, pt.typeName
			ORDER BY pt.typeName
			""")
	List<Object[]> sumAmountGroupByProductType();
}
