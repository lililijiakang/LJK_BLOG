package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgLink;
import com.ljk.domain.vo.LinkVo;
import com.ljk.domain.vo.PageVo;
import com.ljk.mapper.SgLinkMapper;
import com.ljk.service.SgLinkService;
import com.ljk.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SgLinkServiceImpl extends ServiceImpl<SgLinkMapper, SgLink> implements SgLinkService {
    @Override
    public ResponseResult getAllLink() {
        //查出已经审核通过的链接
        LambdaQueryWrapper<SgLink> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SgLink::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<SgLink> list = this.list(lambdaQueryWrapper);
        //封装到vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(list, LinkVo.class);

        return ResponseResult.okResult(linkVos);

    }

    @Override
    public ResponseResult linkPageInfo(Integer pageNum, Integer pageSize, String name, String status) {
        //先判断有无模糊查询
        LambdaQueryWrapper<SgLink> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(name),SgLink::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),SgLink::getStatus,status);
        //分页
        Page page=new Page(pageNum,pageSize);
        page(page,queryWrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    public ResponseResult getLinkInfo(Long id) {
        //根据id查询友链信息
        LambdaQueryWrapper<SgLink> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SgLink::getId,id);
        SgLink link = getOne(queryWrapper);
        return ResponseResult.okResult(link);
    }
}
