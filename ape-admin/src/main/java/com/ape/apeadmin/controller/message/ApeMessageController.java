package com.ape.apeadmin.controller.message;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeMessage;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeMessageService;
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
 * @description: 留言controller
 * @date 2024/01/04 09:20
 */
@Controller
@ResponseBody
@RequestMapping("message")
public class ApeMessageController {

    @Autowired
    private ApeMessageService apeMessageService;

    /** 分页获取留言 */
    @Log(name = "分页获取留言", type = BusinessType.OTHER)
    @PostMapping("getApeMessagePage")
    public Result getApeMessagePage(@RequestBody ApeMessage apeMessage) {
        Page<ApeMessage> page = new Page<>(apeMessage.getPageNumber(),apeMessage.getPageSize());
        QueryWrapper<ApeMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeMessage.getUserId()),ApeMessage::getUserId,apeMessage.getUserId())
                .like(StringUtils.isNotBlank(apeMessage.getContent()),ApeMessage::getContent,apeMessage.getContent())
                .like(StringUtils.isNotBlank(apeMessage.getCreateBy()),ApeMessage::getCreateBy,apeMessage.getCreateBy())
                .orderByDesc(ApeMessage::getCreateTime);
        Page<ApeMessage> apeMessagePage = apeMessageService.page(page, queryWrapper);
        return Result.success(apeMessagePage);
    }

    /** 根据id获取留言 */
    @Log(name = "根据id获取留言", type = BusinessType.OTHER)
    @GetMapping("getApeMessageById")
    public Result getApeMessageById(@RequestParam("id")String id) {
        ApeMessage apeMessage = apeMessageService.getById(id);
        return Result.success(apeMessage);
    }

    /** 保存留言 */
    @Log(name = "保存留言", type = BusinessType.INSERT)
    @PostMapping("saveApeMessage")
    public Result saveApeMessage(@RequestBody ApeMessage apeMessage) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeMessage.setUserId(user.getId());
        boolean save = apeMessageService.save(apeMessage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑留言 */
    @Log(name = "编辑留言", type = BusinessType.UPDATE)
    @PostMapping("editApeMessage")
    public Result editApeMessage(@RequestBody ApeMessage apeMessage) {
        boolean save = apeMessageService.updateById(apeMessage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除留言 */
    @GetMapping("removeApeMessage")
    @Log(name = "删除留言", type = BusinessType.DELETE)
    public Result removeApeMessage(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeMessageService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("留言id不能为空！");
        }
    }

}