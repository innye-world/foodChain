package com.inhye.foodChain.outbound.service;

import com.inhye.foodChain.common.exception.ResourceNotFoundException;
import com.inhye.foodChain.outbound.dto.LotAllocationResponse;
import com.inhye.foodChain.outbound.dto.OutboundDetail;
import com.inhye.foodChain.outbound.dto.OutboundOrder;
import com.inhye.foodChain.outbound.dto.OutboundOrderRequest;
import com.inhye.foodChain.outbound.dto.OutboundOrderResponse;
import com.inhye.foodChain.outbound.repository.OutboundDetailRepository;
import com.inhye.foodChain.outbound.repository.OutboundOrderRepository;
import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.product.repository.ProductRepository;
import com.inhye.foodChain.stock.domain.MovementType;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockMovement;
import com.inhye.foodChain.stock.repository.StockMovementRepository;
import com.inhye.foodChain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 출고 서비스$
 *
 * @author 류인혜
 * @since 2026. 7. 1.$
 */
@Service
@RequiredArgsConstructor
public class OutboundService {
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundDetailRepository outboundDetailRepository;

    @Transactional
    public OutboundOrderResponse processOutboundOrder(OutboundOrderRequest request) {
        String productId = request.productId();
        String orderId = request.orderId();
        int requestedAmount = request.requestAmount();

		// 이미 처리한 주문id 일 때
        if (outboundOrderRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("이미 처리된 주문입니다: " + orderId);
        }

        List<Stock> lotsList = stockRepository.findByproductId(productId);
        int totalStockAmount = 0;
        for (Stock lot : lotsList) {
            totalStockAmount += lot.getAmount();
        }
        // 1. 재고 수량이 출고 요청 수량 미만이면 예외 처리
        if (totalStockAmount < requestedAmount) {
            throw new IllegalStateException("재고 부족으로 출고가 불가합니다.");
        }

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다: " + productId));

        OutboundOrder outboundOrder = outboundOrderRepository.save(
                OutboundOrder.builder()
                        .orderId(orderId)
                        .product(product)
                        .requestAmount(request.requestAmount())
                        .requestedBy(request.requestedBy())
                        .build());

        List<LotAllocationResponse> allocations = new ArrayList<>();

        // 2. 요청 수량에 맞을 때까지 배치 정보를 LotAllocationResponse에 담는다.
        for (int i = 0; i < lotsList.size(); i++) {
            int lotAmount = lotsList.get(i).getAmount();
            int allocateAmount = 0;

            // 더이상 할당할 양이 없으면 루프 끝낸다.
            if (requestedAmount == 0) {
                break;
            }

            // 현재 LOT로 부족하면 다음 로트를 간다.
            if (requestedAmount >= lotAmount) {
                requestedAmount -= lotAmount;
                allocateAmount = lotAmount;
                lotsList.get(i).setAmount(0);
            } else {
                allocateAmount = requestedAmount;
                lotsList.get(i).setAmount(lotAmount - requestedAmount);
                requestedAmount = 0;
            }

            outboundOrder.addFulfilledAmount(allocateAmount);
            outboundDetailRepository.save(
                    OutboundDetail.builder()
                            .outboundOrder(outboundOrder)
                            .stock(lotsList.get(i))
                            .outboundAmount(allocateAmount)
                            .build());
            stockMovementRepository.save(
                    StockMovement.builder()
                            .stock(lotsList.get(i))
                            .productId(productId)
                            .movementType(MovementType.OUTBOUND)
                            .quantity(-allocateAmount)
                            .reason("출고: " + request.orderId())
                            .build());

            allocations.add(new LotAllocationResponse(
                    lotsList.get(i).getLotNo(),
                    allocateAmount,
                    lotsList.get(i).getExpiryDate()));
        }

        return new OutboundOrderResponse(
                request.orderId(),
                productId,
                request.requestAmount(),
                outboundOrder.getFulfilledAmount(),
                allocations,
                "출고 접수 완료");
    }
}
