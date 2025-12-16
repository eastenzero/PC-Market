package com.example.pcparts.dto.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class SalesOrderCreateRequest {

    @NotNull(message = "customerId_required")
    private Long customerId;

    private String remark;

    @Valid
    @NotEmpty(message = "items_required")
    private List<SalesOrderItemRequest> items;
}
