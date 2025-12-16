package com.example.pcparts.dto.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class PurchaseOrderCreateRequest {

    @NotNull(message = "supplierId_required")
    private Long supplierId;

    private String remark;

    @Valid
    @NotEmpty(message = "items_required")
    private List<PurchaseOrderItemRequest> items;
}
