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
 * @description: 订单
 * @date 2024/01/08 02:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_order")
public class ApeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 收获地址id
     */
    private String addressId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 商家id
     */
    private String shopId;

    /**
     * 商家名称
     */
    private String shopName;

    /**
     * 美食id
     */
    private String foodId;

    /**
     * 美食名称
     */
    private String foodName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 使用方式
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 下单用户id
     */
    private String userId;

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
}