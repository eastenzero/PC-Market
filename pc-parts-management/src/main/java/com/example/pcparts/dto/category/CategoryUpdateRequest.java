package com.example.pcparts.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    private Long parentId;

    @NotBlank(message = "name_required")
    private String name;

    private Integer sortOrder;
}
