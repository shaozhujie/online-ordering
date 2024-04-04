package com.ape.apeadmin.controller.food;

import com.alibaba.fastjson2.JSONObject;
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

import java.text.SimpleDateFormat;
import java.util.*;

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

    @PostMapping("getSale")
    public Result getSale(@RequestBody JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        Date startTime = jsonObject.getDate("startTime");
        Date endTime = jsonObject.getDate("endTime");
        // 获取两个日期之间的所有日期
        List<Date> dateList = getDatesBetween(startTime, endTime);
        List<String> dates = new ArrayList<>();
        List<Object> countList = new ArrayList<>();
        List<Object> priceList = new ArrayList<>();
        // 打印结果
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Date date : dateList) {
            dates.add(sdf.format(date));
            QueryWrapper<ApeOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("sum(num) as num").lambda().eq(ApeOrder::getFoodId,id)
                    .ge(ApeOrder::getCreateTime,sdf.format(date) + " 00:00:00")
                    .le(ApeOrder::getCreateTime,sdf.format(date) + " 23:59:59");
            //获取销量
            Map<String, Object> map = apeOrderService.getMap(queryWrapper);
            if (map == null) {
                countList.add(0);
            } else {
                Object num = map.get("num");
                if (num == null) {
                    countList.add(0);
                } else {
                    countList.add(num);
                }
            }
            //获取金额
            QueryWrapper<ApeOrder> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("sum(price) as price").lambda()
                    .eq(ApeOrder::getFoodId,id)
                    .ge(ApeOrder::getCreateTime,sdf.format(date) + " 00:00:00")
                    .le(ApeOrder::getCreateTime,sdf.format(date) + " 23:59:59");
            Map<String, Object> objectMap = apeOrderService.getMap(queryWrapper1);
            if (objectMap == null) {
                priceList.add(0);
            } else {
                Object price = objectMap.get("price");
                if (price == null) {
                    priceList.add(0);
                } else {
                    priceList.add(price);
                }
            }
        }
        //获取总金额
        QueryWrapper<ApeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(price) as price").lambda().eq(ApeOrder::getFoodId,id)
                .ge(ApeOrder::getCreateTime,startTime)
                .le(ApeOrder::getCreateTime,endTime);
        //获取销量
        Map<String, Object> objectMap = apeOrderService.getMap(queryWrapper);
        Object price = 0;
        if (objectMap != null) {
            if (objectMap.get("price") != null) {
                price = objectMap.get("price");
            }
        }
        JSONObject json = new JSONObject();
        json.put("countList",countList);
        json.put("priceList",priceList);
        json.put("price",price);
        json.put("dateList",dates);
        return Result.success(json);
    }

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        while (!cal.getTime().after(endDate)) {
            dates.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        return dates;
    }

}