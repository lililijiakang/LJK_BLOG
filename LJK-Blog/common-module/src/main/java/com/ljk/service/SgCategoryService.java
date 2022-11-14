package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgCategory;

public interface SgCategoryService extends IService<SgCategory> {
    ResponseResult getCategoryList();

    ResponseResult listAllCategory();

    ResponseResult getCategoryPageInfo(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult getCategory(Long id);
}
