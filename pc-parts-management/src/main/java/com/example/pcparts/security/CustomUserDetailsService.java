package com.example.pcparts.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.pcparts.entity.SysRole;
import com.example.pcparts.entity.SysUser;
import com.example.pcparts.mapper.SysRoleMapper;
import com.example.pcparts.mapper.SysUserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
                .last("limit 1");
        SysUser user = sysUserMapper.selectOne(query);
        if (user == null) {
            throw new UsernameNotFoundException("user_not_found");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new UsernameNotFoundException("user_disabled");
        }
        List<String> roles = sysRoleMapper.selectByUserId(user.getId()).stream().map(SysRole::getCode).toList();
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getNickname(), true, roles);
    }
}
