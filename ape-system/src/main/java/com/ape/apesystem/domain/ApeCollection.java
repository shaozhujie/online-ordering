package com.ape.apesystem.domain;

import com.ape.apecommon.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 收藏
 * @date 2024/01/11 09:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_collection")
public class ApeCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 店铺id
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 图片
     */
    private String shopImg;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}