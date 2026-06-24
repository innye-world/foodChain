package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.Stock;
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
}
