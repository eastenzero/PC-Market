package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.brand.BrandCreateRequest;
import com.example.pcparts.dto.brand.BrandUpdateRequest;
import com.example.pcparts.entity.Brand;
import com.example.pcparts.mapper.BrandMapper;
import com.example.pcparts.vo.brand.BrandVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandMapper brandMapper;

    public PageResponse<BrandVO> page(long page, long size, String keyword) {
        Page<Brand> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Brand> query = new LambdaQueryWrapper<Brand>();
        if (StringUtils.hasText(keyword)) {
            query.like(Brand::getName, keyword);
        }
        query.orderByDesc(Brand::getId);
        Page<Brand> result = brandMapper.selectPage(mpPage, query);
        List<BrandVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public BrandVO getById(Long id) {
        Brand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(brand);
    }

    public BrandVO create(BrandCreateRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setStatus(request.getStatus());
        brandMapper.insert(brand);
        return getById(brand.getId());
    }

    public BrandVO update(Long id, BrandUpdateRequest request) {
        Brand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "not_found");
        }
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setStatus(request.getStatus());
        brandMapper.updateById(brand);
        return getById(id);
    }

    public void delete(Long id) {
        Brand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "not_found");
        }
        brandMapper.deleteById(id);
    }

    private BrandVO toVo(Brand b) {
        return new BrandVO(b.getId(), b.getName(), b.getDescription(), b.getStatus());
    }
}
