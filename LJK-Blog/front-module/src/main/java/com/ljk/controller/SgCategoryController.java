package com.ljk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgCategory;
import com.ljk.domain.vo.CategoryVo;
import com.ljk.service.SgCategoryService;
import com.ljk.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class SgCategoryController {
    @Autowired
    private SgCategoryService sgCategoryService;

    @GetMapping("/getCategoryList")
    public ResponseResult getCategoryList(){
       ResponseResult result= sgCategoryService.getCategoryList();
       return  result;
    }
}
