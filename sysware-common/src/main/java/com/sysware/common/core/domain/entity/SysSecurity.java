package com.sysware.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 密级对象 sys_security
 *
 * @author zzr
 * @date 2022-01-05
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_security")
public class SysSecurity  extends BaseEntity {



    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String securityId;

    /**
     * 密级名称
     */
    private String securityName;

    /**
     * 密级值
     */
    private Long securityValue;

    /**
     * 密级类型
     */
    private String securityType;

    /**
     * 起用状态
     */
    private String securityFlag;


    /**
     * 排序
     */
    private Long sort;

}
