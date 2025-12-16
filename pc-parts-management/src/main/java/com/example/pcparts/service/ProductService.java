package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.product.ProductCreateRequest;
import com.example.pcparts.dto.product.ProductUpdateRequest;
import com.example.pcparts.entity.Brand;
import com.example.pcparts.entity.Product;
import com.example.pcparts.entity.ProductCategory;
import com.example.pcparts.mapper.BrandMapper;
import com.example.pcparts.mapper.ProductCategoryMapper;
import com.example.pcparts.mapper.ProductMapper;
import com.example.pcparts.vo.product.ProductVO;
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
public class ProductService {

    private final ProductMapper productMapper;
    private final BrandMapper brandMapper;
    private final ProductCategoryMapper productCategoryMapper;

    public PageResponse<ProductVO> page(long page, long size, String keyword, Long categoryId, Long brandId) {
        Page<Product> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<Product>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getSpuCode, keyword));
        }
        if (categoryId != null) {
            query.eq(Product::getCategoryId, categoryId);
        }
        if (brandId != null) {
            query.eq(Product::getBrandId, brandId);
        }
        query.orderByDesc(Product::getId);
        Page<Product> result = productMapper.selectPage(mpPage, query);

        List<Product> records = result.getRecords();
        Map<Long, String> brandNames = loadBrandNames(records);
        Map<Long, String> categoryNames = loadCategoryNames(records);

        List<ProductVO> items = records.stream()
                .map(p -> toVo(p, brandNames.get(p.getBrandId()), categoryNames.get(p.getCategoryId())))
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public ProductVO getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "not_found");
        }
        String brandName = null;
        String categoryName = null;
        if (product.getBrandId() != null) {
            Brand brand = brandMapper.selectById(product.getBrandId());
            if (brand != null) {
                brandName = brand.getName();
            }
        }
        if (product.getCategoryId() != null) {
            ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }
        return toVo(product, brandName, categoryName);
    }

    public ProductVO create(ProductCreateRequest request) {
        Product product = new Product();
        product.setSpuCode(request.getSpuCode());
        product.setName(request.getName());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setDescription(request.getDescription());
        product.setStatus(request.getStatus());
        productMapper.insert(product);
        return getById(product.getId());
    }

    public ProductVO update(Long id, ProductUpdateRequest request) {
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "not_found");
        }
        existing.setSpuCode(request.getSpuCode());
        existing.setName(request.getName());
        existing.setCategoryId(request.getCategoryId());
        existing.setBrandId(request.getBrandId());
        existing.setDescription(request.getDescription());
        existing.setStatus(request.getStatus());
        productMapper.updateById(existing);
        return getById(id);
    }

    public void delete(Long id) {
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "not_found");
        }
        productMapper.deleteById(id);
    }

    private Map<Long, String> loadBrandNames(List<Product> products) {
        Set<Long> ids = products.stream()
                .map(Product::getBrandId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<Brand> brands = brandMapper.selectBatchIds(ids);
        Map<Long, String> map = new HashMap<>();
        for (Brand b : brands) {
            map.put(b.getId(), b.getName());
        }
        return map;
    }

    private Map<Long, String> loadCategoryNames(List<Product> products) {
        Set<Long> ids = products.stream()
                .map(Product::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<ProductCategory> categories = productCategoryMapper.selectBatchIds(ids);
        Map<Long, String> map = new HashMap<>();
        for (ProductCategory c : categories) {
            map.put(c.getId(), c.getName());
        }
        return map;
    }

    private ProductVO toVo(Product p, String brandName, String categoryName) {
        return new ProductVO(
                p.getId(),
                p.getSpuCode(),
                p.getName(),
                p.getCategoryId(),
                categoryName,
                p.getBrandId(),
                brandName,
                p.getDescription(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
