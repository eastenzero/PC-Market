package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.sales.SalesOrderCreateRequest;
import com.example.pcparts.dto.sales.SalesPayRequest;
import com.example.pcparts.dto.sales.SalesShipRequest;
import com.example.pcparts.service.SalesOrderService;
import com.example.pcparts.vo.sales.SalesOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ApiResponse<SalesOrderVO> create(@Valid @RequestBody SalesOrderCreateRequest request) {
        return ApiResponse.ok(salesOrderService.create(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<SalesOrderVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.ok(salesOrderService.page(page, size, keyword, customerId, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<SalesOrderVO> get(@PathVariable Long id) {
        return ApiResponse.ok(salesOrderService.getById(id));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<SalesOrderVO> pay(@PathVariable Long id, @Valid @RequestBody SalesPayRequest request) {
        return ApiResponse.ok(salesOrderService.pay(id, request));
    }

    @PostMapping("/{id}/ship")
    public ApiResponse<SalesOrderVO> ship(@PathVariable Long id, @Valid @RequestBody SalesShipRequest request) {
        return ApiResponse.ok(salesOrderService.ship(id, request));
    }
}
