package com.ape.apeadmin.controller.business;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeBusiness;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeBusinessService;
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
 * @description: 商圈controller
 * @date 2023/12/23 09:43
 */
@Controller
@ResponseBody
@RequestMapping("business")
public class ApeBusinessController {

    @Autowired
    private ApeBusinessService apeBusinessService;

    /** 分页获取商圈 */
    @Log(name = "分页获取商圈", type = BusinessType.OTHER)
    @PostMapping("getApeBusinessPage")
    public Result getApeBusinessPage(@RequestBody ApeBusiness apeBusiness) {
        Page<ApeBusiness> page = new Page<>(apeBusiness.getPageNumber(),apeBusiness.getPageSize());
        QueryWrapper<ApeBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeBusiness.getProvince()),ApeBusiness::getProvince,apeBusiness.getProvince())
                .eq(StringUtils.isNotBlank(apeBusiness.getCity()),ApeBusiness::getCity,apeBusiness.getCity())
                .like(StringUtils.isNotBlank(apeBusiness.getBusiness()),ApeBusiness::getBusiness,apeBusiness.getBusiness());
        Page<ApeBusiness> apeBusinessPage = apeBusinessService.page(page, queryWrapper);
        return Result.success(apeBusinessPage);
    }

    @GetMapping("getApeBusinessList")
    public Result getApeBusinessList() {
        List<ApeBusiness> list = apeBusinessService.list();
        return Result.success(list);
    }

    /** 根据id获取商圈 */
    @Log(name = "根据id获取商圈", type = BusinessType.OTHER)
    @GetMapping("getApeBusinessById")
    public Result getApeBusinessById(@RequestParam("id")String id) {
        ApeBusiness apeBusiness = apeBusinessService.getById(id);
        return Result.success(apeBusiness);
    }

    /** 保存商圈 */
    @Log(name = "保存商圈", type = BusinessType.INSERT)
    @PostMapping("saveApeBusiness")
    public Result saveApeBusiness(@RequestBody ApeBusiness apeBusiness) {
        boolean save = apeBusinessService.save(apeBusiness);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑商圈 */
    @Log(name = "编辑商圈", type = BusinessType.UPDATE)
    @PostMapping("editApeBusiness")
    public Result editApeBusiness(@RequestBody ApeBusiness apeBusiness) {
        boolean save = apeBusinessService.updateById(apeBusiness);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除商圈 */
    @GetMapping("removeApeBusiness")
    @Log(name = "删除商圈", type = BusinessType.DELETE)
    public Result removeApeBusiness(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeBusinessService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("商圈id不能为空！");
        }
    }

    @GetMapping("getBusinessListByCity")
    public Result getBusinessListByCity() {
        ApeUser user = ShiroUtils.getUserInfo();
        return null;
    }

}