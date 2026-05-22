package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.Stock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

	/** FEFO: 유통기한 → 입고일 순 */
	@Query(
			"""
			SELECT s FROM Stock s
			JOIN FETCH s.product
			ORDER BY s.expiryDate ASC, s.receivedAt ASC, s.stockId ASC
			""")
	List<Stock> findAllOrderByFefo();
}
