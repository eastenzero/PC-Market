package com.example.pcparts.vo.stock;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockVO {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long productId;
    private String productName;
    private Integer quantity;
    private LocalDateTime updatedAt;
}
