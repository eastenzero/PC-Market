package com.example.pcparts.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandUpdateRequest {

    @NotBlank(message = "name_required")
    private String name;

    private String description;

    @NotNull(message = "status_required")
    private Integer status;
}
