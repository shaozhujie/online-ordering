package com.ape.apeadmin.controller.type;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeType;
import com.ape.apesystem.service.ApeTypeService;
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
 * @description: 分类controller
 * @date 2023/12/23 11:31
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ApeTypeController {

    @Autowired
    private ApeTypeService apeTypeService;

    /** 分页获取分类 */
    @Log(name = "分页获取分类", type = BusinessType.OTHER)
    @PostMapping("getApeTypePage")
    public Result getApeTypePage(@RequestBody ApeType apeType) {
        Page<ApeType> page = new Page<>(apeType.getPageNumber(),apeType.getPageSize());
        QueryWrapper<ApeType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeType.getName()),ApeType::getName,apeType.getName());
        Page<ApeType> apeTypePage = apeTypeService.page(page, queryWrapper);
        return Result.success(apeTypePage);
    }

    @GetMapping("getTypeList")
    public Result getTypeList() {
        List<ApeType> list = apeTypeService.list();
        return Result.success(list);
    }

    /** 根据id获取分类 */
    @Log(name = "根据id获取分类", type = BusinessType.OTHER)
    @GetMapping("getApeTypeById")
    public Result getApeTypeById(@RequestParam("id")String id) {
        ApeType apeType = apeTypeService.getById(id);
        return Result.success(apeType);
    }

    /** 保存分类 */
    @Log(name = "保存分类", type = BusinessType.INSERT)
    @PostMapping("saveApeType")
    public Result saveApeType(@RequestBody ApeType apeType) {
        boolean save = apeTypeService.save(apeType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑分类 */
    @Log(name = "编辑分类", type = BusinessType.UPDATE)
    @PostMapping("editApeType")
    public Result editApeType(@RequestBody ApeType apeType) {
        boolean save = apeTypeService.updateById(apeType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除分类 */
    @GetMapping("removeApeType")
    @Log(name = "删除分类", type = BusinessType.DELETE)
    public Result removeApeType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTypeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("分类id不能为空！");
        }
    }

}