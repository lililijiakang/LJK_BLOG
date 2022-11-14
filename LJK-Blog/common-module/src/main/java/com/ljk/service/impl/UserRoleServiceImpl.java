package com.ljk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.domain.entity.UserRole;
import com.ljk.mapper.UserRoleMapper;
import com.ljk.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
