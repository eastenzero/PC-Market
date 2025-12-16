package com.example.pcparts.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WarehouseCreateRequest {

    @NotBlank(message = "code_required")
    private String code;

    @NotBlank(message = "name_required")
    private String name;

    private String address;

    private String contactName;

    private String contactPhone;

    @NotNull(message = "status_required")
    private Integer status;
}
