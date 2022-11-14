package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.domain.dto.TagDto;
import com.ljk.domain.vo.TagVo;
import com.ljk.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult getTagList(Integer pageNum, Integer pageSize, TagDto tagDto){
        return tagService.pageTagList(pageNum,pageSize,tagDto);
    }

    @PostMapping
    public ResponseResult addTag(@RequestBody TagDto tagDto){
        return tagService.addTag(tagDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable("id") Integer id){
        return tagService.deleteTag(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getTagInfo(@PathVariable("id") Integer id){
        return tagService.getTagInfo(id);
    }

    @PutMapping
    public ResponseResult updateTagInfo(@RequestBody TagVo tagVo){
        return tagService.updateTagInfo(tagVo);
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTag();
    }
}
