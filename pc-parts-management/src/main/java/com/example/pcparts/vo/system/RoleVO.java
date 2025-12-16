package com.example.pcparts.vo.system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleVO {

    private Long id;
    private String code;
    private String name;
    private String description;
}
