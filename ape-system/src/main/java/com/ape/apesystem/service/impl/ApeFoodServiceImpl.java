package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeFood;
import com.ape.apesystem.mapper.ApeFoodMapper;
import com.ape.apesystem.service.ApeFoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食service实现类
 * @date 2024/01/08 08:57
 */
@Service
public class ApeFoodServiceImpl extends ServiceImpl<ApeFoodMapper, ApeFood> implements ApeFoodService {
}