package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 表单项显示控制对象 sys_form_field_visible
 *
 * @author aa
 * @date 2023-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_form_field_visible")
public class SysFormFieldVisible extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;
    /**
     * 字段ID
     */
    private String fieldId;/**
     * 字段名称
     */
    private String fieldLabel;
    /**
     * 关联字段显示名称
     */
    private String visibleLabel;
    /**
     * 关联字段属性值
     */
    private String visible;
    /**
     * 显示条件
     */
    private String visibleValue;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否需要
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private String visibleDefaultValue;

}
