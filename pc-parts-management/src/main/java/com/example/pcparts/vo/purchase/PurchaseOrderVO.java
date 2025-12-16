package com.example.pcparts.vo.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseOrderVO {

    private Long id;
    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private Integer status;
    private BigDecimal totalAmount;
    private String remark;
    private LocalDateTime orderedAt;
    private List<PurchaseOrderItemVO> items;
}
