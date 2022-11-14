package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.TagDto;
import com.ljk.domain.entity.Tag;
import com.ljk.domain.vo.TagVo;

public interface TagService extends IService<Tag> {
    ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagDto tagDto);

    ResponseResult addTag(TagDto tagDto);

    ResponseResult deleteTag(Integer id);

    ResponseResult getTagInfo(Integer id);

    ResponseResult updateTagInfo(TagVo tagVo);

    ResponseResult listAllTag();
}
