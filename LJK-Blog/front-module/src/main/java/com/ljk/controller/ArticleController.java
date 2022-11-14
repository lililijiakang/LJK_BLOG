package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.Article;
import com.ljk.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /*@GetMapping("/list")
    public List<Article> list(){
       return articleService.list();
    }*/

    @GetMapping("/hotArticleList")
    public ResponseResult<Article> hotArticleList(){
        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Integer categoryId){
       ResponseResult result= articleService.pageInfo(pageNum,pageSize,categoryId);
       return result;
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }
}
