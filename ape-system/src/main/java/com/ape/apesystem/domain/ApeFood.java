package com.ape.apesystem.domain;

import com.ape.apecommon.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 美食
 * @date 2024/01/08 08:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_food")
public class ApeFood implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 商家名称
     */
    private String shopName;

    /**
     * 商家id
     */
    private String shopId;

    /**
     * 城市
     */
    private String city;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片
     */
    private String img;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 评级
     */
    private Float star;

    /**
     * 商圈
     */
    private String business;

    /**
     * 类型
     */
    private String type;

    /**
     * 标签
     */
    private String tag;

    /**
     * 配送方式（0：到店 1：外送）
     */
    private String delivery;

    /**
     * 配送费
     */
    private Float deliveryPrice;

    /**
     * 简介
     */
    private String introduce;

    /**
     * 详情
     */
    private String details;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 已售
     */
    private Integer saleNum;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    private Integer sort;
}