package com.ljk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ljk.constants.SystemConstants;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.entity.SgLink;
import com.ljk.service.SgLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {
    @Autowired
    private SgLinkService sgLinkService;

    /**
     * 友链分页及模糊查询
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult linkPageInfo(Integer pageNum,Integer pageSize,String name,String status){
        return sgLinkService.linkPageInfo(pageNum,pageSize,name,status);
    }

    /**
     * 新增友链
     * @param sgLink
     * @return
     */
    @PostMapping
    public ResponseResult saveLink(@RequestBody SgLink sgLink){
        sgLinkService.save(sgLink);
        return ResponseResult.okResult();
    }

    /**
     * 修改友链时回显的信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getLinkInfo(@PathVariable("id") Long id){
        return sgLinkService.getLinkInfo(id);
    }

    /**
     * 修改友链
     * @param sgLink
     * @return
     */
    @PutMapping
    public ResponseResult updateLink(@RequestBody SgLink sgLink){
        LambdaQueryWrapper<SgLink> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SgLink::getId,sgLink.getId());
        sgLinkService.remove(queryWrapper);
        sgLinkService.save(sgLink);
        return ResponseResult.okResult();
    }

    /**
     * 删除友链(逻辑删除)
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable("id") Long id){
        UpdateWrapper<SgLink> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        updateWrapper.set("del_flag", SystemConstants.LINK_DEL);
        sgLinkService.update(updateWrapper);
        return ResponseResult.okResult();
    }
}
