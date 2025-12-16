package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.system.RoleCreateRequest;
import com.example.pcparts.dto.system.RoleUpdateRequest;
import com.example.pcparts.entity.SysRole;
import com.example.pcparts.mapper.SysRoleMapper;
import com.example.pcparts.vo.system.RoleVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    public PageResponse<RoleVO> page(long page, long size, String keyword) {
        Page<SysRole> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(SysRole::getCode, keyword)
                    .or()
                    .like(SysRole::getName, keyword));
        }
        query.orderByDesc(SysRole::getId);
        Page<SysRole> result = sysRoleMapper.selectPage(mpPage, query);
        List<RoleVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public List<RoleVO> listAll() {
        List<SysRole> list = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByDesc(SysRole::getId));
        return list.stream().map(this::toVo).toList();
    }

    public RoleVO getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(role);
    }

    public RoleVO create(RoleCreateRequest request) {
        long exists = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, request.getCode()));
        if (exists > 0) {
            throw new BusinessException(400, "code_exists");
        }

        SysRole role = new SysRole();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        sysRoleMapper.insert(role);
        return getById(role.getId());
    }

    public RoleVO update(Long id, RoleUpdateRequest request) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "not_found");
        }

        long exists = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, request.getCode())
                .ne(SysRole::getId, id));
        if (exists > 0) {
            throw new BusinessException(400, "code_exists");
        }

        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        sysRoleMapper.updateById(role);
        return getById(id);
    }

    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "not_found");
        }
        sysRoleMapper.deleteById(id);
    }

    private RoleVO toVo(SysRole r) {
        return new RoleVO(r.getId(), r.getCode(), r.getName(), r.getDescription());
    }
}
