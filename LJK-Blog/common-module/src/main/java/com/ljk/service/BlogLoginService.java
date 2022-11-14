package com.ljk.service;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
