package com.example.pcparts.vo.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesOrderVO {

    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String remark;
    private LocalDateTime orderedAt;
    private List<SalesOrderItemVO> items;
}
