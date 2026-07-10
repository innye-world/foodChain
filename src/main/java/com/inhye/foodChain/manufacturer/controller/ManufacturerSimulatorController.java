package com.inhye.foodChain.manufacturer.controller;

import com.inhye.foodChain.manufacturer.dto.ManufacturerQrPayload;
import com.inhye.foodChain.manufacturer.service.ManufacturerSimulatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 제조사 서비스라 가정$
 *
 * @author 류인혜
 * @since 2026. 7. 10.$
 */
@Tag(name = "제조사 QR 코드 페이지", description = "테스트 데이터를 만드는 것이므로 상품 테이블에서 임의의 상품ID 추출")
@Controller
@RequestMapping("/manufacturer")
@RequiredArgsConstructor
public class ManufacturerSimulatorController {
    private final ManufacturerSimulatorService manufacturerSimulatorService;

    @GetMapping("/qr")
    public String qrPage(Model model) {
        String qrDomain = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ManufacturerQrPayload payload = manufacturerSimulatorService.generateRandomQrPayload();

        String qrUrl = qrDomain + "/stock/add-temperature"
                + "?productId=" + payload.productId()
                + "&mfgDate=" + payload.mfgDate()
                + "&expiryDate=" + payload.expiryDate()
                + "&amount=" + payload.amount();

        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("payload", payload);
        return "manufacturer/qr";
    }
}
