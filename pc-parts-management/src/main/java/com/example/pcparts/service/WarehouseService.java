package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.warehouse.WarehouseCreateRequest;
import com.example.pcparts.dto.warehouse.WarehouseUpdateRequest;
import com.example.pcparts.entity.Warehouse;
import com.example.pcparts.mapper.WarehouseMapper;
import com.example.pcparts.vo.warehouse.WarehouseVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseMapper warehouseMapper;

    public PageResponse<WarehouseVO> page(long page, long size, String keyword) {
        Page<Warehouse> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Warehouse> query = new LambdaQueryWrapper<Warehouse>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(Warehouse::getName, keyword)
                    .or()
                    .like(Warehouse::getCode, keyword));
        }
        query.orderByDesc(Warehouse::getId);
        Page<Warehouse> result = warehouseMapper.selectPage(mpPage, query);
        List<WarehouseVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public WarehouseVO getById(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(warehouse);
    }

    public WarehouseVO create(WarehouseCreateRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setContactName(request.getContactName());
        warehouse.setContactPhone(request.getContactPhone());
        warehouse.setStatus(request.getStatus());
        warehouseMapper.insert(warehouse);
        return getById(warehouse.getId());
    }

    public WarehouseVO update(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException(404, "not_found");
        }
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setContactName(request.getContactName());
        warehouse.setContactPhone(request.getContactPhone());
        warehouse.setStatus(request.getStatus());
        warehouseMapper.updateById(warehouse);
        return getById(id);
    }

    public void delete(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException(404, "not_found");
        }
        warehouseMapper.deleteById(id);
    }

    private WarehouseVO toVo(Warehouse w) {
        return new WarehouseVO(w.getId(), w.getCode(), w.getName(), w.getAddress(), w.getContactName(), w.getContactPhone(), w.getStatus());
    }
}
