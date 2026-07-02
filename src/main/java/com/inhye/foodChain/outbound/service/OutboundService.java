package com.inhye.foodChain.outbound.service;

import com.inhye.foodChain.outbound.dto.LotAllocationResponse;
import com.inhye.foodChain.outbound.dto.OutboundOrderRequest;
import com.inhye.foodChain.outbound.dto.OutboundOrderResponse;
import com.inhye.foodChain.stock.domain.Stock;
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

    @Transactional
    public OutboundOrderResponse processOutboundOrder(OutboundOrderRequest request) {
        String productId = request.productId();
        int requestedAmount = request.requestAmount();

        List<Stock> lotsList = stockRepository.findByproductId(productId);
        List<LotAllocationResponse> allocations = new ArrayList<>();

        String message = "재고부족으로 출고가 불가합니다.";
        int fulfilledAmount = 0;
        if(lotsList.size() > 0){
            int totalStockAmount = 0;
            for(Stock lot : lotsList) {
                totalStockAmount += lot.getAmount();
            }
            // 1. 재고 수량이 출고 요청 수량 이상일 때만 계산
            if(totalStockAmount >= requestedAmount) {
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

                        // 해당 로트 개수 업데이트
                    }

                    allocations.add(new LotAllocationResponse(
                            lotsList.get(i).getLotNo(),
                            allocateAmount,
                            lotsList.get(i).getExpiryDate()));
                }
                message = "출고 접수 완료";

                return new OutboundOrderResponse(
                        request.orderId(),
                        productId,
                        request.requestAmount(),
                        request.requestAmount(),
                        allocations,
                        message);
            }
        }

        // OutboundOrder, OutboundDetail 테이블에 저장
        // StockMovement 테이블에 저장
        
        return new OutboundOrderResponse(
                request.orderId(),
                productId,
                request.requestAmount(),
                fulfilledAmount,
                List.of(),
                message);
    }
}
