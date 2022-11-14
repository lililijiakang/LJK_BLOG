package com.ljk.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgCategory;
import com.ljk.domain.vo.ExcelCategoryVo;
import com.ljk.enums.AppHttpCodeEnum;
import com.ljk.service.SgCategoryService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private SgCategoryService sgCategoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        return sgCategoryService.listAllCategory();
    }

    @PreAuthorize("@ps.hasPermission('content:category:export')")
    //判断当前用户是否有执行此操作的权限,有则返回true可以执行此操作,否不能执行此操作
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<SgCategory> categoryVos = sgCategoryService.list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);
        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    /**
     * 分类分页查询及模糊查询
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getCategoryPageInfo(Integer pageNum,Integer pageSize,String name,String status){
        return sgCategoryService.getCategoryPageInfo(pageNum,pageSize,name,status);
    }

    /**
     * 新增分类
     * @param sgCategory
     * @return
     */
    @PostMapping
    public ResponseResult saveCategory(@RequestBody SgCategory sgCategory){
        sgCategoryService.save(sgCategory);
        return ResponseResult.okResult();
    }

    /**
     * 修改分类时回显数据接口
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getCategory(@PathVariable("id") Long id){
        return sgCategoryService.getCategory(id);
    }

    /**
     * 修改分类
     * @param sgCategory
     * @return
     */
    @PutMapping
    public ResponseResult updateCategory(@RequestBody SgCategory sgCategory){
        LambdaQueryWrapper<SgCategory> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SgCategory::getId,sgCategory.getId());
        sgCategoryService.remove(queryWrapper);
        sgCategoryService.save(sgCategory);
        return ResponseResult.okResult();
    }

    /**
     * 逻辑删除分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteCategory(@PathVariable("id") Long id){
        UpdateWrapper<SgCategory> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        updateWrapper.set("del_flag", SystemConstants.CATEGORY_LOGIC_DEL);
        sgCategoryService.update(updateWrapper);
        return ResponseResult.okResult();
    }
}
