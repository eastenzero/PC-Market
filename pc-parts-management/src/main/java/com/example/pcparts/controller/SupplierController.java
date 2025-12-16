package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.supplier.SupplierCreateRequest;
import com.example.pcparts.dto.supplier.SupplierUpdateRequest;
import com.example.pcparts.service.SupplierService;
import com.example.pcparts.vo.supplier.SupplierVO;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ApiResponse<PageResponse<SupplierVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(supplierService.page(page, size, keyword));
    }

    @PostMapping
    public ApiResponse<SupplierVO> create(@Valid @RequestBody SupplierCreateRequest request) {
        return ApiResponse.ok(supplierService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SupplierVO> get(@PathVariable Long id) {
        return ApiResponse.ok(supplierService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplierVO> update(@PathVariable Long id, @Valid @RequestBody SupplierUpdateRequest request) {
        return ApiResponse.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ApiResponse.ok(null);
    }
}
