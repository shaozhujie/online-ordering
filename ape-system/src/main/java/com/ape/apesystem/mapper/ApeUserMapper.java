package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户mapper
 * @date 2023/8/28 8:41
 */
public interface ApeUserMapper extends BaseMapper<ApeUser> {

    /**
    * 分页查询用户
    */
    Page<ApeUser> getUserPage(Page<ApeUser> page, @Param("ew")ApeUser apeUser);
    /**
     * 分页查询用户消费
     */
    Page<Map<String, Object>> getUserSalePage(Page<Map<String, Object>> page, @Param("userName") String userName,@Param("id") String id);

    Page<Map<String, Object>> getUserDianCaiPage(Page<Map<String, Object>> page, @Param("userName")String userName, @Param("id")String id);

    Map<String,Object> getMaxFood(@Param("userId") String userId);
}
