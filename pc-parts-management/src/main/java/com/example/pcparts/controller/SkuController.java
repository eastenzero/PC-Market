package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.sku.SkuCreateRequest;
import com.example.pcparts.dto.sku.SkuUpdateRequest;
import com.example.pcparts.dto.sku.SkuUpdateStatusRequest;
import com.example.pcparts.service.SkuService;
import com.example.pcparts.vo.sku.SkuVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skus")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping
    public ApiResponse<PageResponse<SkuVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long productId
    ) {
        return ApiResponse.ok(skuService.page(page, size, keyword, productId));
    }

    @PostMapping
    public ApiResponse<SkuVO> create(@Valid @RequestBody SkuCreateRequest request) {
        return ApiResponse.ok(skuService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SkuVO> get(@PathVariable Long id) {
        return ApiResponse.ok(skuService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<SkuVO> update(@PathVariable Long id, @Valid @RequestBody SkuUpdateRequest request) {
        return ApiResponse.ok(skuService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<SkuVO> updateStatus(@PathVariable Long id, @Valid @RequestBody SkuUpdateStatusRequest request) {
        return ApiResponse.ok(skuService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        skuService.delete(id);
        return ApiResponse.ok(null);
    }
}
