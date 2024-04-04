package com.ape.apesystem.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.mapper.ApeUserMapper;
import com.ape.apesystem.service.ApeUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户service实现类
 * @date 2023/8/28 8:46
 */
@Service
public class ApeUserServiceImpl extends ServiceImpl<ApeUserMapper, ApeUser> implements ApeUserService {

    /**
     * 分页查询用户
     */
    @Override
    public Page<ApeUser> getUserPage(ApeUser apeUser) {
        Page<ApeUser> page = new Page<>(apeUser.getPageNumber(),apeUser.getPageSize());
        return baseMapper.getUserPage(page,apeUser);
    }

    @Override
    public Page<Map<String, Object>> getUserSalePage(JSONObject jsonObject) {
        String userName = jsonObject.getString("userName");
        String id = jsonObject.getString("id");
        Integer pageNumber = jsonObject.getInteger("pageNumber");
        Integer pageSize = jsonObject.getInteger("pageSize");
        Page<Map<String, Object>> page = new Page<>(pageNumber,pageSize);
        return baseMapper.getUserSalePage(page,userName,id);
    }

    @Override
    public Page<Map<String, Object>> getUserDianCaiPage(JSONObject jsonObject) {
        String userName = jsonObject.getString("userName");
        String id = jsonObject.getString("id");
        Integer pageNumber = jsonObject.getInteger("pageNumber");
        Integer pageSize = jsonObject.getInteger("pageSize");
        Page<Map<String, Object>> page = new Page<>(pageNumber,pageSize);
        return baseMapper.getUserDianCaiPage(page,userName,id);
    }

    @Override
    public Map<String, Object> getMaxFood(String userId) {
        return baseMapper.getMaxFood(userId);
    }
}
