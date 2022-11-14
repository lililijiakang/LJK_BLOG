package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.Menu;
import com.ljk.domain.vo.MenuTreeVo;
import com.ljk.service.MenuService;
import com.ljk.utils.SystemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 查询所有菜单信息返回给前端
     * @param status
     * @param menuName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getMenuInfoList(String status,String menuName){
           return menuService.getMenuInfoList(status,menuName);
    }

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    @PostMapping
    public ResponseResult saveMenu(@RequestBody Menu menu){
        return menuService.saveMenu(menu);
    }

    /**
     * 根据id查询菜单,回显给前端
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult menuInfo(@PathVariable("id") Long id){
        return menuService.menuInfo(id);
    }

    /**
     * 更新菜单
     * @param menu
     * @return
     */
    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable("menuId") Long id){
        return menuService.deleteMenu(id);
    }

    /**
     * 新增角色时的将菜单树回显给前端
     * @return
     */
    @GetMapping("/treeselect")
    public ResponseResult treeSelect(){
        List<Menu> menus = menuService.list();
        List<MenuTreeVo> options =  SystemConverter.buildMenuSelectTree(menus);
        return ResponseResult.okResult(options);
    }

    /**
     * 修改角色时回显的角色所能具有权限的菜单信息
     * @param id
     * @return
     */
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselect(@PathVariable("id") Long id){
        return menuService.roleMenuTreeselect(id);
    }
}
