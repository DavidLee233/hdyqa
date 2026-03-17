package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 操作的数据对象 sys_oper_params
 *
 * @author aa
 * @date 2024-06-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_params")
public class SysOperParams extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 操作ID
     */
    private String operId;
    /**
     * 操作前数据
     */
    private String operPreParam;
    /**
     * 操作后数据
     */
    private String operAfterParam;
    /**
     * 操作对象表
     */
    private String operTableName;

}
