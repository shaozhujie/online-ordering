package com.ape.apeadmin.controller.food;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeFoodComment;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeFoodCommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食评论controller
 * @date 2024/01/08 01:59
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class ApeFoodCommentController {

    @Autowired
    private ApeFoodCommentService apeFoodCommentService;

    /** 分页获取美食评论 */
    @Log(name = "分页获取美食评论", type = BusinessType.OTHER)
    @PostMapping("getApeFoodCommentPage")
    public Result getApeFoodCommentPage(@RequestBody ApeFoodComment apeFoodComment) {
        Page<ApeFoodComment> page = new Page<>(apeFoodComment.getPageNumber(),apeFoodComment.getPageSize());
        QueryWrapper<ApeFoodComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeFoodComment.getContent()),ApeFoodComment::getContent,apeFoodComment.getContent())
                .eq(StringUtils.isNotBlank(apeFoodComment.getFoodId()),ApeFoodComment::getFoodId,apeFoodComment.getFoodId())
                .like(StringUtils.isNotBlank(apeFoodComment.getCreateBy()),ApeFoodComment::getCreateBy,apeFoodComment.getCreateBy())
                .orderByDesc(ApeFoodComment::getCreateTime);
        Page<ApeFoodComment> apeFoodCommentPage = apeFoodCommentService.page(page, queryWrapper);
        return Result.success(apeFoodCommentPage);
    }

    /** 根据id获取美食评论 */
    @Log(name = "根据id获取美食评论", type = BusinessType.OTHER)
    @GetMapping("getApeFoodCommentById")
    public Result getApeFoodCommentById(@RequestParam("id")String id) {
        ApeFoodComment apeFoodComment = apeFoodCommentService.getById(id);
        return Result.success(apeFoodComment);
    }

    /** 保存美食评论 */
    @Log(name = "保存美食评论", type = BusinessType.INSERT)
    @PostMapping("saveApeFoodComment")
    public Result saveApeFoodComment(@RequestBody ApeFoodComment apeFoodComment) {
        ApeUser apeUser = ShiroUtils.getUserInfo();
        apeFoodComment.setAvatar(apeUser.getAvatar());
        apeFoodComment.setUserId(apeUser.getId());
        boolean save = apeFoodCommentService.save(apeFoodComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食评论 */
    @Log(name = "编辑美食评论", type = BusinessType.UPDATE)
    @PostMapping("editApeFoodComment")
    public Result editApeFoodComment(@RequestBody ApeFoodComment apeFoodComment) {
        boolean save = apeFoodCommentService.updateById(apeFoodComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食评论 */
    @GetMapping("removeApeFoodComment")
    @Log(name = "删除美食评论", type = BusinessType.DELETE)
    public Result removeApeFoodComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeFoodCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食评论id不能为空！");
        }
    }

}