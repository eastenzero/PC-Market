package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.NoGenerator;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.sales.SalesOrderCreateRequest;
import com.example.pcparts.dto.sales.SalesOrderItemRequest;
import com.example.pcparts.dto.sales.SalesPayRequest;
import com.example.pcparts.dto.sales.SalesShipRequest;
import com.example.pcparts.entity.Customer;
import com.example.pcparts.entity.PaymentRecord;
import com.example.pcparts.entity.ProductSku;
import com.example.pcparts.entity.SalesOrder;
import com.example.pcparts.entity.SalesOrderItem;
import com.example.pcparts.entity.StockOut;
import com.example.pcparts.entity.StockOutItem;
import com.example.pcparts.mapper.CustomerMapper;
import com.example.pcparts.mapper.PaymentRecordMapper;
import com.example.pcparts.mapper.ProductSkuMapper;
import com.example.pcparts.mapper.SalesOrderItemMapper;
import com.example.pcparts.mapper.SalesOrderMapper;
import com.example.pcparts.mapper.StockOutItemMapper;
import com.example.pcparts.mapper.StockOutMapper;
import com.example.pcparts.vo.sales.SalesOrderItemVO;
import com.example.pcparts.vo.sales.SalesOrderVO;
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
public class SalesOrderService {

    public static final int STATUS_CREATED = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_SHIPPED = 2;

    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;
    private final CustomerMapper customerMapper;
    private final ProductSkuMapper productSkuMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final StockOutMapper stockOutMapper;
    private final StockOutItemMapper stockOutItemMapper;
    private final StockService stockService;

    @Transactional
    public SalesOrderVO create(SalesOrderCreateRequest request) {
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException(400, "customer_not_found");
        }

        SalesOrder order = new SalesOrder();
        order.setOrderNo(NoGenerator.generate("SO"));
        order.setCustomerId(request.getCustomerId());
        order.setStatus(STATUS_CREATED);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setRemark(request.getRemark());
        order.setOrderedAt(LocalDateTime.now());
        salesOrderMapper.insert(order);

        BigDecimal total = BigDecimal.ZERO;
        for (SalesOrderItemRequest itemRequest : request.getItems()) {
            ProductSku sku = productSkuMapper.selectById(itemRequest.getSkuId());
            if (sku == null) {
                throw new BusinessException(400, "sku_not_found");
            }

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrderId(order.getId());
            item.setSkuId(itemRequest.getSkuId());
            item.setPrice(itemRequest.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            BigDecimal amount = itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setAmount(amount);
            item.setShippedQuantity(0);
            salesOrderItemMapper.insert(item);
            total = total.add(amount);
        }

        order.setTotalAmount(total);
        salesOrderMapper.updateById(order);

        return getById(order.getId());
    }

    public PageResponse<SalesOrderVO> page(long page, long size, String keyword, Long customerId, Integer status) {
        Page<SalesOrder> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<SalesOrder> query = new LambdaQueryWrapper<SalesOrder>();
        if (StringUtils.hasText(keyword)) {
            query.like(SalesOrder::getOrderNo, keyword);
        }
        if (customerId != null) {
            query.eq(SalesOrder::getCustomerId, customerId);
        }
        if (status != null) {
            query.eq(SalesOrder::getStatus, status);
        }
        query.orderByDesc(SalesOrder::getId);
        Page<SalesOrder> result = salesOrderMapper.selectPage(mpPage, query);

        List<SalesOrder> orders = result.getRecords();
        if (orders.isEmpty()) {
            return new PageResponse<>(0, List.of());
        }

        Map<Long, Customer> customerMap = loadCustomers(orders.stream().map(SalesOrder::getCustomerId).toList());
        Map<Long, List<SalesOrderItem>> itemMap = loadOrderItems(orders.stream().map(SalesOrder::getId).toList());
        Map<Long, ProductSku> skuMap = loadSkus(itemMap.values().stream().flatMap(List::stream).map(SalesOrderItem::getSkuId).toList());

        List<SalesOrderVO> items = orders.stream()
                .map(o -> toVo(o, customerMap.get(o.getCustomerId()), itemMap.getOrDefault(o.getId(), List.of()), skuMap))
                .toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public SalesOrderVO getById(Long id) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "not_found");
        }
        Customer customer = customerMapper.selectById(order.getCustomerId());
        List<SalesOrderItem> items = salesOrderItemMapper.selectList(new LambdaQueryWrapper<SalesOrderItem>()
                .eq(SalesOrderItem::getSalesOrderId, id)
                .orderByAsc(SalesOrderItem::getId));
        Map<Long, ProductSku> skuMap = loadSkus(items.stream().map(SalesOrderItem::getSkuId).toList());
        return toVo(order, customer, items, skuMap);
    }

    @Transactional
    public SalesOrderVO pay(Long id, SalesPayRequest request) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "not_found");
        }
        if (Objects.equals(order.getStatus(), STATUS_SHIPPED)) {
            throw new BusinessException(400, "invalid_status");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "amount_min_1");
        }

        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        BigDecimal total = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        BigDecimal newPaid = paid.add(amount);
        if (newPaid.compareTo(total) > 0) {
            throw new BusinessException(400, "paid_amount_exceed_total");
        }

        PaymentRecord record = new PaymentRecord();
        record.setPayNo(NoGenerator.generate("PAY"));
        record.setSalesOrderId(order.getId());
        record.setAmount(amount);
        record.setPayMethod(request.getPayMethod());
        record.setPaidAt(LocalDateTime.now());
        record.setRemark(request.getRemark());
        paymentRecordMapper.insert(record);

        order.setPaidAmount(newPaid);
        if (newPaid.compareTo(total) >= 0) {
            order.setStatus(STATUS_PAID);
        }
        salesOrderMapper.updateById(order);

        return getById(id);
    }

    @Transactional
    public SalesOrderVO ship(Long id, SalesShipRequest request) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "not_found");
        }
        if (Objects.equals(order.getStatus(), STATUS_SHIPPED)) {
            throw new BusinessException(400, "invalid_status");
        }

        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        BigDecimal total = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        if (paid.compareTo(total) < 0) {
            throw new BusinessException(400, "not_paid");
        }

        List<SalesOrderItem> items = salesOrderItemMapper.selectList(new LambdaQueryWrapper<SalesOrderItem>()
                .eq(SalesOrderItem::getSalesOrderId, id)
                .orderByAsc(SalesOrderItem::getId));
        if (items.isEmpty()) {
            throw new BusinessException(400, "items_required");
        }

        StockOut stockOut = new StockOut();
        stockOut.setStockOutNo(NoGenerator.generate("SO"));
        stockOut.setSalesOrderId(id);
        stockOut.setWarehouseId(request.getWarehouseId());
        stockOut.setStatus(1);
        stockOut.setTotalAmount(total);
        stockOut.setShippedAt(LocalDateTime.now());
        stockOut.setRemark(request.getRemark());
        stockOutMapper.insert(stockOut);

        for (SalesOrderItem item : items) {
            int shippedQty = item.getShippedQuantity() == null ? 0 : item.getShippedQuantity();
            int toShip = item.getQuantity() == null ? 0 : (item.getQuantity() - shippedQty);
            if (toShip <= 0) {
                continue;
            }

            StockOutItem outItem = new StockOutItem();
            outItem.setStockOutId(stockOut.getId());
            outItem.setSkuId(item.getSkuId());
            outItem.setPrice(item.getPrice());
            outItem.setQuantity(toShip);
            outItem.setAmount(item.getPrice().multiply(BigDecimal.valueOf(toShip)));
            stockOutItemMapper.insert(outItem);

            item.setShippedQuantity(shippedQty + toShip);
            salesOrderItemMapper.updateById(item);

            stockService.adjustStock(
                    request.getWarehouseId(),
                    item.getSkuId(),
                    toShip,
                    StockService.MOVEMENT_OUT,
                    "SALES_SHIP",
                    order.getId(),
                    order.getOrderNo(),
                    request.getRemark()
            );
        }

        order.setStatus(STATUS_SHIPPED);
        salesOrderMapper.updateById(order);

        return getById(id);
    }

    private Map<Long, Customer> loadCustomers(List<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<Customer> list = customerMapper.selectBatchIds(distinct);
        Map<Long, Customer> map = new HashMap<>();
        for (Customer c : list) {
            map.put(c.getId(), c);
        }
        return map;
    }

    private Map<Long, List<SalesOrderItem>> loadOrderItems(List<Long> orderIds) {
        List<Long> distinct = orderIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return Map.of();
        }
        List<SalesOrderItem> list = salesOrderItemMapper.selectList(new LambdaQueryWrapper<SalesOrderItem>()
                .in(SalesOrderItem::getSalesOrderId, distinct)
                .orderByAsc(SalesOrderItem::getId));
        return list.stream().collect(Collectors.groupingBy(SalesOrderItem::getSalesOrderId));
    }

    private Map<Long, ProductSku> loadSkus(List<Long> skuIds) {
        Set<Long> distinct = skuIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
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

    private SalesOrderVO toVo(SalesOrder o, Customer customer, List<SalesOrderItem> items, Map<Long, ProductSku> skuMap) {
        List<SalesOrderItemVO> itemVos = new ArrayList<>();
        for (SalesOrderItem item : items) {
            ProductSku sku = skuMap.get(item.getSkuId());
            itemVos.add(new SalesOrderItemVO(
                    item.getId(),
                    item.getSkuId(),
                    sku == null ? null : sku.getSkuCode(),
                    sku == null ? null : sku.getName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getAmount(),
                    item.getShippedQuantity()
            ));
        }
        return new SalesOrderVO(
                o.getId(),
                o.getOrderNo(),
                o.getCustomerId(),
                customer == null ? null : customer.getName(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getPaidAmount(),
                o.getRemark(),
                o.getOrderedAt(),
                itemVos
        );
    }
}
