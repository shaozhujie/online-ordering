package com.ape.apeadmin.controller.tag;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeTag;
import com.ape.apesystem.service.ApeTagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 标签controller
 * @date 2023/12/25 09:07
 */
@Controller
@ResponseBody
@RequestMapping("tag")
public class ApeTagController {

    @Autowired
    private ApeTagService apeTagService;

    /** 分页获取标签 */
    @Log(name = "分页获取标签", type = BusinessType.OTHER)
    @PostMapping("getApeTagPage")
    public Result getApeTagPage(@RequestBody ApeTag apeTag) {
        Page<ApeTag> page = new Page<>(apeTag.getPageNumber(),apeTag.getPageSize());
        QueryWrapper<ApeTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeTag.getType()),ApeTag::getType,apeTag.getType())
                .like(StringUtils.isNotBlank(apeTag.getName()),ApeTag::getName,apeTag.getName());
        Page<ApeTag> apeTagPage = apeTagService.page(page, queryWrapper);
        return Result.success(apeTagPage);
    }

    @GetMapping("getTagList")
    public Result getTagList() {
        List<ApeTag> tagList = apeTagService.list();
        return Result.success(tagList);
    }

    /** 根据id获取标签 */
    @Log(name = "根据id获取标签", type = BusinessType.OTHER)
    @GetMapping("getApeTagById")
    public Result getApeTagById(@RequestParam("id")String id) {
        ApeTag apeTag = apeTagService.getById(id);
        return Result.success(apeTag);
    }

    /** 保存标签 */
    @Log(name = "保存标签", type = BusinessType.INSERT)
    @PostMapping("saveApeTag")
    public Result saveApeTag(@RequestBody ApeTag apeTag) {
        boolean save = apeTagService.save(apeTag);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑标签 */
    @Log(name = "编辑标签", type = BusinessType.UPDATE)
    @PostMapping("editApeTag")
    public Result editApeTag(@RequestBody ApeTag apeTag) {
        boolean save = apeTagService.updateById(apeTag);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除标签 */
    @GetMapping("removeApeTag")
    @Log(name = "删除标签", type = BusinessType.DELETE)
    public Result removeApeTag(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTagService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("标签id不能为空！");
        }
    }

}