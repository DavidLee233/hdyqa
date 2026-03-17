package com.sysware.system.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 单项显示控制业务对象 sys_form_field_visible
 *
 * @author aa
 * @date 2023-06-04
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysFormFieldVisibleBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 字段ID
     */
    @NotBlank(message = "字段ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fieldId;

    /**
     * 字段名称
     */
    private String fieldLabel;

    /**
     * 关联字段显示名称
     */
    private String visibleLabel;

    /**
     * 显示条件
     */
    private String visibleValue;

    /**
     * 关联字段属性值
     */
    private String visible;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;


}
