package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.Article;
import com.ljk.domain.entity.SgCategory;
import com.ljk.domain.vo.AdminCategoryVo;
import com.ljk.domain.vo.CategoryVo;
import com.ljk.domain.vo.PageVo;
import com.ljk.mapper.SgCategoryMapper;
import com.ljk.service.ArticleService;
import com.ljk.service.SgCategoryService;
import com.ljk.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SgCategoryServiceImpl extends ServiceImpl<SgCategoryMapper, SgCategory> implements SgCategoryService {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SgCategoryService sgCategoryService;
    @Override
    public ResponseResult getCategoryList() {
        //先查询文章表(正常可发布的文章)
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //查询文章表的分类id
        List<Article> list = articleService.list(queryWrapper);
        Set<Long> categoryIds=  list.stream().map(article -> {
            return article.getCategoryId();
        }).collect(Collectors.toSet()); //set可以去重,因为article表中的文章的分类id可能重复
        //由分类id再查询SgCategory表
        List<SgCategory> categoryList = this.listByIds(categoryIds);
        categoryList=categoryList.stream().filter(category->{
            return SystemConstants.STATUS_NORMAL.equals(category.getStatus());
        }).collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public ResponseResult listAllCategory() {
        //查询所有status为0的分类信息
        LambdaQueryWrapper<SgCategory> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SgCategory::getStatus,SystemConstants.STATUS_NORMAL);
        List<SgCategory> list = list(queryWrapper);
        //封装到AdminCategoryVo中
        List<AdminCategoryVo> adminCategoryVos = list.stream()
                .map(sgCategory -> new AdminCategoryVo(sgCategory.getId(), sgCategory.getName(), sgCategory.getDescription()))
                .collect(Collectors.toList());
        return ResponseResult.okResult(adminCategoryVos);
    }

    @Override
    public ResponseResult getCategoryPageInfo(Integer pageNum, Integer pageSize, String name, String status) {
        //先判断有无模糊查询
        LambdaQueryWrapper<SgCategory> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(name),SgCategory::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),SgCategory::getStatus,status);
        //分页
        Page page=new Page(pageNum,pageSize);
        page(page,queryWrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    public ResponseResult getCategory(Long id) {
        LambdaQueryWrapper<SgCategory> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SgCategory::getId,id);
        SgCategory category = sgCategoryService.getOne(queryWrapper);
        return ResponseResult.okResult(category);
    }
}
