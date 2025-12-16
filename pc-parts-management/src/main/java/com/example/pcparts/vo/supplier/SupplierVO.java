package com.example.pcparts.vo.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierVO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Integer status;
}
