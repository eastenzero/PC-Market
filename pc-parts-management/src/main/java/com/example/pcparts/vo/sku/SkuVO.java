package com.example.pcparts.vo.sku;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkuVO {

    private Long id;
    private Long productId;
    private String productName;
    private String skuCode;
    private String barcode;
    private String name;
    private String specJson;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
