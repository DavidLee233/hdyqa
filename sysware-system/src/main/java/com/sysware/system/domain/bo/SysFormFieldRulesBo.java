package com.sysware.system.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 校验规则业务对象 sys_form_field_rules
 *
 * @author aa
 * @date 2023-06-03
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysFormFieldRulesBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 字段ID
     */
    @NotBlank(message = "关联字段不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fieldId;

    /**
     * 校验类型
     */
    @NotBlank(message = "校验类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String verifyType;

    private String  verifyTypeLabel;

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

}
