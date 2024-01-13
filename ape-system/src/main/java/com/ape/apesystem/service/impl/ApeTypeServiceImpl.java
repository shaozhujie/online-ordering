package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeType;
import com.ape.apesystem.mapper.ApeTypeMapper;
import com.ape.apesystem.service.ApeTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 分类service实现类
 * @date 2023/12/23 11:31
 */
@Service
public class ApeTypeServiceImpl extends ServiceImpl<ApeTypeMapper, ApeType> implements ApeTypeService {
}