package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sysware.common.core.domain.BaseEntity;
import com.sysware.system.domain.SysFormFieldRules;
import com.sysware.system.domain.SysFormFieldVisible;
import lombok.Data;



/**
 * 表单配置视图对象 sys_form_field
 *
 * @author zzr
 * @date 2023-04-14
 */
@Data
public class SysFormFieldVo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
     *  主键
     */
	private String fieldId;

    /**
     * 表单ID
     */
	@ExcelProperty(value= "表单ID")
	private String formId;

    /**
     * 类型
     */
	@ExcelProperty(value= "类型")
	private String type;

    /**
     * 属性名
     */
	@ExcelProperty(value= "属性名")
	private String prop;

    /**
     * 显示名
     */
	@ExcelProperty(value= "显示名")
	private String label;

    /**
     * 是否必填
     */
	@ExcelProperty(value= "是否必填")
	private String required;

    /**
     * 验证规则
     */
	@ExcelProperty(value= "验证规则")
	private String rules;

    /**
     * 属性对应关系
     */
	@ExcelProperty(value= "属性对应关系")
	private String props;

    /**
     * 是否初始化
     */
	@ExcelProperty(value= "是否初始化")
	private String initRequest;

    /**
     * 能否搜索
     */
	@ExcelProperty(value= "能否搜索")
	private String searchable;

    /**
     * URL地址
     */
	@ExcelProperty(value= "URL地址")
	private String url;

    /**
     * 请求方式
     */
	@ExcelProperty(value= "请求方式")
	private String method;

    /**
     * 搜索关键字
     */
	@ExcelProperty(value= "搜索关键字")
	private String keyword;

    /**
     * 日期格式
     */
	@ExcelProperty(value= "日期格式")
	private String dateType;

    /**
     * 默认样式
     */
	@ExcelProperty(value= "默认样式")
	private String defaultCss;

    /**
     * 禁用日期类型
     */
	@ExcelProperty(value= "禁用日期类型")
	private String disabledDateType;

    /**
     * 布局比例
     */
	@ExcelProperty(value= "布局比例")
	private Long span;

    /**
     * 禁用日期范围
     */
	@ExcelProperty(value= "禁用日期范围")
	private Long disabledDateDays;

    /**
     * 排序
     */
	@ExcelProperty(value= "排序")
	private Long sort;

    /**
     * 关联更新字段
     */
	@ExcelProperty(value= "关联更新字段")
	private String updateField;

    /**
     * 过滤的条件
     */
	@ExcelProperty(value= "过滤的条件")
	private String filterParams;

    /**
     * 默认值
     */
	@ExcelProperty(value= "默认值")
	private String defaultValue;

	/**
	 * 是否显示
	 */
	@ExcelProperty(value= "是否显示")
	private String disabled;
	/**
	 * 能否多选
	 */
	@ExcelProperty(value= "能否多选")
	private String multiple;
	/**
	 * 是否显示
	 */
	@ExcelProperty(value= "是否显示")
	private String showabled;

	/**
	 * 动态关联字段
	 */
	@ExcelProperty(value= "动态关联字段")
	private String dynamicLabel;
	/**
	 * 备注
	 */
	@ExcelProperty(value = "备注")
	private String remark;

	/**
	 * 文件格式
	 */
	@ExcelProperty(value = "文件格式")
	private String fileType;

	private List<SysFormFieldRules> ruleList;

	private List<SysFormFieldVisible> visible;



}
