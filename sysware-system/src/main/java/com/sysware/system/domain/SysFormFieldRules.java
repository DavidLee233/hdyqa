package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 校验规则对象 sys_form_field_rules
 *
 * @author aa
 * @date 2023-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_form_field_rules")
public class SysFormFieldRules extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;
    /**
     * 字段ID
     */
    private String fieldId;
    /**
     * 校验类型
     */
    private String verifyType;
    /**
     * 校验类型
     */
    private String verifyTypeLabel;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 比较字段
     */
    private String checkName;
    /**
     * 正则表达式
     */
    private String regText;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 验证表主键
     */
    private String validateIdName;
    /**
     * 最长
     */
    private Long max;
    /**
     * 最短
     */
    private Long min;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 备注
     */
    private String remark;
    /**
     * 其他字段
     */
    private String otherField;

    @TableField(exist = false)
    private String ruleValue;
}
