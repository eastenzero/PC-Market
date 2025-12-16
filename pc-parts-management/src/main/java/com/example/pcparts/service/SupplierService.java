package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.supplier.SupplierCreateRequest;
import com.example.pcparts.dto.supplier.SupplierUpdateRequest;
import com.example.pcparts.entity.Supplier;
import com.example.pcparts.mapper.SupplierMapper;
import com.example.pcparts.vo.supplier.SupplierVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierMapper supplierMapper;

    public PageResponse<SupplierVO> page(long page, long size, String keyword) {
        Page<Supplier> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Supplier> query = new LambdaQueryWrapper<Supplier>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(Supplier::getName, keyword)
                    .or()
                    .like(Supplier::getPhone, keyword));
        }
        query.orderByDesc(Supplier::getId);
        Page<Supplier> result = supplierMapper.selectPage(mpPage, query);
        List<SupplierVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public SupplierVO getById(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(supplier);
    }

    public SupplierVO create(SupplierCreateRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setStatus(request.getStatus());
        supplierMapper.insert(supplier);
        return getById(supplier.getId());
    }

    public SupplierVO update(Long id, SupplierUpdateRequest request) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "not_found");
        }
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setStatus(request.getStatus());
        supplierMapper.updateById(supplier);
        return getById(id);
    }

    public void delete(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "not_found");
        }
        supplierMapper.deleteById(id);
    }

    private SupplierVO toVo(Supplier s) {
        return new SupplierVO(s.getId(), s.getName(), s.getPhone(), s.getEmail(), s.getAddress(), s.getStatus());
    }
}
