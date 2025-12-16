package com.example.pcparts.vo.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerVO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Integer status;
}
