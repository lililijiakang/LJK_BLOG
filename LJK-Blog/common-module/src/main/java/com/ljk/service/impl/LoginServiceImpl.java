package com.ljk.service.impl;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.LoginUser;
import com.ljk.domain.entity.User;
import com.ljk.domain.entity.UserInfoVo;
import com.ljk.domain.vo.BlogUserLoginVo;
import com.ljk.service.BlogLoginService;
import com.ljk.service.LoginService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.JwtUtils;
import com.ljk.utils.RedisCache;
import com.ljk.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        //进行认证
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断认证是否通过
        if(authenticate==null){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userId生成token
        LoginUser loginUser= (LoginUser) authenticate.getPrincipal();
        String id = String.valueOf(loginUser.getUser().getId());
        String jwt = JwtUtils.createJWT(id);
        //把用户信息存入redis
        redisCache.setCacheObject("login:"+id,loginUser);
        //把token封装返回
        Map<String,String> map=new HashMap<>();
        map.put("token",jwt);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中的数据
        redisCache.deleteObject("login:"+userId);
        return ResponseResult.okResult();
    }
}
