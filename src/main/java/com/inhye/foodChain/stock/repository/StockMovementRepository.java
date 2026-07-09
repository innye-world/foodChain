package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.MovementType;
import com.inhye.foodChain.stock.domain.StockMovement;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

	@Query(
			value =
					"""
			SELECT m FROM StockMovement m
			JOIN FETCH m.stock s
			JOIN FETCH s.product
			ORDER BY m.createdAt DESC, m.movementId DESC
			""",
			countQuery = "SELECT count(m) FROM StockMovement m")
	Page<StockMovement> findAllOrderByCreatedAtDesc(Pageable pageable);

	@Query(
			"""
			SELECT m FROM StockMovement m
			JOIN FETCH m.stock s
			JOIN FETCH s.product
			ORDER BY m.createdAt DESC, m.movementId DESC
			""")
	List<StockMovement> findAllOrderByCreatedAtDesc();

	/** 대시보드 입출고 차트: 최근 7일 이동 이력 */
	@Query(
			"""
			SELECT m FROM StockMovement m
			WHERE m.createdAt >= :since
			AND m.movementType IN :types
			""")
	List<StockMovement> findMovementsSince(
			@Param("since") LocalDateTime since, @Param("types") List<MovementType> types);
}
