package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.entity.RoleMenu;

public interface RoleMenuService extends IService<RoleMenu> {
    void deleteRoleMenuByRoleId(Long id);
}
