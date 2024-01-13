package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTag;
import com.ape.apesystem.mapper.ApeTagMapper;
import com.ape.apesystem.service.ApeTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 标签service实现类
 * @date 2023/12/25 09:07
 */
@Service
public class ApeTagServiceImpl extends ServiceImpl<ApeTagMapper, ApeTag> implements ApeTagService {
}