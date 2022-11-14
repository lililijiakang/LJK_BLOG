package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.User;
import com.ljk.enums.AppHttpCodeEnum;
import com.ljk.exception.SystemException;
import com.ljk.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {
    @Autowired
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
          if(user.getUserName()==null){
              throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
          }
          return blogLoginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}
