package com.ljk.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailVo {
    private Long categoryId;
    /**
     * 文章内容
     */
    private String content;
    private String categoryName;
    private Date createTime;
    private Long id;
    private String summary;
    private String thumbnail;
    private String title;
    private Long viewCount;
}
