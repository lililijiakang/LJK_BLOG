package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.RoleDto;
import com.ljk.domain.entity.Role;
import com.ljk.domain.entity.RoleMenu;
import com.ljk.domain.vo.PageVo;
import com.ljk.domain.vo.RoleVo;
import com.ljk.mapper.RoleMapper;
import com.ljk.service.RoleMenuService;
import com.ljk.service.RoleService;
import com.ljk.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;
    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //如果id是1代表超级管理员直接返回admin
        if(id==1L){
            List<String> roleKeys=new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询相关的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status) {
        //需要有角色列表分页查询的功能。
        //要求能够针对角色名称进行模糊查询。
        //要求能够针对状态进行查询。
        //要求按照role_sort进行升序排列
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(roleName),Role::getRoleName,roleName);
        queryWrapper.eq(StringUtils.hasText(status),Role::getStatus,status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        //分页查询
        Page page=new Page(pageNum,pageSize);
        page(page, queryWrapper);
        //封装进pageVo中返回给前端
        PageVo pageVo=new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult changeStatus(RoleDto roleDto) {
        UpdateWrapper<Role> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",roleDto.getId());
        updateWrapper.set("status",roleDto.getStatus());
        update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRoleInfo(Long id) {
        //根据id查询角色信息
        Role role = getById(id);
        //封装为RoleVo中返回
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }
    @Transactional
    @Override
    public ResponseResult updateRole(Role role) {
        //更新role表
        updateById(role);
        //更新role_menu的关联表
        roleMenuService.deleteRoleMenuByRoleId(role.getId());
        insertRoleMenu(role);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult saveRole(Role role) {
        //先保存角色信息
        save(role);
        //再保存角色和菜单关联信息
        insertRoleMenu(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllRole() {
        //查询所有状态正常的角色
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> list = list(queryWrapper);
        return ResponseResult.okResult(list);
    }

    private void insertRoleMenu(Role role) {
        List<RoleMenu> roleMenuList = Arrays.stream(role.getMenuIds())
                .map(memuId -> new RoleMenu(role.getId(), memuId))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenuList);
    }
}
