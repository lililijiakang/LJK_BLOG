package com.ljk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgLink;

public interface SgLinkService extends IService<SgLink> {
    ResponseResult getAllLink();

    ResponseResult linkPageInfo(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult getLinkInfo(Long id);
}
