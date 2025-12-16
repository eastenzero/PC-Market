package com.example.pcparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pcparts.entity.SysRole;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.*
            FROM sys_role r
                     JOIN sys_user_role ur ON ur.role_id = r.id AND ur.deleted = 0
            WHERE ur.user_id = #{userId}
              AND r.deleted = 0
            """)
    List<SysRole> selectByUserId(@Param("userId") Long userId);
}
