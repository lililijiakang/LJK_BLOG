package com.ljk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.User;
import com.ljk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户信息分页显示及模糊查询
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param phonenumber
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult userInfoPage(Integer pageNum,Integer pageSize,String userName,String phonenumber,String status){
        return userService.userInfoPage(pageNum,pageSize,userName,phonenumber,status);
    }

    /**
     * 新增用户
     * @return
     */
    @PostMapping
    public ResponseResult saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    /**
     * 删除用户逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteUser(@PathVariable("id") Long id){
        return userService.deleteUser(id);
    }

    /**
     * 修改用户时回显的用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping
    public ResponseResult updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }
}
