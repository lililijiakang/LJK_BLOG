package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.domain.entity.RoleMenu;
import com.ljk.mapper.RoleMenuMapper;
import com.ljk.service.RoleMenuService;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Override
    public void deleteRoleMenuByRoleId(Long id) {
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,id);
        remove(queryWrapper);
    }
}
