package com.ljk.domain.vo;

import com.ljk.domain.entity.Role;
import com.ljk.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleVo {
    private Long[] roleIds;
    private List<Role> roles;
    private User user;
}
