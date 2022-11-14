package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.Menu;
import com.ljk.domain.entity.RoleMenu;
import com.ljk.domain.vo.MenuTreeVo;
import com.ljk.domain.vo.RoleMenuTreeVo;
import com.ljk.enums.AppHttpCodeEnum;
import com.ljk.mapper.MenuMapper;
import com.ljk.mapper.RoleMenuMapper;
import com.ljk.service.MenuService;
import com.ljk.service.RoleMenuService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.SecurityUtils;
import com.ljk.utils.SystemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是超级管理员,则查询所有权限
        if(id==1L){
            LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU,SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(queryWrapper);
            List<String> perms = menus.stream().map(menu -> menu.getPerms()).collect(Collectors.toList());
            return perms;
        }
        //否则查询所具有的权限
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        //判断当前登录用户是否是超级管理员,是就返回所有menu信息
        List<Menu> menus=null;
        if(SecurityUtils.isAdmin()){
            menus=getBaseMapper().selectAllMenu();
        }else{
            //否则查询当前用户的menu信息返回
            menus=getBaseMapper().selectRouterMenuByUserId(userId);
        }
        //构建tree形式
        List<Menu> menusTree=builderTree(menus,0L);
        return menusTree;
    }

    @Override
    public ResponseResult getMenuInfoList(String status, String menuName) {
        //判断前端是否对status和menuName进行模糊查询
        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(status),Menu::getStatus,status);
        queryWrapper.eq(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        //对菜单的id和orderNum进行排序
        queryWrapper.orderByAsc(Menu::getParentId);
        queryWrapper.orderByAsc(Menu::getOrderNum);
        //进行查询
        List<Menu> menus = list(queryWrapper);
        return ResponseResult.okResult(menus);
    }

    @Override
    public ResponseResult saveMenu(Menu menu) {
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult menuInfo(Long id) {
        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getId,id);
        Menu menu = getOne(queryWrapper);
        return ResponseResult.okResult(menu);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        //能够修改菜单，但是修改的时候不能把父菜单设置为当前菜单，
        // 如果设置了需要给出相应的提示。并且修改失败。
        if(menu.getParentId().equals(menu.getId())){
            //此处Long类型的俩个数值不能直接用==比较是否相同
            //如果不在[-127,128]之间，则会new一个新对象，自然“==”两个不同的对象，其结果必然是false了
            return ResponseResult.errorResult(AppHttpCodeEnum.MENU_EXCEPTION);
        }
        //更新菜单
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long id) {
        //能够删除菜单，但是如果要删除的菜单有子菜单则提示：存在子菜单不允许删除 并且删除失败
        //根据id查询菜单信息
        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getId,id);
        Menu menu = getOne(queryWrapper);
        //判断此菜单中是否有子菜单
        LambdaQueryWrapper<Menu> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Menu::getParentId,menu.getId());
        List<Menu> list = list(queryWrapper1);
        if(list.size()>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.DELETE_MENU_EXCEPTION);
        }
        remove(queryWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult roleMenuTreeselect(Long id) {
        //获取角色对应菜单集合
        List<Menu> menus=null;
        List<RoleMenu> checkedKeys =null;
        List<Long> keys =null;
        Long[] checkedKeyL=null;
        if(id.equals(1L)){
            menus=getBaseMapper().selectAllMenu();
            checkedKeys=roleMenuMapper.selectAllMenuId();
            keys = checkedKeys.stream().map(check -> check.getMenuId())
                    .collect(Collectors.toList());
            checkedKeyL= keys.toArray(new Long[0]);
        }else{
            //否则查询当前用户的menu信息返回
            menus=getBaseMapper().selectRouterMenuByRoleId(id);
            //先根据角色id查询角色关联的菜单权限id
            checkedKeys = roleMenuMapper.selectMenuIdByRoleId(id);
            keys = checkedKeys.stream().map(checkedKey -> checkedKey.getMenuId())
                    .collect(Collectors.toList());
            checkedKeyL=keys.toArray(new Long[0]); //list集合转为数组
        }
        //构建tree
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(menus);
        //封装进RoleMenuTreeVo中
        RoleMenuTreeVo roleMenuTreeVo=new RoleMenuTreeVo(menuTreeVos,checkedKeyL);
        return ResponseResult.okResult(roleMenuTreeVo);
    }

    private List<Menu> builderTree(List<Menu> menus, long parentId) {
        //找到没有根目录的层级(parentId为0的)并将其子目录设置为children
        List<Menu> menusTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menusTree;
    }

    /**
     * 得到menu的子目录
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrens = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
        return childrens;
    }
}
