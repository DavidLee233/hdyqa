package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 校验规则视图对象 sys_form_field_rules
 *
 * @author aa
 * @date 2023-06-03
 */
@Data
@ExcelIgnoreUnannotated
public class SysFormFieldRulesVo {

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


    private String verifyType;

    /**
     * 校验类型
     */
    @ExcelProperty(value = "校验类型")
    private String  verifyTypeLabel;
    /**
     * 提示信息
     */
    @ExcelProperty(value = "提示信息")
    private String message;

    /**
     * 规则名称
     */
    @ExcelProperty(value = "规则名称")
    private String ruleName;

    /**
     * 比较字段
     */
    @ExcelProperty(value = "比较字段")
    private String checkName;

    /**
     * 正则表达式
     */
    @ExcelProperty(value = "正则表达式")
    private String regText;

    /**
     * 表名称
     */
    @ExcelProperty(value = "表名称")
    private String tableName;

    /**
     * 验证表主键
     */
    @ExcelProperty(value = "验证表主键")
    private String validateIdName;

    /**
     * 最长
     */
    @ExcelProperty(value = "最长")
    private Long max;

    /**
     * 最短
     */
    @ExcelProperty(value = "最短")
    private Long min;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 其他字段
     */
    @ExcelProperty(value = "其他字段")
    private String otherField;

}
