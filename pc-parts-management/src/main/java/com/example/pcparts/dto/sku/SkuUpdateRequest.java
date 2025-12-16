package com.example.pcparts.dto.sku;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class SkuUpdateRequest {

    @NotNull(message = "productId_required")
    private Long productId;

    @NotBlank(message = "skuCode_required")
    private String skuCode;

    private String barcode;

    @NotBlank(message = "name_required")
    private String name;

    private String specJson;

    @NotNull(message = "price_required")
    private BigDecimal price;

    @NotNull(message = "cost_required")
    private BigDecimal cost;

    @NotNull(message = "status_required")
    private Integer status;
}
