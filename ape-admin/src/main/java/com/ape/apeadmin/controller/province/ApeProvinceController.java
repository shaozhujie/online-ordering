package com.ape.apeadmin.controller.province;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeProvince;
import com.ape.apesystem.service.ApeProvinceService;
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
 * @description: 省份controller
 * @date 2023/12/23 09:03
 */
@Controller
@ResponseBody
@RequestMapping("province")
public class ApeProvinceController {

    @Autowired
    private ApeProvinceService apeProvinceService;

    /** 分页获取省份 */
    @Log(name = "分页获取省份", type = BusinessType.OTHER)
    @PostMapping("getApeProvincePage")
    public Result getApeProvincePage(@RequestBody ApeProvince apeProvince) {
        Page<ApeProvince> page = new Page<>(apeProvince.getPageNumber(),apeProvince.getPageSize());
        QueryWrapper<ApeProvince> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeProvince.getName()),ApeProvince::getName,apeProvince.getName());
        Page<ApeProvince> apeProvincePage = apeProvinceService.page(page, queryWrapper);
        return Result.success(apeProvincePage);
    }

    @GetMapping("getApeProvinceList")
    public Result getApeProvinceList() {
        List<ApeProvince> list = apeProvinceService.list();
        return Result.success(list);
    }

    /** 根据id获取省份 */
    @Log(name = "根据id获取省份", type = BusinessType.OTHER)
    @GetMapping("getApeProvinceById")
    public Result getApeProvinceById(@RequestParam("id")String id) {
        ApeProvince apeProvince = apeProvinceService.getById(id);
        return Result.success(apeProvince);
    }

    /** 保存省份 */
    @Log(name = "保存省份", type = BusinessType.INSERT)
    @PostMapping("saveApeProvince")
    public Result saveApeProvince(@RequestBody ApeProvince apeProvince) {
        boolean save = apeProvinceService.save(apeProvince);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑省份 */
    @Log(name = "编辑省份", type = BusinessType.UPDATE)
    @PostMapping("editApeProvince")
    public Result editApeProvince(@RequestBody ApeProvince apeProvince) {
        boolean save = apeProvinceService.updateById(apeProvince);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除省份 */
    @GetMapping("removeApeProvince")
    @Log(name = "删除省份", type = BusinessType.DELETE)
    public Result removeApeProvince(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeProvinceService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("省份id不能为空！");
        }
    }

}