package com.example.pcparts.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "spuCode_required")
    private String spuCode;

    @NotBlank(message = "name_required")
    private String name;

    @NotNull(message = "categoryId_required")
    private Long categoryId;

    @NotNull(message = "brandId_required")
    private Long brandId;

    private String description;

    @NotNull(message = "status_required")
    private Integer status;
}
