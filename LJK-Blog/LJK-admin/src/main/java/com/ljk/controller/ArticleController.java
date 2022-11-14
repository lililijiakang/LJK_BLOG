package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.ArticleDto;
import com.ljk.domain.entity.Article;
import com.ljk.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 写博文
     * @param articleDto
     * @return
     */
    @PostMapping
    public ResponseResult saveArticle(@RequestBody ArticleDto articleDto){
        return articleService.saveArticle(articleDto);
    }

    /**
     * 文章分页查询和模糊查询
     * @param pageNum
     * @param pageSize
     * @param title
     * @param summary
     * @return
     */
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum,Integer pageSize,String title,String summary){
        return articleService.articleList(pageNum,pageSize,title,summary);
    }

    /**
     * 修改文章时回显页面的文章数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult articleInfo(@PathVariable("id") Long id){
        return articleService.articleInfo(id);
    }

    /**
     * 更新文章
     * @param article
     * @return
     */
    @PutMapping
    public ResponseResult updateArticle(@RequestBody Article article){
        return articleService.updateArticle(article);
    }

    /**
     * 删除id对应的文章(逻辑删除)
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteArticle(@PathVariable("id") Long id){
        return articleService.deleteArticle(id);
    }
}
