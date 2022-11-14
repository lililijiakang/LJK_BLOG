package com.ljk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.TagDto;
import com.ljk.domain.entity.Tag;
import com.ljk.domain.vo.PageVo;
import com.ljk.domain.vo.TagVo;
import com.ljk.mapper.TagMapper;
import com.ljk.service.TagService;
import com.ljk.utils.BeanCopyUtils;
import com.ljk.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagDto tagDto) {
        //先判断是否根据标签名或备注查询,没有直接分页查询
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(tagDto.getName()),Tag::getName,tagDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagDto.getRemark()),Tag::getRemark,tagDto.getRemark());
        //再分页查询
        Page<Tag> page=new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        this.page(page,queryWrapper);
        //将查询到的list集合封装到TagVo中
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(page.getRecords(), TagVo.class);
        PageVo pageVo=new PageVo(tagVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(TagDto tagDto) {
        Tag tag=new Tag();
        tag.setName(tagDto.getName());
        tag.setRemark(tagDto.getRemark());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setCreateBy(SecurityUtils.getUserId());
        tag.setUpdateBy(SecurityUtils.getUserId());
        //保存数据
        save(tag);
        return ResponseResult.okResult();
    }

    /**
     * 注意此处的删除为逻辑删除,只是修改了数据库表中的del_flag的值,使他在前端页面展示不出
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteTag(Integer id) {
        //修改指定id的del_flag操作
        getBaseMapper().logicDeleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTagInfo(Integer id) {
        //根据id查询tag信息并返回
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getId,id);
        Tag tag = getOne(queryWrapper);
        //封装到tagVo中
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public ResponseResult updateTagInfo(TagVo tagVo) {
        //根据前端返回的数据进行修改tag
        //获取id,修改指定id的tag
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getId,tagVo.getId());
        Tag tag=new Tag();
        tag.setName(tagVo.getName());
        tag.setRemark(tagVo.getRemark());
        update(tag,queryWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllTag() {
        List<Tag> tags = list();
        List<TagVo> tagVos = tags.stream()
                .map(tag -> new TagVo(tag.getId(), tag.getName(), null))
                .collect(Collectors.toList());
        return ResponseResult.okResult(tagVos);
    }
}
