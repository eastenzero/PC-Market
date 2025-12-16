package com.example.pcparts.dto.purchase;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseReceiveRequest {

    @NotNull(message = "warehouseId_required")
    private Long warehouseId;

    private String remark;
}
