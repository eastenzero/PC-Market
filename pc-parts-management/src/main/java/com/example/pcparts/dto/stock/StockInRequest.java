package com.example.pcparts.dto.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockInRequest {

    @NotNull(message = "warehouseId_required")
    private Long warehouseId;

    @NotNull(message = "skuId_required")
    private Long skuId;

    @NotNull(message = "quantity_required")
    @Min(value = 1, message = "quantity_min_1")
    private Integer quantity;

    private String remark;
}
