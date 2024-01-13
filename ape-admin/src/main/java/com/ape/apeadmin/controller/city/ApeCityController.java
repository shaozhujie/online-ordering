package com.ape.apeadmin.controller.city;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeCity;
import com.ape.apesystem.service.ApeCityService;
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
 * @description: 城市controller
 * @date 2023/12/23 09:27
 */
@Controller
@ResponseBody
@RequestMapping("city")
public class ApeCityController {

    @Autowired
    private ApeCityService apeCityService;

    /** 分页获取城市 */
    @Log(name = "分页获取城市", type = BusinessType.OTHER)
    @PostMapping("getApeCityPage")
    public Result getApeCityPage(@RequestBody ApeCity apeCity) {
        Page<ApeCity> page = new Page<>(apeCity.getPageNumber(),apeCity.getPageSize());
        QueryWrapper<ApeCity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeCity.getProvince()),ApeCity::getProvince,apeCity.getProvince())
                .like(StringUtils.isNotBlank(apeCity.getCity()),ApeCity::getCity,apeCity.getCity());
        Page<ApeCity> apeCityPage = apeCityService.page(page, queryWrapper);
        return Result.success(apeCityPage);
    }

    @GetMapping("getCityList")
    public Result getCityList() {
        List<ApeCity> list = apeCityService.list();
        return Result.success(list);
    }

    /** 根据id获取城市 */
    @Log(name = "根据id获取城市", type = BusinessType.OTHER)
    @GetMapping("getApeCityById")
    public Result getApeCityById(@RequestParam("id")String id) {
        ApeCity apeCity = apeCityService.getById(id);
        return Result.success(apeCity);
    }

    /** 保存城市 */
    @Log(name = "保存城市", type = BusinessType.INSERT)
    @PostMapping("saveApeCity")
    public Result saveApeCity(@RequestBody ApeCity apeCity) {
        boolean save = apeCityService.save(apeCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑城市 */
    @Log(name = "编辑城市", type = BusinessType.UPDATE)
    @PostMapping("editApeCity")
    public Result editApeCity(@RequestBody ApeCity apeCity) {
        boolean save = apeCityService.updateById(apeCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除城市 */
    @GetMapping("removeApeCity")
    @Log(name = "删除城市", type = BusinessType.DELETE)
    public Result removeApeCity(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeCityService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("城市id不能为空！");
        }
    }

}