package com.ape.apesystem.service;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apesystem.domain.ApeUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户service
 * @date 2023/8/28 8:45
 */
public interface ApeUserService extends IService<ApeUser> {

    /**
    * @description: 分页查询用户
    * @param: apeUser
    * @return: Page
    * @author shaozhujie
    * @date: 2023/8/28 10:49
    */
    Page<ApeUser> getUserPage(ApeUser apeUser);

    Page<Map<String, Object>> getUserSalePage(JSONObject jsonObject);

    Page<Map<String, Object>> getUserDianCaiPage(JSONObject jsonObject);

    Map<String,Object> getMaxFood(String userId);
}
