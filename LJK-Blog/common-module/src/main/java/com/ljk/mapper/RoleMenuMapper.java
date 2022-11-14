package com.ljk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljk.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    List<RoleMenu> selectAllMenuId();

    List<RoleMenu> selectMenuIdByRoleId(Long id);
}
