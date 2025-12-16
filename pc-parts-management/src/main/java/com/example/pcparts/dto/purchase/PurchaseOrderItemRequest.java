package com.example.pcparts.dto.purchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PurchaseOrderItemRequest {

    @NotNull(message = "skuId_required")
    private Long skuId;

    @NotNull(message = "price_required")
    private BigDecimal price;

    @NotNull(message = "quantity_required")
    @Min(value = 1, message = "quantity_min_1")
    private Integer quantity;
}
