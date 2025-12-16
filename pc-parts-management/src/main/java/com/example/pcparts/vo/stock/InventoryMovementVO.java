package com.example.pcparts.vo.stock;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryMovementVO {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long productId;
    private String productName;
    private Integer movementType;
    private String bizType;
    private Long bizId;
    private String bizNo;
    private Integer quantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String remark;
    private LocalDateTime movedAt;
}
