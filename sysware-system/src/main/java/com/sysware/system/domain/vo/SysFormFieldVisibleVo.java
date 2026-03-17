package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 单项显示控制视图对象 sys_form_field_visible
 *
 * @author aa
 * @date 2023-06-04
 */
@Data
@ExcelIgnoreUnannotated
public class SysFormFieldVisibleVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 字段ID
     */
    @ExcelProperty(value = "字段ID")
    private String fieldId;

    /**
     * 字段名称
     */
    @ExcelProperty(value = "字段名称")
    private String fieldLabel;
    /**
     * 关联字段
     */
    @ExcelProperty(value = "关联字段")
    private String visibleLabel;

    /**
     * 关联字段属性值
     */
    private String visible;
    /**
     * 显示条件
     */
    @ExcelProperty(value = "显示条件")
    private String visibleValue;

    /**
     * 默认值
     */
    @ExcelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 是否需要
     */
    @ExcelProperty(value = "是否需要")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
