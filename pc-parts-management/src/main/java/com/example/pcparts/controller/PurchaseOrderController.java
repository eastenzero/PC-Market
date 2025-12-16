package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.purchase.PurchaseOrderCreateRequest;
import com.example.pcparts.dto.purchase.PurchaseReceiveRequest;
import com.example.pcparts.service.PurchaseOrderService;
import com.example.pcparts.vo.purchase.PurchaseOrderVO;
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
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ApiResponse<PurchaseOrderVO> create(@Valid @RequestBody PurchaseOrderCreateRequest request) {
        return ApiResponse.ok(purchaseOrderService.create(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<PurchaseOrderVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.ok(purchaseOrderService.page(page, size, keyword, supplierId, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<PurchaseOrderVO> get(@PathVariable Long id) {
        return ApiResponse.ok(purchaseOrderService.getById(id));
    }

    @PostMapping("/{id}/receive")
    public ApiResponse<PurchaseOrderVO> receive(@PathVariable Long id, @Valid @RequestBody PurchaseReceiveRequest request) {
        return ApiResponse.ok(purchaseOrderService.receive(id, request));
    }
}
