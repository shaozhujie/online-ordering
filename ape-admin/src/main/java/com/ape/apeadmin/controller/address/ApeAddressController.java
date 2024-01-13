package com.ape.apeadmin.controller.address;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeAddress;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
 * @description: 地址controller
 * @date 2024/01/08 07:33
 */
@Controller
@ResponseBody
@RequestMapping("address")
public class ApeAddressController {

    @Autowired
    private ApeAddressService apeAddressService;

    /** 分页获取地址 */
    @Log(name = "分页获取地址", type = BusinessType.OTHER)
    @PostMapping("getApeAddressPage")
    public Result getApeAddressPage(@RequestBody ApeAddress apeAddress) {
        Page<ApeAddress> page = new Page<>(apeAddress.getPageNumber(),apeAddress.getPageSize());
        QueryWrapper<ApeAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeAddress.getAddress()),ApeAddress::getAddress,apeAddress.getAddress())
                .eq(StringUtils.isNotBlank(apeAddress.getName()),ApeAddress::getName,apeAddress.getName())
                .eq(StringUtils.isNotBlank(apeAddress.getTel()),ApeAddress::getTel,apeAddress.getTel())
                .eq(apeAddress.getFirst() != null,ApeAddress::getFirst,apeAddress.getFirst())
                .eq(StringUtils.isNotBlank(apeAddress.getUserId()),ApeAddress::getUserId,apeAddress.getUserId())
                .eq(StringUtils.isNotBlank(apeAddress.getCreateBy()),ApeAddress::getCreateBy,apeAddress.getCreateBy())
                .eq(apeAddress.getCreateTime() != null,ApeAddress::getCreateTime,apeAddress.getCreateTime())
                .eq(StringUtils.isNotBlank(apeAddress.getUpdateBy()),ApeAddress::getUpdateBy,apeAddress.getUpdateBy())
                .eq(apeAddress.getUpdateTime() != null,ApeAddress::getUpdateTime,apeAddress.getUpdateTime());
        Page<ApeAddress> apeAddressPage = apeAddressService.page(page, queryWrapper);
        return Result.success(apeAddressPage);
    }

    @GetMapping("getAddressList")
    public Result getAddressList() {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeAddress::getUserId,user.getId());
        List<ApeAddress> addressList = apeAddressService.list(queryWrapper);
        return Result.success(addressList);
    }

    /** 根据id获取地址 */
    @Log(name = "根据id获取地址", type = BusinessType.OTHER)
    @GetMapping("getApeAddressById")
    public Result getApeAddressById(@RequestParam("id")String id) {
        ApeAddress apeAddress = apeAddressService.getById(id);
        return Result.success(apeAddress);
    }

    /** 保存地址 */
    @Log(name = "保存地址", type = BusinessType.INSERT)
    @PostMapping("saveApeAddress")
    @Transactional(rollbackFor = Exception.class)
    public Result saveApeAddress(@RequestBody ApeAddress apeAddress) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeAddress.setUserId(user.getId());
        if (apeAddress.getFirst() == 0) {
            UpdateWrapper<ApeAddress> query = new UpdateWrapper<>();
            query.lambda().set(ApeAddress::getFirst,1);
            apeAddressService.update(query);
        }
        boolean save = apeAddressService.save(apeAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑地址 */
    @Log(name = "编辑地址", type = BusinessType.UPDATE)
    @PostMapping("editApeAddress")
    @Transactional(rollbackFor = Exception.class)
    public Result editApeAddress(@RequestBody ApeAddress apeAddress) {
        if (apeAddress.getFirst() == 0) {
            UpdateWrapper<ApeAddress> query = new UpdateWrapper<>();
            query.lambda().set(ApeAddress::getFirst,1).ne(ApeAddress::getId,apeAddress.getId());
            apeAddressService.update(query);
        }
        boolean save = apeAddressService.updateById(apeAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除地址 */
    @GetMapping("removeApeAddress")
    @Log(name = "删除地址", type = BusinessType.DELETE)
    public Result removeApeAddress(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeAddressService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("地址id不能为空！");
        }
    }

}