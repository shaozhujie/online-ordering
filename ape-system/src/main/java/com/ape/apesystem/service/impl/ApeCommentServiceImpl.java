package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeComment;
import com.ape.apesystem.mapper.ApeCommentMapper;
import com.ape.apesystem.service.ApeCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食点评service实现类
 * @date 2024/01/03 04:44
 */
@Service
public class ApeCommentServiceImpl extends ServiceImpl<ApeCommentMapper, ApeComment> implements ApeCommentService {
}