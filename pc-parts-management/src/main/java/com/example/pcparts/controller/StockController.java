package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.stock.StockInRequest;
import com.example.pcparts.dto.stock.StockOutRequest;
import com.example.pcparts.service.StockService;
import com.example.pcparts.vo.stock.InventoryMovementVO;
import com.example.pcparts.vo.stock.StockVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks")
    public ApiResponse<PageResponse<StockVO>> stocks(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(stockService.pageStocks(page, size, warehouseId, skuId, keyword));
    }

    @PostMapping("/stock/in")
    public ApiResponse<StockVO> stockIn(@Valid @RequestBody StockInRequest request) {
        return ApiResponse.ok(stockService.stockIn(request));
    }

    @PostMapping("/stock/out")
    public ApiResponse<StockVO> stockOut(@Valid @RequestBody StockOutRequest request) {
        return ApiResponse.ok(stockService.stockOut(request));
    }

    @GetMapping("/stock/movements")
    public ApiResponse<PageResponse<InventoryMovementVO>> movements(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.ok(stockService.pageMovements(page, size));
    }
}
