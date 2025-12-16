package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.NoGenerator;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.purchase.PurchaseOrderCreateRequest;
import com.example.pcparts.dto.purchase.PurchaseOrderItemRequest;
import com.example.pcparts.dto.purchase.PurchaseReceiveRequest;
import com.example.pcparts.entity.ProductSku;
import com.example.pcparts.entity.PurchaseOrder;
import com.example.pcparts.entity.PurchaseOrderItem;
import com.example.pcparts.entity.StockIn;
import com.example.pcparts.entity.StockInItem;
import com.example.pcparts.entity.Supplier;
import com.example.pcparts.mapper.ProductSkuMapper;
import com.example.pcparts.mapper.PurchaseOrderItemMapper;
import com.example.pcparts.mapper.PurchaseOrderMapper;
import com.example.pcparts.mapper.StockInItemMapper;
import com.example.pcparts.mapper.StockInMapper;
import com.example.pcparts.mapper.SupplierMapper;
import com.example.pcparts.vo.purchase.PurchaseOrderItemVO;
import com.example.pcparts.vo.purchase.PurchaseOrderVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    public static final int STATUS_CREATED = 0;
    public static final int STATUS_RECEIVED = 1;

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final SupplierMapper supplierMapper;
    private final ProductSkuMapper productSkuMapper;
    private final StockInMapper stockInMapper;
    private final StockInItemMapper stockInItemMapper;
    private final StockService stockService;

    @Transactional
    public PurchaseOrderVO create(PurchaseOrderCreateRequest request) {
        Supplier supplier = supplierMapper.selectById(request.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(400, "supplier_not_found");
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(NoGenerator.generate("PO"));
        order.setSupplierId(request.getSupplierId());
        order.setStatus(STATUS_CREATED);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setRemark(request.getRemark());
        order.setOrderedAt(LocalDateTime.now());
        purchaseOrderMapper.insert(order);

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            ProductSku sku = productSkuMapper.selectById(itemRequest.getSkuId());
            if (sku == null) {
                throw new BusinessException(400, "sku_not_found");
            }
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrderId(order.getId());
            item.setSkuId(itemRequest.getSkuId());
            item.setPrice(itemRequest.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            BigDecimal amount = itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setAmount(amount);
            item.setReceivedQuantity(0);
            purchaseOrderItemMapper.insert(item);
            total = total.add(amount);
        }

        order.setTotalAmount(total);
        purchaseOrderMapper.updateById(order);

        return getById(order.getId());
    }

    public PageResponse<PurchaseOrderVO> page(long page, long size, String keyword, Long supplierId, Integer status) {
        Page<PurchaseOrder> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<PurchaseOrder> query = new LambdaQueryWrapper<PurchaseOrder>();
        if (StringUtils.hasText(keyword)) {
            query.like(PurchaseOrder::getOrderNo, keyword);
        }
        if (supplierId != null) {
            query.eq(PurchaseOrder::getSupplierId, supplierId);
        }
        if (status != null) {
            query.eq(PurchaseOrder::getStatus, status);
        }
        query.orderByDesc(PurchaseOrder::getId);
        Page<PurchaseOrder> result = purchaseOrderMapper.selectPage(mpPage, query);

        List<PurchaseOrder> orders = result.getRecords();
        if (orders.isEmpty()) {
            return new PageResponse<>(0, List.of());
        }

        Map<Long, Supplier> supplierMap = loadSuppliers(orders.stream().map(PurchaseOrder::getSupplierId).toList());
        Map<Long, List<PurchaseOrderItem>> itemMap = loadOrderItems(orders.stream().map(PurchaseOrder::getId).toList());
        Map<Long, ProductSku> skuMap = loadSkus(itemMap.values().stream().flatMap(List::stream).map(PurchaseOrderItem::getSkuId).toList());

        List<PurchaseOrderVO> items = orders.stream()
                .map(o -> toVo(o, supplierMap.get(o.getSupplierId()), itemMap.getOrDefault(o.getId(), List.of()), skuMap))
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public PurchaseOrderVO getById(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "not_found");
        }
        Supplier supplier = supplierMapper.selectById(order.getSupplierId());
        List<PurchaseOrderItem> orderItems = purchaseOrderItemMapper.selectList(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, id)
                .orderByAsc(PurchaseOrderItem::getId));
        Map<Long, ProductSku> skuMap = loadSkus(orderItems.stream().map(PurchaseOrderItem::getSkuId).toList());
        return toVo(order, supplier, orderItems, skuMap);
    }

    @Transactional
    public PurchaseOrderVO receive(Long id, PurchaseReceiveRequest request) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "not_found");
        }
        if (!Objects.equals(order.getStatus(), STATUS_CREATED)) {
            throw new BusinessException(400, "invalid_status");
        }

        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectList(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, id)
                .orderByAsc(PurchaseOrderItem::getId));
        if (items.isEmpty()) {
            throw new BusinessException(400, "items_required");
        }

        StockIn stockIn = new StockIn();
        stockIn.setStockInNo(NoGenerator.generate("SI"));
        stockIn.setPurchaseOrderId(id);
        stockIn.setWarehouseId(request.getWarehouseId());
        stockIn.setStatus(1);
        stockIn.setTotalAmount(order.getTotalAmount());
        stockIn.setReceivedAt(LocalDateTime.now());
        stockIn.setRemark(request.getRemark());
        stockInMapper.insert(stockIn);

        for (PurchaseOrderItem item : items) {
            int alreadyReceived = item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity();
            int toReceive = item.getQuantity() == null ? 0 : (item.getQuantity() - alreadyReceived);
            if (toReceive <= 0) {
                continue;
            }

            StockInItem inItem = new StockInItem();
            inItem.setStockInId(stockIn.getId());
            inItem.setSkuId(item.getSkuId());
            inItem.setPrice(item.getPrice());
            inItem.setQuantity(toReceive);
            inItem.setAmount(item.getPrice().multiply(BigDecimal.valueOf(toReceive)));
            stockInItemMapper.insert(inItem);

            item.setReceivedQuantity(alreadyReceived + toReceive);
            purchaseOrderItemMapper.updateById(item);

            stockService.adjustStock(
                    request.getWarehouseId(),
                    item.getSkuId(),
                    toReceive,
                    StockService.MOVEMENT_IN,
                    "PURCHASE_RECEIVE",
                    order.getId(),
                    order.getOrderNo(),
                    request.getRemark()
            );
        }

        order.setStatus(STATUS_RECEIVED);
        purchaseOrderMapper.updateById(order);

        return getById(id);
    }

    private Map<Long, Supplier> loadSuppliers(List<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<Supplier> list = supplierMapper.selectBatchIds(distinct);
        Map<Long, Supplier> map = new HashMap<>();
        for (Supplier s : list) {
            map.put(s.getId(), s);
        }
        return map;
    }

    private Map<Long, List<PurchaseOrderItem>> loadOrderItems(List<Long> orderIds) {
        List<Long> distinct = orderIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<PurchaseOrderItem> list = purchaseOrderItemMapper.selectList(new LambdaQueryWrapper<PurchaseOrderItem>()
                .in(PurchaseOrderItem::getPurchaseOrderId, distinct)
                .orderByAsc(PurchaseOrderItem::getId));
        return list.stream().collect(Collectors.groupingBy(PurchaseOrderItem::getPurchaseOrderId));
    }

    private Map<Long, ProductSku> loadSkus(List<Long> skuIds) {
        Set<Long> distinct = skuIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<ProductSku> skus = productSkuMapper.selectBatchIds(distinct);
        Map<Long, ProductSku> map = new HashMap<>();
        for (ProductSku sku : skus) {
            map.put(sku.getId(), sku);
        }
        return map;
    }

    private PurchaseOrderVO toVo(PurchaseOrder o, Supplier supplier, List<PurchaseOrderItem> items, Map<Long, ProductSku> skuMap) {
        List<PurchaseOrderItemVO> itemVos = new ArrayList<>();
        for (PurchaseOrderItem item : items) {
            ProductSku sku = skuMap.get(item.getSkuId());
            itemVos.add(new PurchaseOrderItemVO(
                    item.getId(),
                    item.getSkuId(),
                    sku == null ? null : sku.getSkuCode(),
                    sku == null ? null : sku.getName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getAmount(),
                    item.getReceivedQuantity()
            ));
        }
        return new PurchaseOrderVO(
                o.getId(),
                o.getOrderNo(),
                o.getSupplierId(),
                supplier == null ? null : supplier.getName(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getRemark(),
                o.getOrderedAt(),
                itemVos
        );
    }
}
