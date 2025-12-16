package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.warehouse.WarehouseCreateRequest;
import com.example.pcparts.dto.warehouse.WarehouseUpdateRequest;
import com.example.pcparts.service.WarehouseService;
import com.example.pcparts.vo.warehouse.WarehouseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ApiResponse<PageResponse<WarehouseVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(warehouseService.page(page, size, keyword));
    }

    @PostMapping
    public ApiResponse<WarehouseVO> create(@Valid @RequestBody WarehouseCreateRequest request) {
        return ApiResponse.ok(warehouseService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseVO> get(@PathVariable Long id) {
        return ApiResponse.ok(warehouseService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseVO> update(@PathVariable Long id, @Valid @RequestBody WarehouseUpdateRequest request) {
        return ApiResponse.ok(warehouseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ApiResponse.ok(null);
    }
}
