package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.StockMovement;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
