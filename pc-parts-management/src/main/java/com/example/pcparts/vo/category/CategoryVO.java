package com.example.pcparts.vo.category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryVO {

    private Long id;
    private Long parentId;
    private String name;
    private Integer sortOrder;
    private Integer level;
    private String path;
}
