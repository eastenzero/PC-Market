package com.example.pcparts.vo.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WarehouseVO {

    private Long id;
    private String code;
    private String name;
    private String address;
    private String contactName;
    private String contactPhone;
    private Integer status;
}
