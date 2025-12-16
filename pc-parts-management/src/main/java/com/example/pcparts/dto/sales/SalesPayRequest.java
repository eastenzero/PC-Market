package com.example.pcparts.dto.sales;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class SalesPayRequest {

    @NotNull(message = "amount_required")
    @Min(value = 1, message = "amount_min_1")
    private BigDecimal amount;

    @NotBlank(message = "payMethod_required")
    private String payMethod;

    private String remark;
}
