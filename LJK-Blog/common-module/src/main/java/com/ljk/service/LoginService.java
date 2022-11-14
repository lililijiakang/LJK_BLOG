package com.ljk.service;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
