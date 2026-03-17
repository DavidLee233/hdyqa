package com.sysware.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sysware.common.core.domain.BaseEntity;

/**
 * 表单配置分页查询对象 sys_form_field
 *
 * @author zzr
 * @date 2023-04-14
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysFormFieldQueryBo extends BaseEntity {


    /**
     * 表单ID
     */
	private String formId;
    /**
     * 类型
     */
	private String type;
    /**
     * 属性名
     */
	private String prop;
    /**
     * 显示名
     */
	private String label;
    /**
     * 是否必填
     */
	private String required;
    /**
     * 验证规则
     */
	private String rules;
    /**
     * 属性对应关系
     */
	private String props;
    /**
     * 是否初始化
     */
	private String initRequest;
    /**
     * 能否搜索
     */
	private String searchable;
    /**
     * URL地址
     */
	private String url;
    /**
     * 请求方式
     */
	private String method;
    /**
     * 搜索关键字
     */
	private String keyword;
    /**
     * 日期格式
     */
	private String dateType;
    /**
     * 默认样式
     */
	private String defaultCss;
    /**
     * 禁用日期类型
     */
	private String disabledDateType;
    /**
     * 布局比例
     */
	private Long span;
    /**
     * 禁用日期范围
     */
	private Long disabledDateDays;
    /**
     * 排序
     */
	private Long sort;
    /**
     * 关联更新字段
     */
	private String updateField;
    /**
     * 过滤的条件
     */
	private String filterParams;
    /**
     * 默认值
     */
	private String defaultValue;
	/**
	 * 文件格式
	 */
	private String fileType;
	/**
	 * 字段说明
	 */
	private String remark;


}
