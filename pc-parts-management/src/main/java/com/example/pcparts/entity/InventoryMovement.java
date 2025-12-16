package com.example.pcparts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("inventory_movement")
public class InventoryMovement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long warehouseId;

    private Long skuId;

    private Integer movementType;

    private String bizType;

    private Long bizId;

    private String bizNo;

    private Integer quantity;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private String remark;

    private LocalDateTime movedAt;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private Long createdBy;

    private LocalDateTime updatedAt;

    private Long updatedBy;
}
