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
 * @description: 城市
 * @date 2023/12/23 09:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_city")
public class ApeCity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}