package com.ljk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.domain.entity.ArticleTag;
import com.ljk.mapper.ArticleTagMapper;
import com.ljk.service.ArticleTagService;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
