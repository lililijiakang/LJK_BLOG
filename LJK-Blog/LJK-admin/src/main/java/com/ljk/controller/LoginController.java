package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.LoginUser;
import com.ljk.domain.entity.Menu;
import com.ljk.domain.entity.User;
import com.ljk.domain.entity.UserInfoVo;
import com.ljk.domain.vo.AdminUserInfoVo;
import com.ljk.domain.vo.RoutersVo;
import com.ljk.enums.AppHttpCodeEnum;
import com.ljk.exception.SystemException;
import com.ljk.service.BlogLoginService;
import com.ljk.service.LoginService;
import com.ljk.service.MenuService;
import com.ljk.service.RoleService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @GetMapping("getInfo")
    public ResponseResult getInfo(){
        //获取当前登录的用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据用户id查询权限信息
        List<String> perms=menuService.selectPermsByUserId(loginUser.getUser().getId());
        //根据用户id查询角色信息
       List<String> roleKeyList=roleService.selectRoleKeyByUserId(loginUser.getUser().getId());
        //将user信息封装到userInfo中
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(),UserInfoVo.class);
        //将信息封装到AdminUserInfoVo
        AdminUserInfoVo adminUserInfoVo=new AdminUserInfoVo(perms,roleKeyList,userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }

    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
