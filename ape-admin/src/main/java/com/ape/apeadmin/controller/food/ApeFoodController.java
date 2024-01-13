package com.ape.apeadmin.controller.food;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeFood;
import com.ape.apesystem.domain.ApeOrder;
import com.ape.apesystem.domain.ApeTag;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeFoodService;
import com.ape.apesystem.service.ApeOrderService;
import com.ape.apesystem.service.ApeTagService;
import com.ape.apesystem.service.ApeUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食controller
 * @date 2024/01/08 08:57
 */
@Controller
@ResponseBody
@RequestMapping("food")
public class ApeFoodController {

    @Autowired
    private ApeFoodService apeFoodService;
    @Autowired
    private ApeTagService apeTagService;
    @Autowired
    private ApeUserService apeUserService;
    @Autowired
    private ApeOrderService apeOrderService;

    /** 分页获取美食 */
    @Log(name = "分页获取美食", type = BusinessType.OTHER)
    @PostMapping("getApeFoodPage")
    public Result getApeFoodPage(@RequestBody ApeFood apeFood) {
        Page<ApeFood> page = new Page<>(apeFood.getPageNumber(),apeFood.getPageSize());
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(apeFood.getState() != null,ApeFood::getState,apeFood.getState())
                .eq(StringUtils.isNotBlank(apeFood.getShopId()),ApeFood::getShopId,apeFood.getShopId())
                .like(StringUtils.isNotBlank(apeFood.getShopName()),ApeFood::getShopName,apeFood.getShopName())
                .like(StringUtils.isNotBlank(apeFood.getName()),ApeFood::getName,apeFood.getName())
                .orderByDesc(ApeFood::getCreateTime);
        Page<ApeFood> apeFoodPage = apeFoodService.page(page, queryWrapper);
        return Result.success(apeFoodPage);
    }

    @PostMapping("getApeFoodPageByShop")
    public Result getApeFoodPageByShop(@RequestBody ApeFood apeFood) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeFood.setShopId(user.getId());
        Page<ApeFood> page = new Page<>(apeFood.getPageNumber(),apeFood.getPageSize());
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(apeFood.getState() != null,ApeFood::getState,apeFood.getState())
                .eq(StringUtils.isNotBlank(apeFood.getShopId()),ApeFood::getShopId,apeFood.getShopId())
                .like(StringUtils.isNotBlank(apeFood.getShopName()),ApeFood::getShopName,apeFood.getShopName())
                .like(StringUtils.isNotBlank(apeFood.getName()),ApeFood::getName,apeFood.getName())
                .orderByDesc(ApeFood::getCreateTime);
        Page<ApeFood> apeFoodPage = apeFoodService.page(page, queryWrapper);
        return Result.success(apeFoodPage);
    }

    @PostMapping("getApeFoodPageByCity")
    public Result getApeFoodPageByCity(@RequestBody ApeFood apeFood) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        Page<ApeFood> page = new Page<>(apeFood.getPageNumber(),apeFood.getPageSize());
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(apeFood.getState() != null,ApeFood::getState,apeFood.getState())
                .eq(StringUtils.isNotBlank(apeFood.getShopId()),ApeFood::getShopId,apeFood.getShopId())
                .like(StringUtils.isNotBlank(apeFood.getType()),ApeFood::getType,apeFood.getType())
                .like(StringUtils.isNotBlank(apeFood.getTag()),ApeFood::getTag,apeFood.getTag())
                .eq(ApeFood::getCity,userInfo.getCity());
        if (apeFood.getSort() == 0) {
            queryWrapper.lambda().orderByDesc(ApeFood::getCreateTime);
        } else if (apeFood.getSort() == 1) {
            queryWrapper.lambda().orderByDesc(ApeFood::getSaleNum);
        } else if (apeFood.getSort() == 2) {
            queryWrapper.lambda().orderByAsc(ApeFood::getPrice);
        } else if (apeFood.getSort() == 3) {
            queryWrapper.lambda().orderByDesc(ApeFood::getPrice);
        }
        Page<ApeFood> apeFoodPage = apeFoodService.page(page, queryWrapper);
        return Result.success(apeFoodPage);
    }

    /** 根据id获取美食 */
    @Log(name = "根据id获取美食", type = BusinessType.OTHER)
    @GetMapping("getApeFoodById")
    public Result getApeFoodById(@RequestParam("id")String id) {
        ApeFood apeFood = apeFoodService.getById(id);
        return Result.success(apeFood);
    }

    /** 保存美食 */
    @Log(name = "保存美食", type = BusinessType.INSERT)
    @PostMapping("saveApeFood")
    public Result saveApeFood(@RequestBody ApeFood apeFood) {
        ApeUser user = apeUserService.getById(apeFood.getShopId());
        apeFood.setBusiness(user.getBusiness());
        apeFood.setShopName(user.getName());
        apeFood.setShopId(user.getId());
        apeFood.setCity(user.getCity());
        String[] split = apeFood.getTag().split("-");
        apeFood.setType(split[0]);
        apeFood.setTag(split[1]);
        boolean save = apeFoodService.save(apeFood);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑美食 */
    @Log(name = "编辑美食", type = BusinessType.UPDATE)
    @PostMapping("editApeFood")
    public Result editApeFood(@RequestBody ApeFood apeFood) {
        String[] split = apeFood.getTag().split("-");
        apeFood.setType(split[0]);
        apeFood.setTag(split[1]);
        ApeUser user = apeUserService.getById(apeFood.getShopId());
        apeFood.setBusiness(user.getBusiness());
        apeFood.setShopName(user.getName());
        apeFood.setShopId(user.getId());
        apeFood.setCity(user.getCity());
        boolean save = apeFoodService.updateById(apeFood);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除美食 */
    @GetMapping("removeApeFood")
    @Log(name = "删除美食", type = BusinessType.DELETE)
    public Result removeApeFood(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeFoodService.removeById(id);
                //删除订单
                QueryWrapper<ApeOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeOrder::getShopId,id);
                apeOrderService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("美食id不能为空！");
        }
    }

    @GetMapping("getFoodFour")
    public Result getFoodFour() {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeFood::getCity,user.getCity())
                .orderByDesc(ApeFood::getSaleNum).eq(ApeFood::getState,0).last("limit 4");
        List<ApeFood> foodList = apeFoodService.list(queryWrapper);
        return Result.success(foodList);
    }

    @GetMapping("getFoodOtherFour")
    public Result getFoodOtherFour(@RequestParam("id") String id) {
        ApeFood food = apeFoodService.getById(id);
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeFood::getShopId,food.getShopId())
                .ne(ApeFood::getId,food.getId())
                .eq(ApeFood::getState,0).last("limit 4");
        List<ApeFood> foodList = apeFoodService.list(queryWrapper);
        return Result.success(foodList);
    }

}