package com.ljk.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.RoleDto;
import com.ljk.domain.entity.Role;
import com.ljk.service.RoleService;
import com.ljk.utils.SystemConverter;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 角色信息模糊查询及分页查询
     * @param pageNum
     * @param pageSize
     * @param roleName
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getRoleList(Integer pageNum,Integer pageSize,String roleName,String status){
        return roleService.getRoleList(pageNum,pageSize,roleName,status);
    }

    /**
     * 改变role的状态
     * @param roleDto
     * @return
     */
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleDto roleDto){
        return roleService.changeStatus(roleDto);
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    @PostMapping
    public ResponseResult saveRole(@RequestBody Role role){
        return roleService.saveRole(role);
    }

    /**
     * 修改role时回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getRoleInfo(@PathVariable("id") Long id){
        return roleService.getRoleInfo(id);
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @PutMapping
    public ResponseResult updateRole(@RequestBody Role role){
        return roleService.updateRole(role);
    }

    /**
     * 删除角色,逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable("id") Long id){
        UpdateWrapper<Role> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        updateWrapper.set("del_flag", SystemConstants.ROLE_DEL);
        roleService.update(updateWrapper);
        return ResponseResult.okResult();
    }

    /**
     * 新增用户时返回所有状态正常的角色
     * @return
     */
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        return roleService.listAllRole();
    }
}
