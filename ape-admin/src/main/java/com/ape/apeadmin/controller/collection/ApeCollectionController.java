package com.ape.apeadmin.controller.collection;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeCollection;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeCollectionService;
import com.ape.apesystem.service.ApeUserService;
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
 * @description: 收藏controller
 * @date 2024/01/11 09:05
 */
@Controller
@ResponseBody
@RequestMapping("collection")
public class ApeCollectionController {

    @Autowired
    private ApeCollectionService apeCollectionService;
    @Autowired
    private ApeUserService apeUserService;

    /** 分页获取收藏 */
    @Log(name = "分页获取收藏", type = BusinessType.OTHER)
    @PostMapping("getApeCollectionPage")
    public Result getApeCollectionPage(@RequestBody ApeCollection apeCollection) {
        Page<ApeCollection> page = new Page<>(apeCollection.getPageNumber(),apeCollection.getPageSize());
        QueryWrapper<ApeCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeCollection.getUserId()),ApeCollection::getUserId,apeCollection.getUserId())
                .eq(StringUtils.isNotBlank(apeCollection.getShopId()),ApeCollection::getShopId,apeCollection.getShopId())
                .eq(StringUtils.isNotBlank(apeCollection.getShopName()),ApeCollection::getShopName,apeCollection.getShopName())
                .eq(StringUtils.isNotBlank(apeCollection.getShopImg()),ApeCollection::getShopImg,apeCollection.getShopImg());
        Page<ApeCollection> apeCollectionPage = apeCollectionService.page(page, queryWrapper);
        return Result.success(apeCollectionPage);
    }

    @GetMapping("getFavorByUser")
    public Result getFavorByUser(@RequestParam("shopId")String shopId) {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeCollection::getShopId,shopId)
                .eq(ApeCollection::getUserId,user.getId());
        List<ApeCollection> list = apeCollectionService.list(queryWrapper);
        if (list.size() > 0) {
            return Result.success(list.get(0));
        } else {
            return Result.fail();
        }
    }

    /** 根据id获取收藏 */
    @Log(name = "根据id获取收藏", type = BusinessType.OTHER)
    @GetMapping("getApeCollectionById")
    public Result getApeCollectionById(@RequestParam("id")String id) {
        ApeCollection apeCollection = apeCollectionService.getById(id);
        return Result.success(apeCollection);
    }

    /** 保存收藏 */
    @Log(name = "保存收藏", type = BusinessType.INSERT)
    @PostMapping("saveApeCollection")
    public Result saveApeCollection(@RequestBody ApeCollection apeCollection) {
        ApeUser user = apeUserService.getById(apeCollection.getShopId());
        apeCollection.setShopName(user.getName());
        apeCollection.setShopImg(user.getCoverImg());
        ApeUser apeUser = ShiroUtils.getUserInfo();
        apeCollection.setUserId(apeUser.getId());
        boolean save = apeCollectionService.save(apeCollection);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑收藏 */
    @Log(name = "编辑收藏", type = BusinessType.UPDATE)
    @PostMapping("editApeCollection")
    public Result editApeCollection(@RequestBody ApeCollection apeCollection) {
        boolean save = apeCollectionService.updateById(apeCollection);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除收藏 */
    @GetMapping("removeApeCollection")
    @Log(name = "删除收藏", type = BusinessType.DELETE)
    public Result removeApeCollection(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeCollectionService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("收藏id不能为空！");
        }
    }

}