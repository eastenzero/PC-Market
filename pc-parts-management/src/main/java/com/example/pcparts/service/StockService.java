package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.stock.StockInRequest;
import com.example.pcparts.dto.stock.StockOutRequest;
import com.example.pcparts.entity.InventoryMovement;
import com.example.pcparts.entity.InventoryStock;
import com.example.pcparts.entity.Product;
import com.example.pcparts.entity.ProductSku;
import com.example.pcparts.entity.Warehouse;
import com.example.pcparts.mapper.InventoryMovementMapper;
import com.example.pcparts.mapper.InventoryStockMapper;
import com.example.pcparts.mapper.ProductMapper;
import com.example.pcparts.mapper.ProductSkuMapper;
import com.example.pcparts.mapper.WarehouseMapper;
import com.example.pcparts.vo.stock.InventoryMovementVO;
import com.example.pcparts.vo.stock.StockVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StockService {

    public static final int MOVEMENT_IN = 1;
    public static final int MOVEMENT_OUT = 2;

    private final InventoryStockMapper inventoryStockMapper;
    private final InventoryMovementMapper inventoryMovementMapper;
    private final WarehouseMapper warehouseMapper;
    private final ProductSkuMapper productSkuMapper;
    private final ProductMapper productMapper;

    public PageResponse<StockVO> pageStocks(long page, long size, Long warehouseId, Long skuId, String keyword) {
        List<Long> keywordSkuIds = null;
        if (StringUtils.hasText(keyword)) {
            keywordSkuIds = findSkuIdsByKeyword(keyword);
            if (keywordSkuIds.isEmpty()) {
                return new PageResponse<>(0, List.of());
            }
        }

        Page<InventoryStock> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<InventoryStock> query = new LambdaQueryWrapper<InventoryStock>();
        if (warehouseId != null) {
            query.eq(InventoryStock::getWarehouseId, warehouseId);
        }
        if (skuId != null) {
            query.eq(InventoryStock::getSkuId, skuId);
        }
        if (keywordSkuIds != null) {
            query.in(InventoryStock::getSkuId, keywordSkuIds);
        }
        query.orderByDesc(InventoryStock::getId);
        Page<InventoryStock> result = inventoryStockMapper.selectPage(mpPage, query);

        List<InventoryStock> records = result.getRecords();
        Map<Long, Warehouse> warehouseMap = loadWarehouses(records.stream().map(InventoryStock::getWarehouseId).toList());
        Map<Long, ProductSku> skuMap = loadSkus(records.stream().map(InventoryStock::getSkuId).toList());
        Map<Long, Product> productMap = loadProducts(skuMap.values().stream().map(ProductSku::getProductId).filter(Objects::nonNull).toList());

        List<StockVO> items = records.stream()
                .map(s -> {
                    Warehouse w = warehouseMap.get(s.getWarehouseId());
                    ProductSku sku = skuMap.get(s.getSkuId());
                    Product p = sku == null ? null : productMap.get(sku.getProductId());
                    return new StockVO(
                            s.getId(),
                            s.getWarehouseId(),
                            w == null ? null : w.getName(),
                            s.getSkuId(),
                            sku == null ? null : sku.getSkuCode(),
                            sku == null ? null : sku.getName(),
                            sku == null ? null : sku.getProductId(),
                            p == null ? null : p.getName(),
                            s.getQuantity(),
                            s.getUpdatedAt()
                    );
                })
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    @Transactional
    public StockVO stockIn(StockInRequest request) {
        adjustStock(request.getWarehouseId(), request.getSkuId(), request.getQuantity(), MOVEMENT_IN, "MANUAL_IN", null, null, request.getRemark());
        return getStockVO(request.getWarehouseId(), request.getSkuId());
    }

    @Transactional
    public StockVO stockOut(StockOutRequest request) {
        adjustStock(request.getWarehouseId(), request.getSkuId(), request.getQuantity(), MOVEMENT_OUT, "MANUAL_OUT", null, null, request.getRemark());
        return getStockVO(request.getWarehouseId(), request.getSkuId());
    }

    public PageResponse<InventoryMovementVO> pageMovements(long page, long size) {
        Page<InventoryMovement> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<InventoryMovement> query = new LambdaQueryWrapper<InventoryMovement>()
                .orderByDesc(InventoryMovement::getMovedAt)
                .orderByDesc(InventoryMovement::getId);
        Page<InventoryMovement> result = inventoryMovementMapper.selectPage(mpPage, query);

        List<InventoryMovement> records = result.getRecords();
        Map<Long, Warehouse> warehouseMap = loadWarehouses(records.stream().map(InventoryMovement::getWarehouseId).toList());
        Map<Long, ProductSku> skuMap = loadSkus(records.stream().map(InventoryMovement::getSkuId).toList());
        Map<Long, Product> productMap = loadProducts(skuMap.values().stream().map(ProductSku::getProductId).filter(Objects::nonNull).toList());

        List<InventoryMovementVO> items = records.stream()
                .map(m -> {
                    Warehouse w = warehouseMap.get(m.getWarehouseId());
                    ProductSku sku = skuMap.get(m.getSkuId());
                    Product p = sku == null ? null : productMap.get(sku.getProductId());
                    return new InventoryMovementVO(
                            m.getId(),
                            m.getWarehouseId(),
                            w == null ? null : w.getName(),
                            m.getSkuId(),
                            sku == null ? null : sku.getSkuCode(),
                            sku == null ? null : sku.getName(),
                            sku == null ? null : sku.getProductId(),
                            p == null ? null : p.getName(),
                            m.getMovementType(),
                            m.getBizType(),
                            m.getBizId(),
                            m.getBizNo(),
                            m.getQuantity(),
                            m.getBeforeQuantity(),
                            m.getAfterQuantity(),
                            m.getRemark(),
                            m.getMovedAt()
                    );
                })
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    @Transactional
    public void adjustStock(Long warehouseId, Long skuId, int quantity, int movementType, String bizType, Long bizId, String bizNo, String remark) {
        if (quantity <= 0) {
            throw new BusinessException(400, "quantity_min_1");
        }
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(400, "warehouse_not_found");
        }
        ProductSku sku = productSkuMapper.selectById(skuId);
        if (sku == null) {
            throw new BusinessException(400, "sku_not_found");
        }

        int maxRetry = 5;
        for (int i = 0; i < maxRetry; i++) {
            InventoryStock stock = selectStockForUpdateLogic(warehouseId, skuId);
            if (stock == null) {
                if (movementType == MOVEMENT_OUT) {
                    throw new BusinessException(400, "stock_not_enough");
                }
                try {
                    InventoryStock created = new InventoryStock();
                    created.setWarehouseId(warehouseId);
                    created.setSkuId(skuId);
                    created.setQuantity(0);
                    created.setVersion(0);
                    inventoryStockMapper.insert(created);
                } catch (DataIntegrityViolationException ignored) {
                }
                continue;
            }

            int beforeQty = stock.getQuantity() == null ? 0 : stock.getQuantity();
            int afterQty = movementType == MOVEMENT_IN ? beforeQty + quantity : beforeQty - quantity;
            if (afterQty < 0) {
                throw new BusinessException(400, "stock_not_enough");
            }

            stock.setQuantity(afterQty);
            int updated = inventoryStockMapper.updateById(stock);
            if (updated == 0) {
                continue;
            }

            InventoryMovement movement = new InventoryMovement();
            movement.setWarehouseId(warehouseId);
            movement.setSkuId(skuId);
            movement.setMovementType(movementType);
            movement.setBizType(bizType);
            movement.setBizId(bizId);
            movement.setBizNo(bizNo);
            movement.setQuantity(quantity);
            movement.setBeforeQuantity(beforeQty);
            movement.setAfterQuantity(afterQty);
            movement.setRemark(remark);
            inventoryMovementMapper.insert(movement);
            return;
        }
        throw new BusinessException(409, "stock_update_conflict");
    }

    private InventoryStock selectStockForUpdateLogic(Long warehouseId, Long skuId) {
        LambdaQueryWrapper<InventoryStock> query = new LambdaQueryWrapper<InventoryStock>()
                .eq(InventoryStock::getWarehouseId, warehouseId)
                .eq(InventoryStock::getSkuId, skuId)
                .last("limit 1");
        return inventoryStockMapper.selectOne(query);
    }

    private StockVO getStockVO(Long warehouseId, Long skuId) {
        InventoryStock stock = selectStockForUpdateLogic(warehouseId, skuId);
        if (stock == null) {
            throw new BusinessException(404, "not_found");
        }
        Warehouse w = warehouseMapper.selectById(stock.getWarehouseId());
        ProductSku sku = productSkuMapper.selectById(stock.getSkuId());
        Product p = (sku == null || sku.getProductId() == null) ? null : productMapper.selectById(sku.getProductId());
        return new StockVO(
                stock.getId(),
                stock.getWarehouseId(),
                w == null ? null : w.getName(),
                stock.getSkuId(),
                sku == null ? null : sku.getSkuCode(),
                sku == null ? null : sku.getName(),
                sku == null ? null : sku.getProductId(),
                p == null ? null : p.getName(),
                stock.getQuantity(),
                stock.getUpdatedAt()
        );
    }

    private List<Long> findSkuIdsByKeyword(String keyword) {
        LambdaQueryWrapper<Product> productQuery = new LambdaQueryWrapper<Product>()
                .like(Product::getName, keyword);
        List<Product> products = productMapper.selectList(productQuery);
        Set<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toSet());

        LambdaQueryWrapper<ProductSku> skuQuery = new LambdaQueryWrapper<ProductSku>()
                .and(w -> w.like(ProductSku::getName, keyword)
                        .or()
                        .like(ProductSku::getSkuCode, keyword)
                        .or()
                        .like(ProductSku::getBarcode, keyword));
        if (!productIds.isEmpty()) {
            skuQuery.or().in(ProductSku::getProductId, productIds);
        }
        List<ProductSku> skus = productSkuMapper.selectList(skuQuery);
        return skus.stream().map(ProductSku::getId).toList();
    }

    private Map<Long, Warehouse> loadWarehouses(List<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<Warehouse> list = warehouseMapper.selectBatchIds(distinct);
        Map<Long, Warehouse> map = new HashMap<>();
        for (Warehouse w : list) {
            map.put(w.getId(), w);
        }
        return map;
    }

    private Map<Long, ProductSku> loadSkus(List<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<ProductSku> list = productSkuMapper.selectBatchIds(distinct);
        Map<Long, ProductSku> map = new HashMap<>();
        for (ProductSku sku : list) {
            map.put(sku.getId(), sku);
        }
        return map;
    }

    private Map<Long, Product> loadProducts(List<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<Product> list = productMapper.selectBatchIds(distinct);
        Map<Long, Product> map = new HashMap<>();
        for (Product p : list) {
            map.put(p.getId(), p);
        }
        return map;
    }
}
