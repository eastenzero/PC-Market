package com.example.pcparts.vo.purchase;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseOrderItemVO {

    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
    private Integer receivedQuantity;
}
