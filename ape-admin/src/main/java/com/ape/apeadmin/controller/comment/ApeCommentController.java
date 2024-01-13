package com.ape.apeadmin.controller.comment;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeComment;
import com.ape.apesystem.service.ApeCommentService;
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
 * @description: 美食点评controller
 * @date 2024/01/03 04:44
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class ApeCommentController {

    @Autowired
    private ApeCommentService apeCommentService;

    /** 分页获取美食点评 */
    @Log(name = "分页获取美食点评", type = BusinessType.OTHER)
    @PostMapping("getApeCommentPage")
    public Result getApeCommentPage(@RequestBody ApeComment apeComment) {
        Page<ApeComment> page = new Page<>(apeComment.getPageNumber(),apeComment.getPageSize());
        QueryWrapper<ApeComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeComment.getTitle()),ApeComment::getTitle,apeComment.getTitle())
                .eq(StringUtils.isNotBlank(apeComment.getCreateBy()),ApeComment::getCreateBy,apeComment.getCreateBy())
                .eq(StringUtils.isNotBlank(apeComment.getUserId()),ApeComment::getUserId,apeComment.getUserId())
                .eq(apeComment.getState() != null,ApeComment::getState,apeComment.getState())
                .orderByDesc(ApeComment::getCreateTime);
        Page<ApeComment> apeCommentPage = apeCommentService.page(page, queryWrapper);
        return Result.success(apeCommentPage);
    }

    /** 根据id获取美食点评 */
    @Log(name = "根据id获取美食点评", type = BusinessType.OTHER)
    @GetMapping("getApeCommentById")
    public Result getApeCommentById(@RequestParam("id")String id) {
        ApeComment apeComment = apeCommentService.getById(id);
        return Result.success(apeComment);
    }

    /** 保存美食点评 */
    @Log(name = "保存美食点评", type = BusinessType.INSERT)
    @PostMapping("saveApeComment")
    public Result saveApeComment(@RequestBody ApeComment apeComment) {
        boolean save = apeCommentService.save(apeComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食点评 */
    @Log(name = "编辑美食点评", type = BusinessType.UPDATE)
    @PostMapping("editApeComment")
    public Result editApeComment(@RequestBody ApeComment apeComment) {
        boolean save = apeCommentService.updateById(apeComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食点评 */
    @GetMapping("removeApeComment")
    @Log(name = "删除美食点评", type = BusinessType.DELETE)
    public Result removeApeComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食点评id不能为空！");
        }
    }

    @GetMapping("getCommentThree")
    public Result getCommentThree() {
        QueryWrapper<ApeComment> queryWrapper =  new QueryWrapper<ApeComment>();
        queryWrapper.lambda().eq(ApeComment::getState,1).orderByDesc(ApeComment::getCreateTime)
                .last("limit 3");
        List<ApeComment> commentList = apeCommentService.list(queryWrapper);
        return Result.success(commentList);
    }

}