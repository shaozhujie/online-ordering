package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeArticle;
import com.ape.apesystem.mapper.ApeArticleMapper;
import com.ape.apesystem.service.ApeArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食资讯service实现类
 * @date 2024/01/03 03:43
 */
@Service
public class ApeArticleServiceImpl extends ServiceImpl<ApeArticleMapper, ApeArticle> implements ApeArticleService {
}