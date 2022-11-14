package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.Role;
import com.ljk.domain.entity.User;
import com.ljk.domain.entity.UserInfoVo;
import com.ljk.domain.entity.UserRole;
import com.ljk.domain.vo.PageVo;
import com.ljk.domain.vo.UserRoleVo;
import com.ljk.enums.AppHttpCodeEnum;
import com.ljk.exception.SystemException;
import com.ljk.mapper.UserMapper;
import com.ljk.service.RoleService;
import com.ljk.service.UserRoleService;
import com.ljk.service.UserService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseResult userInfo() {
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装进UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(passwordExist(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //对密码进行加密处理
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult userInfoPage(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        //先判断有无模糊查询,有则查,无则不查
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(userName),User::getUserName,userName);
        queryWrapper.eq(StringUtils.hasText(phonenumber),User::getPhonenumber,phonenumber);
        queryWrapper.eq(StringUtils.hasText(status),User::getStatus,status);
        //分页
        Page page=new Page(pageNum,pageSize);
        page(page, queryWrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    public ResponseResult saveUser(User user) {
        //用户名不能为空，否则提示：必需填写用户名
        //用户名必须之前未存在，否则提示：用户名已存在
        //手机号必须之前未存在，否则提示：手机号已存在
        //邮箱必须之前未存在，否则提示：邮箱已存在
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //注意：新增用户时注意密码加密存储。
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(Long id) {
        //不能删除当前操作的用户
        if(SecurityUtils.getUserId().equals(id)){
            throw new SystemException(AppHttpCodeEnum.NO_DEL_USER);
        }
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        updateWrapper.set("del_flag", SystemConstants.USER_DEL_FLAG);
        update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserById(Long id) {
        //根据用户id查询用户信息
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,id);
        User user = getOne(queryWrapper);
        //查询用户所关联的角色id列表
        LambdaQueryWrapper<UserRole> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(UserRole::getUserId,id);
        List<UserRole> list = userRoleService.list(queryWrapper1);
        List<Long> ids = list.stream().map(userRole -> userRole.getRoleId())
                .collect(Collectors.toList());
        Long[] roleIds=ids.toArray(new Long[0]);
        //查询所有角色信息
        List<Role> roles = roleService.list();
        //封装进UserRoleVo中返回
        UserRoleVo userRoleVo=new UserRoleVo(roleIds,roles,user);
        return ResponseResult.okResult(userRoleVo);
    }

    @Transactional
    @Override
    public ResponseResult updateUser(User user) {
        //先更新user表
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,user.getId());
        userService.remove(queryWrapper);
        userService.save(user);
        //再更新user-role关联表
        LambdaQueryWrapper<UserRole> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(queryWrapper1);
        Long[] roleIds = user.getRoleIds();
        List<UserRole> userRoles=new ArrayList<>();
        for (Long roleId : roleIds) {
            UserRole userRole=new UserRole(user.getId(),roleId);
            userRoles.add(userRole);
        }
        userRoleService.saveBatch(userRoles);
        return ResponseResult.okResult();
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        return count(queryWrapper)>0;
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return count(queryWrapper)>0;
    }

    private boolean passwordExist(String password) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPassword,password);
        return count(queryWrapper)>0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper)>0;
    }
}
