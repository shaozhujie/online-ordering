package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeFoodComment;
import com.ape.apesystem.mapper.ApeFoodCommentMapper;
import com.ape.apesystem.service.ApeFoodCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食评论service实现类
 * @date 2024/01/08 01:59
 */
@Service
public class ApeFoodCommentServiceImpl extends ServiceImpl<ApeFoodCommentMapper, ApeFoodComment> implements ApeFoodCommentService {
}