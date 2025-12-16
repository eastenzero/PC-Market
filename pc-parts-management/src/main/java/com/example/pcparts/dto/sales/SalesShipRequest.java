package com.example.pcparts.dto.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesShipRequest {

    @NotNull(message = "warehouseId_required")
    private Long warehouseId;

    private String remark;
}
