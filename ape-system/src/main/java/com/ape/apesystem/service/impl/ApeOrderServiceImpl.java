package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeOrder;
import com.ape.apesystem.mapper.ApeOrderMapper;
import com.ape.apesystem.service.ApeOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 订单service实现类
 * @date 2024/01/08 02:23
 */
@Service
public class ApeOrderServiceImpl extends ServiceImpl<ApeOrderMapper, ApeOrder> implements ApeOrderService {
}