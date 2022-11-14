package com.ljk.service.impl;

import com.ljk.utils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service("ps")
public class PermissionService {

    public boolean hasPermission(String permission){
        //如果是超级管理员直接返回true
        if(SecurityUtils.isAdmin()){
            return true;
        }
        //否则查询是否包含此操作需要的权限信息,是返回true 否返回false
        return SecurityUtils.getLoginUser().getPermissions().contains(permission);
    }
}
