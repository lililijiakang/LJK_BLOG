package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.ArticleDto;
import com.ljk.domain.entity.Article;

public interface ArticleService extends IService<Article> {

    ResponseResult hotArticleList();

    ResponseResult pageInfo(Integer pageNum, Integer pageSize, Integer categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult saveArticle(ArticleDto articleDto);

    ResponseResult articleList(Integer pageNum, Integer pageSize, String title, String summary);

    ResponseResult articleInfo(Long id);

    ResponseResult updateArticle(Article article);

    ResponseResult deleteArticle(Long id);
}
