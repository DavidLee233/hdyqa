package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 自定义页面显示视图对象 sys_table_field
 *
 * @author aa
 * @date 2022-08-03
 */
@Data
public class SysTableFieldVo {

	private static final long serialVersionUID = 1L;

	/**
     *  ID
     */
	private String id;

    /**
     * 所属用户的ID
     */
	@ExcelProperty(value= "所属用户的ID")
	private String userId;

	/**
	 * 页面名称
	 */
	private String menuId;

	/**
	 * 页面组件
	 */
	private String path;

    /**
     * 页面名称
     */
	@ExcelProperty(value= "页面名称")
	private String pageName;

	/**
	 * 插槽名称
	 */
	private String slotName;
    /**
     * 字段类型
     */
	@ExcelProperty(value= "字段类型")
	private String type;

    /**
     * 宽度
     */
	@ExcelProperty(value= "宽度")
	private Integer width;

    /**
     * 对其方式
     */
	@ExcelProperty(value= "对其方式")
	private String align;

    /**
     * 字段名称
     */
	@ExcelProperty(value= "字段名称")
	private String label;

    /**
     * 字段属性
     */
	@ExcelProperty(value= "字段属性")
	private String prop;

    /**
     * 是否可见
     */
	@ExcelProperty(value= "是否可见")
	private String visible;
    /**
     * 能否排序
     */
	@ExcelProperty(value= "能否排序")
	private String sortable;

    /**
     * 是否是日期搜索
     */
	@ExcelProperty(value= "是否是日期搜索")
	private String dateselect;

    /**
     * 是否可搜索
     */
	@ExcelProperty(value= "是否可搜索")
	private String searchable;

	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 是否高亮
	 */
	private String highLight;

	/**
	 * 固定位置
	 */
	private String fixedLocation;

	/**
	 * 字典类型
	 */
	private String dictType;
	/**
	 * 是否多选
	 */
	private String isMultiple;
	/**
	 * 显示搜索
	 */
	private String showSearch;
	/**
	 * 仅显示，用于高亮搜索显示结果
	 */
	private String onlyShow;

	/**
	 * 关联项目类型
	 */
	private String projectType;


}
