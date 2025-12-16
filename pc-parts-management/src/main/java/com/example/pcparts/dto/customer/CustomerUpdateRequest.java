package com.example.pcparts.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerUpdateRequest {

    @NotBlank(message = "name_required")
    private String name;

    private String phone;

    private String email;

    private String address;

    @NotNull(message = "status_required")
    private Integer status;
}
