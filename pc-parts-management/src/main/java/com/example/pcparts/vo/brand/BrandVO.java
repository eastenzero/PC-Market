package com.example.pcparts.vo.brand;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandVO {

    private Long id;
    private String name;
    private String description;
    private Integer status;
}
