package com.ape.apeadmin.controller.order;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeFood;
import com.ape.apesystem.domain.ApeOrder;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeFoodService;
import com.ape.apesystem.service.ApeOrderService;
import com.ape.apesystem.service.ApeUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 订单controller
 * @date 2024/01/08 02:23
 */
@Controller
@ResponseBody
@RequestMapping("order")
public class ApeOrderController {

    @Autowired
    private ApeOrderService apeOrderService;
    @Autowired
    private ApeFoodService apeFoodService;
    @Autowired
    private ApeUserService apeUserService;

    /** 分页获取订单 */
    @Log(name = "分页获取订单", type = BusinessType.OTHER)
    @PostMapping("getApeOrderPage")
    public Result getApeOrderPage(@RequestBody ApeOrder apeOrder) {
        Page<ApeOrder> page = new Page<>(apeOrder.getPageNumber(),apeOrder.getPageSize());
        QueryWrapper<ApeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeOrder.getOrderId()),ApeOrder::getOrderId,apeOrder.getOrderId())
                .like(StringUtils.isNotBlank(apeOrder.getShopName()),ApeOrder::getShopName,apeOrder.getShopName())
                .like(StringUtils.isNotBlank(apeOrder.getFoodName()),ApeOrder::getFoodName,apeOrder.getFoodName())
                .like(StringUtils.isNotBlank(apeOrder.getUserId()),ApeOrder::getUserId,apeOrder.getUserId())
                .eq(apeOrder.getPrice() != null,ApeOrder::getPrice,apeOrder.getPrice())
                .eq(apeOrder.getNum() != null,ApeOrder::getNum,apeOrder.getNum())
                .eq(apeOrder.getType() != null,ApeOrder::getType,apeOrder.getType())
                .eq(apeOrder.getState() != null,ApeOrder::getState,apeOrder.getState())
                .like(StringUtils.isNotBlank(apeOrder.getCreateBy()),ApeOrder::getCreateBy,apeOrder.getCreateBy());
        Page<ApeOrder> apeOrderPage = apeOrderService.page(page, queryWrapper);
        return Result.success(apeOrderPage);
    }

    @PostMapping("getApeOrderPageByShop")
    public Result getApeOrderPageByShop(@RequestBody ApeOrder apeOrder) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeOrder.setShopId(user.getId());
        Page<ApeOrder> page = new Page<>(apeOrder.getPageNumber(),apeOrder.getPageSize());
        QueryWrapper<ApeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeOrder.getOrderId()),ApeOrder::getOrderId,apeOrder.getOrderId())
                .like(StringUtils.isNotBlank(apeOrder.getShopName()),ApeOrder::getShopName,apeOrder.getShopName())
                .like(StringUtils.isNotBlank(apeOrder.getFoodName()),ApeOrder::getFoodName,apeOrder.getFoodName())
                .like(StringUtils.isNotBlank(apeOrder.getUserId()),ApeOrder::getUserId,apeOrder.getUserId())
                .eq(apeOrder.getPrice() != null,ApeOrder::getPrice,apeOrder.getPrice())
                .eq(StringUtils.isNotBlank(apeOrder.getShopId()),ApeOrder::getShopId,apeOrder.getShopId())
                .eq(apeOrder.getNum() != null,ApeOrder::getNum,apeOrder.getNum())
                .eq(apeOrder.getType() != null,ApeOrder::getType,apeOrder.getType())
                .eq(apeOrder.getState() != null,ApeOrder::getState,apeOrder.getState())
                .like(StringUtils.isNotBlank(apeOrder.getCreateBy()),ApeOrder::getCreateBy,apeOrder.getCreateBy());
        Page<ApeOrder> apeOrderPage = apeOrderService.page(page, queryWrapper);
        return Result.success(apeOrderPage);
    }

    /** 根据id获取订单 */
    @Log(name = "根据id获取订单", type = BusinessType.OTHER)
    @GetMapping("getApeOrderById")
    public Result getApeOrderById(@RequestParam("id")String id) {
        ApeOrder apeOrder = apeOrderService.getById(id);
        return Result.success(apeOrder);
    }

    /** 保存订单 */
    @Log(name = "保存订单", type = BusinessType.INSERT)
    @PostMapping("saveApeOrder")
    @Transactional(rollbackFor = Exception.class)
    public Result saveApeOrder(@RequestBody ApeOrder apeOrder) {
        ApeUser apeUser = apeUserService.getById(apeOrder.getShopId());
        if (apeUser.getOpen() == 0) {
            return Result.fail("商家已打样无法下单！");
        }
        ApeUser user = ShiroUtils.getUserInfo();
        apeOrder.setUserId(user.getId());
        apeOrder.setOrderId(IdWorker.getTimeId());
        boolean save = apeOrderService.save(apeOrder);
        ApeFood food = apeFoodService.getById(apeOrder.getFoodId());
        food.setSaleNum(food.getSaleNum() + 1);
        apeFoodService.updateById(food);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑订单 */
    @Log(name = "编辑订单", type = BusinessType.UPDATE)
    @PostMapping("editApeOrder")
    public Result editApeOrder(@RequestBody ApeOrder apeOrder) {
        boolean save = apeOrderService.updateById(apeOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @GetMapping("editApeOrderState")
    @Transactional(rollbackFor = Exception.class)
    public Result editApeOrderState(@RequestParam("id") String id,@RequestParam("state")Integer state) {
        ApeOrder order = apeOrderService.getById(id);
        if (state == 2) {
            if (order.getState() == 4 || order.getState() == 5) {
                return Result.fail("该订单已发起过退款");
            }
            if (order.getState() == 2) {
                return Result.fail("订单已使用无需重复核销");
            }
        }
        if (state == 4) {
            ApeFood food = apeFoodService.getById(order.getFoodId());
            food.setSaleNum(food.getSaleNum() -1);
            apeFoodService.updateById(food);
        }
        order.setState(state);
        boolean save = apeOrderService.updateById(order);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除订单 */
    @GetMapping("removeApeOrder")
    @Log(name = "删除订单", type = BusinessType.DELETE)
    public Result removeApeOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeOrderService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("订单id不能为空！");
        }
    }

}