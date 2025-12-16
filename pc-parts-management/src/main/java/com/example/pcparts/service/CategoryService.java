package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.category.CategoryCreateRequest;
import com.example.pcparts.dto.category.CategoryUpdateRequest;
import com.example.pcparts.entity.ProductCategory;
import com.example.pcparts.mapper.ProductCategoryMapper;
import com.example.pcparts.vo.category.CategoryVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    public PageResponse<CategoryVO> page(long page, long size, String keyword, Long parentId) {
        Page<ProductCategory> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<ProductCategory> query = new LambdaQueryWrapper<ProductCategory>();
        if (StringUtils.hasText(keyword)) {
            query.like(ProductCategory::getName, keyword);
        }
        if (parentId != null) {
            query.eq(ProductCategory::getParentId, parentId);
        }
        query.orderByAsc(ProductCategory::getSortOrder).orderByDesc(ProductCategory::getId);
        Page<ProductCategory> result = productCategoryMapper.selectPage(mpPage, query);
        List<CategoryVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public CategoryVO getById(Long id) {
        ProductCategory category = productCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(category);
    }

    public CategoryVO create(CategoryCreateRequest request) {
        ProductCategory category = new ProductCategory();
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        fillDerivedFields(category);

        productCategoryMapper.insert(category);
        return getById(category.getId());
    }

    public CategoryVO update(Long id, CategoryUpdateRequest request) {
        ProductCategory category = productCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "not_found");
        }
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        fillDerivedFields(category);

        productCategoryMapper.updateById(category);
        return getById(id);
    }

    public void delete(Long id) {
        ProductCategory category = productCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "not_found");
        }
        productCategoryMapper.deleteById(id);
    }

    private void fillDerivedFields(ProductCategory category) {
        if (category.getParentId() == null) {
            category.setLevel(1);
            category.setPath("/" + category.getName());
            return;
        }
        ProductCategory parent = productCategoryMapper.selectById(category.getParentId());
        if (parent == null) {
            throw new BusinessException(400, "parent_not_found");
        }
        Integer parentLevel = parent.getLevel() == null ? 1 : parent.getLevel();
        category.setLevel(parentLevel + 1);
        String parentPath = parent.getPath() == null ? "" : parent.getPath();
        category.setPath(parentPath + "/" + category.getName());
    }

    private CategoryVO toVo(ProductCategory c) {
        return new CategoryVO(c.getId(), c.getParentId(), c.getName(), c.getSortOrder(), c.getLevel(), c.getPath());
    }
}
