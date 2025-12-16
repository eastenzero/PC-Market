package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.sku.SkuCreateRequest;
import com.example.pcparts.dto.sku.SkuUpdateRequest;
import com.example.pcparts.entity.Product;
import com.example.pcparts.entity.ProductSku;
import com.example.pcparts.mapper.ProductMapper;
import com.example.pcparts.mapper.ProductSkuMapper;
import com.example.pcparts.vo.sku.SkuVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SkuService {

    private final ProductSkuMapper productSkuMapper;
    private final ProductMapper productMapper;

    public PageResponse<SkuVO> page(long page, long size, String keyword, Long productId) {
        Page<ProductSku> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<ProductSku> query = new LambdaQueryWrapper<ProductSku>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(ProductSku::getName, keyword)
                    .or()
                    .like(ProductSku::getSkuCode, keyword)
                    .or()
                    .like(ProductSku::getBarcode, keyword));
        }
        if (productId != null) {
            query.eq(ProductSku::getProductId, productId);
        }
        query.orderByDesc(ProductSku::getId);
        Page<ProductSku> result = productSkuMapper.selectPage(mpPage, query);

        List<ProductSku> records = result.getRecords();
        Map<Long, String> productNames = loadProductNames(records);

        List<SkuVO> items = records.stream()
                .map(s -> toVo(s, productNames.get(s.getProductId())))
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public SkuVO create(SkuCreateRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(400, "product_not_found");
        }
        ProductSku sku = new ProductSku();
        sku.setProductId(request.getProductId());
        sku.setSkuCode(request.getSkuCode());
        sku.setBarcode(request.getBarcode());
        sku.setName(request.getName());
        sku.setSpecJson(request.getSpecJson());
        sku.setPrice(request.getPrice());
        sku.setCost(request.getCost());
        sku.setStatus(request.getStatus());
        productSkuMapper.insert(sku);
        return getById(sku.getId());
    }

    public SkuVO update(Long id, SkuUpdateRequest request) {
        ProductSku existing = productSkuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "not_found");
        }
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(400, "product_not_found");
        }
        existing.setProductId(request.getProductId());
        existing.setSkuCode(request.getSkuCode());
        existing.setBarcode(request.getBarcode());
        existing.setName(request.getName());
        existing.setSpecJson(request.getSpecJson());
        existing.setPrice(request.getPrice());
        existing.setCost(request.getCost());
        existing.setStatus(request.getStatus());
        productSkuMapper.updateById(existing);
        return getById(id);
    }

    public SkuVO updateStatus(Long id, Integer status) {
        ProductSku existing = productSkuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "not_found");
        }
        existing.setStatus(status);
        productSkuMapper.updateById(existing);
        return getById(id);
    }

    public void delete(Long id) {
        ProductSku existing = productSkuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "not_found");
        }
        productSkuMapper.deleteById(id);
    }

    public SkuVO getById(Long id) {
        ProductSku sku = productSkuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(404, "not_found");
        }
        String productName = null;
        if (sku.getProductId() != null) {
            Product product = productMapper.selectById(sku.getProductId());
            if (product != null) {
                productName = product.getName();
            }
        }
        return toVo(sku, productName);
    }

    private Map<Long, String> loadProductNames(List<ProductSku> skus) {
        Set<Long> ids = skus.stream()
                .map(ProductSku::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<Product> products = productMapper.selectBatchIds(ids);
        Map<Long, String> map = new HashMap<>();
        for (Product p : products) {
            map.put(p.getId(), p.getName());
        }
        return map;
    }

    private SkuVO toVo(ProductSku s, String productName) {
        return new SkuVO(
                s.getId(),
                s.getProductId(),
                productName,
                s.getSkuCode(),
                s.getBarcode(),
                s.getName(),
                s.getSpecJson(),
                s.getPrice(),
                s.getCost(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
