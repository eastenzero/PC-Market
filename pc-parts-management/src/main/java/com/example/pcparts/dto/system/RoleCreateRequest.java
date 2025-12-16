package com.example.pcparts.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @NotBlank(message = "code_required")
    private String code;

    @NotBlank(message = "name_required")
    private String name;

    private String description;
}
