package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeAddress;
import com.ape.apesystem.mapper.ApeAddressMapper;
import com.ape.apesystem.service.ApeAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 地址service实现类
 * @date 2024/01/08 07:33
 */
@Service
public class ApeAddressServiceImpl extends ServiceImpl<ApeAddressMapper, ApeAddress> implements ApeAddressService {
}