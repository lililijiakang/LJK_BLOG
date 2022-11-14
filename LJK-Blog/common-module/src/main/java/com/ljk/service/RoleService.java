package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.RoleDto;
import com.ljk.domain.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status);

    ResponseResult changeStatus(RoleDto roleDto);

    ResponseResult getRoleInfo(Long id);

    ResponseResult updateRole(Role role);

    ResponseResult saveRole(Role role);

    ResponseResult listAllRole();
}
