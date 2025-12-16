package com.example.pcparts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("stock_out")
public class StockOut {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String stockOutNo;

    private Long salesOrderId;

    private Long warehouseId;

    private Integer status;

    private BigDecimal totalAmount;

    private LocalDateTime shippedAt;

    private String remark;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private Long createdBy;

    private LocalDateTime updatedAt;

    private Long updatedBy;
}
