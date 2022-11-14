package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.ArticleDto;
import com.ljk.domain.entity.Article;
import com.ljk.domain.entity.ArticleTag;
import com.ljk.domain.entity.SgCategory;
import com.ljk.domain.entity.Tag;
import com.ljk.domain.vo.*;
import com.ljk.mapper.ArticleMapper;
import com.ljk.mapper.ArticleTagMapper;
import com.ljk.service.ArticleService;
import com.ljk.service.ArticleTagService;
import com.ljk.service.SgCategoryService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private SgCategoryService sgCategoryService;
    @Value("${viewCountKey}")
    private String s;
    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult hotArticleList() {
        //查询条件status=0(完整的文章) pageSize=10 pageCurrent=1 按照浏览量降序排列
        LambdaQueryWrapper<Article> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        lambdaQueryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page=new Page<>(SystemConstants.PAGE_CURRENT,SystemConstants.PAGE_SIZE);
        page(page,lambdaQueryWrapper);
        List<Article> records = page.getRecords();
        //从redis中获取viewCount
        records =records.stream().map(article -> {
            Integer viewCount=redisCache.getCacheMapValue(s,article.getId().toString());
            article.setViewCount(viewCount.longValue());
            return article;
        }).collect(Collectors.toList());
        //Bean拷贝将前端需要的数据拷贝到HotArticleVo中并返回
        /*List<HotArticleVo> voList=new ArrayList<>();
        for(Article article : records){
            HotArticleVo hotArticleVo=new HotArticleVo();
            BeanUtils.copyProperties(article,hotArticleVo);
            voList.add(hotArticleVo);
        }*/
        List<HotArticleVo> voList = BeanCopyUtils.copyBeanList(records, HotArticleVo.class);
        return ResponseResult.okResult(voList);
    }

    @Override
    public ResponseResult pageInfo(Integer pageNum, Integer pageSize, Integer categoryId) {
        //查询article表并进行分页
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //对是否传入categoryId进行判断
        queryWrapper.eq(categoryId!=null&&categoryId>0,Article::getCategoryId,categoryId);
        //必须是正式发布的文章
        queryWrapper.eq(Article::getStatus,SystemConstants.STATUS_NORMAL);
        //按照isTop进行降序排序
        queryWrapper.orderByDesc(Article::getIsTop);
        //进行分页查询
        Page<Article> page=new Page(pageNum,pageSize);
        this.page(page, queryWrapper);
        List<Article> articleList = page.getRecords();
        /*for (Article article : articleList) {
            //得到categoryId
            Long categoryId1 = article.getCategoryId();
            //根据id得到categoryName
            SgCategory category = sgCategoryService.getById(categoryId1);
            article.setCategoryName(category.getName());
         }*/
        articleList=articleList.stream().map(article -> {
            //从redis中获取viewCount
            Integer viewCount=redisCache.getCacheMapValue(s,article.getId().toString());
            article.setViewCount(viewCount.longValue());
            article.setCategoryName(sgCategoryService.getById( article.getCategoryId()).getName());
            return article;
        }).collect(Collectors.toList());
        //进行封装vo将前端需要的信息封装进去
        List<PageInfoVo> pageInfoVos = BeanCopyUtils.copyBeanList(articleList, PageInfoVo.class);
        //再将数据封装进分页vo中去
        PageVo pageVo=new PageVo(pageInfoVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据文章id查询文章信息
        Article article = this.getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue(s, id.toString());
        article.setViewCount(viewCount.longValue());
        //封装进vo中去
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //再得到categoryId根据分类id得到分类name
        Long categoryId = article.getCategoryId();
        SgCategory category = sgCategoryService.getById(categoryId);
        String name = category.getName();
        articleDetailVo.setCategoryName(name);
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中的viewCount
        redisCache.incrementCacheMapValue(s,id.toString(),1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional  //多表操作要加事务注解
    public ResponseResult saveArticle(ArticleDto articleDto) {
        //保存文章
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);
        //先获取关联关系
        List<Long> tags = articleDto.getTags();
        List<ArticleTag> articleTagList = tags.stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());
        //保存文章和标签的关联关系
        articleTagService.saveBatch(articleTagList);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getDelFlag,SystemConstants.DEL_FLAG);
        queryWrapper.eq(StringUtils.hasText(title),Article::getTitle,title);
        queryWrapper.eq(StringUtils.hasText(summary),Article::getSummary,summary);
        Page page=new Page(pageNum,pageSize);
        page(page, queryWrapper);
        List articleList = BeanCopyUtils.copyBeanList(page.getRecords(), AdminArticleVo.class);
        PageVo pageVo=new PageVo(articleList,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult articleInfo(Long id) {
        //根据文章id查询文章信息
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,id);
        Article article = getOne(queryWrapper);
        //根据文章id查询标签信息
        LambdaQueryWrapper<ArticleTag> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId,id);
        List<ArticleTag> tagList = articleTagService.list(wrapper);
        List<Long> ids = tagList.stream().map(tag -> tag.getTagId())
                .collect(Collectors.toList());
        article.setTags(ids);
        return ResponseResult.okResult(article);
    }

    @Override
    @Transactional
    public ResponseResult updateArticle(Article article) {
        //先更新文章表
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        update(article,queryWrapper);
        //再更新ArticleTag表
        //先把原来ArticleTag表中的对应articleId的数据删除
        LambdaQueryWrapper<ArticleTag> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(queryWrapper1);
        //再把前端传过来的数据添加进去
        List<ArticleTag> articleTags = article.getTags().stream()
                .map(tag -> new ArticleTag(article.getId(), tag))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticle(Long id) {
        getBaseMapper().deleteArticleById(id);
        return ResponseResult.okResult();
    }
}
