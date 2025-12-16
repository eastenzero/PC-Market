package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.system.UserCreateRequest;
import com.example.pcparts.dto.system.UserUpdateRequest;
import com.example.pcparts.entity.SysRole;
import com.example.pcparts.entity.SysUser;
import com.example.pcparts.entity.SysUserRole;
import com.example.pcparts.mapper.SysRoleMapper;
import com.example.pcparts.mapper.SysUserMapper;
import com.example.pcparts.mapper.SysUserRoleMapper;
import com.example.pcparts.vo.system.UserVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<UserVO> page(long page, long size, String keyword) {
        Page<SysUser> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getNickname, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword)
                    .or()
                    .like(SysUser::getEmail, keyword));
        }
        query.orderByDesc(SysUser::getId);
        Page<SysUser> result = sysUserMapper.selectPage(mpPage, query);

        List<SysUser> records = result.getRecords();
        if (records.isEmpty()) {
            return new PageResponse<>(0, List.of());
        }

        Map<Long, UserRoles> rolesMap = loadUserRoles(records.stream().map(SysUser::getId).toList());
        List<UserVO> items = records.stream()
                .map(u -> {
                    UserRoles r = rolesMap.get(u.getId());
                    List<Long> roleIds = r == null ? List.of() : r.roleIds;
                    List<String> roleCodes = r == null ? List.of() : r.roleCodes;
                    return toVo(u, roleIds, roleCodes);
                })
                .toList();

        return new PageResponse<>(result.getTotal(), items);
    }

    public UserVO getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "not_found");
        }
        UserRoles roles = loadUserRoles(List.of(id)).get(id);
        List<Long> roleIds = roles == null ? List.of() : roles.roleIds;
        List<String> roleCodes = roles == null ? List.of() : roles.roleCodes;
        return toVo(user, roleIds, roleCodes);
    }

    @Transactional
    public UserVO create(UserCreateRequest request) {
        long exists = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (exists > 0) {
            throw new BusinessException(400, "username_exists");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        sysUserMapper.insert(user);

        if (request.getRoleIds() != null) {
            replaceRoles(user.getId(), request.getRoleIds());
        }

        return getById(user.getId());
    }

    @Transactional
    public UserVO update(Long id, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "not_found");
        }

        long exists = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .ne(SysUser::getId, id));
        if (exists > 0) {
            throw new BusinessException(400, "username_exists");
        }

        user.setUsername(request.getUsername());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        sysUserMapper.updateById(user);

        if (request.getRoleIds() != null) {
            replaceRoles(id, request.getRoleIds());
        }

        return getById(id);
    }

    @Transactional
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "not_found");
        }
        sysUserMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    private void replaceRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        Set<Long> distinct = roleIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (distinct.isEmpty()) {
            return;
        }

        List<SysRole> roles = sysRoleMapper.selectBatchIds(distinct);
        if (roles.size() != distinct.size()) {
            throw new BusinessException(400, "role_not_found");
        }

        for (Long roleId : distinct) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
    }

    private Map<Long, UserRoles> loadUserRoles(List<Long> userIds) {
        List<Long> distinctUsers = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctUsers.isEmpty()) {
            return Map.of();
        }

        List<SysUserRole> relations = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, distinctUsers));
        if (relations.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<Long>> userRoleIds = relations.stream()
                .collect(Collectors.groupingBy(SysUserRole::getUserId, Collectors.mapping(SysUserRole::getRoleId, Collectors.toList())));

        Set<Long> roleIdSet = relations.stream().map(SysUserRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SysRole> roleMap = new HashMap<>();
        if (!roleIdSet.isEmpty()) {
            for (SysRole r : sysRoleMapper.selectBatchIds(roleIdSet)) {
                roleMap.put(r.getId(), r);
            }
        }

        Map<Long, UserRoles> result = new HashMap<>();
        for (Map.Entry<Long, List<Long>> e : userRoleIds.entrySet()) {
            List<Long> rids = e.getValue() == null ? List.of() : e.getValue();
            List<String> codes = rids.stream()
                    .map(roleMap::get)
                    .filter(Objects::nonNull)
                    .map(SysRole::getCode)
                    .toList();
            result.put(e.getKey(), new UserRoles(rids, codes));
        }
        return result;
    }

    private UserVO toVo(SysUser u, List<Long> roleIds, List<String> roles) {
        return new UserVO(
                u.getId(),
                u.getUsername(),
                u.getNickname(),
                u.getPhone(),
                u.getEmail(),
                u.getStatus(),
                u.getLastLoginAt(),
                roleIds,
                roles,
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }

    private record UserRoles(List<Long> roleIds, List<String> roleCodes) {
    }
}
