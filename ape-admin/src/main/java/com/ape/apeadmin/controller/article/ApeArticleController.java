package com.ape.apeadmin.controller.article;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeArticle;
import com.ape.apesystem.service.ApeArticleService;
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
 * @description: 美食资讯controller
 * @date 2024/01/03 03:43
 */
@Controller
@ResponseBody
@RequestMapping("article")
public class ApeArticleController {

    @Autowired
    private ApeArticleService apeArticleService;

    /** 分页获取美食资讯 */
    @Log(name = "分页获取美食资讯", type = BusinessType.OTHER)
    @PostMapping("getApeArticlePage")
    public Result getApeArticlePage(@RequestBody ApeArticle apeArticle) {
        Page<ApeArticle> page = new Page<>(apeArticle.getPageNumber(),apeArticle.getPageSize());
        QueryWrapper<ApeArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeArticle.getTitle()),ApeArticle::getTitle,apeArticle.getTitle())
                .orderByDesc(ApeArticle::getCreateTime);
        Page<ApeArticle> apeArticlePage = apeArticleService.page(page, queryWrapper);
        return Result.success(apeArticlePage);
    }

    /** 根据id获取美食资讯 */
    @Log(name = "根据id获取美食资讯", type = BusinessType.OTHER)
    @GetMapping("getApeArticleById")
    public Result getApeArticleById(@RequestParam("id")String id) {
        ApeArticle apeArticle = apeArticleService.getById(id);
        return Result.success(apeArticle);
    }

    @GetMapping("getArticleThree")
    public Result getArticleThree() {
        QueryWrapper<ApeArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ApeArticle::getCreateTime).last("limit 3");
        List<ApeArticle> list = apeArticleService.list(queryWrapper);
        return Result.success(list);
    }

    /** 保存美食资讯 */
    @Log(name = "保存美食资讯", type = BusinessType.INSERT)
    @PostMapping("saveApeArticle")
    public Result saveApeArticle(@RequestBody ApeArticle apeArticle) {
        boolean save = apeArticleService.save(apeArticle);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食资讯 */
    @Log(name = "编辑美食资讯", type = BusinessType.UPDATE)
    @PostMapping("editApeArticle")
    public Result editApeArticle(@RequestBody ApeArticle apeArticle) {
        boolean save = apeArticleService.updateById(apeArticle);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食资讯 */
    @GetMapping("removeApeArticle")
    @Log(name = "删除美食资讯", type = BusinessType.DELETE)
    public Result removeApeArticle(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeArticleService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("美食资讯id不能为空！");
        }
    }

}