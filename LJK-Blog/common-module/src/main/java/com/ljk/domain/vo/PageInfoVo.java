package com.ljk.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoVo {
    private String categoryName;
    private Date createTime;
    private Long id;
    private String summary;
    private String thumbnail;
    private String title;
    private Long viewCount;
}
