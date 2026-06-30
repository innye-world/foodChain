package com.inhye.foodChain.outbound.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출고", description = "출고 요청 및 응답 처리 API. 유통기한이 가장 빠른 순으로 처리합니다.")
@RestController
@RequestMapping("/api/outbound")
@RequiredArgsConstructor
public class OutboundApiController {
}
