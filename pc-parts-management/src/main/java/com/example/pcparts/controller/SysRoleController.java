package com.example.pcparts.controller;

import com.example.pcparts.common.ApiResponse;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.system.RoleCreateRequest;
import com.example.pcparts.dto.system.RoleUpdateRequest;
import com.example.pcparts.service.SysRoleService;
import com.example.pcparts.vo.system.RoleVO;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    public ApiResponse<PageResponse<RoleVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(sysRoleService.page(page, size, keyword));
    }

    @GetMapping("/all")
    public ApiResponse<List<RoleVO>> all() {
        return ApiResponse.ok(sysRoleService.listAll());
    }

    @PostMapping
    public ApiResponse<RoleVO> create(@Valid @RequestBody RoleCreateRequest request) {
        return ApiResponse.ok(sysRoleService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleVO> get(@PathVariable Long id) {
        return ApiResponse.ok(sysRoleService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleVO> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return ApiResponse.ok(sysRoleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return ApiResponse.ok(null);
    }
}
