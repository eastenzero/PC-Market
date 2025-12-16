package com.example.pcparts.dto.sku;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkuUpdateStatusRequest {

    @NotNull(message = "status_required")
    private Integer status;
}
