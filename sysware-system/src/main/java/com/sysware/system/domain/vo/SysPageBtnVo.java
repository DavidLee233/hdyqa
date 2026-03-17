package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 页面按钮视图对象 sys_page_btn
 *
 * @author aa
 * @date 2023-06-15
 */
@Data
@ExcelIgnoreUnannotated
public class SysPageBtnVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 按钮ID
     */
    @ExcelProperty(value = "按钮ID")
    private String btnId;

    /**
     * 按钮类型
     */
    @ExcelProperty(value = "按钮类型")
    private String type;

    /**
     * 按钮图标
     */
    @ExcelProperty(value = "按钮图标")
    private String icon;

    /**
     * 按钮大小
     */
    @ExcelProperty(value = "按钮大小")
    private String size;

    /**
     * 按钮位置
     */
    @ExcelProperty(value = "按钮位置")
    private String location;

    /**
     * 按钮文字
     */
    @ExcelProperty(value = "按钮文字")
    private String text;

    /**
     * 隐藏文字
     */
    @ExcelProperty(value = "隐藏文字")
    private String showText;

    /**
     * 按钮权限
     */
    @ExcelProperty(value = "按钮权限")
    private String permission;

    /**
     * 禁用条件
     */
    @ExcelProperty(value = "禁用条件")
    private String disabledType;

    /**
     * 页面路径
     */
    @ExcelProperty(value = "页面路径")
    private String pagePath;

    /**
     * 方法名称
     */
    @ExcelProperty(value = "方法名称")
    private String functionName;

    /**
     * 弹窗标题
     */
    @ExcelProperty(value = "弹窗标题")
    private String title;

    /**
     * 弹窗类型
     */
    @ExcelProperty(value = "弹窗类型")
    private String openType;

    /**
     * 弹窗方式
     */
    @ExcelProperty(value = "弹窗方式")
    private String direction;

    /**
     * 表单ID
     */
    @ExcelProperty(value = "表单ID")
    private String formId;

    /**
     * 提交事件
     */
    @ExcelProperty(value = "提交事件")
    private String submitEventName;

    /**
     * 确认按钮
     */
    @ExcelProperty(value = "确认按钮")
    private String confirmText;

    /**
     * 取消按钮
     */
    @ExcelProperty(value = "取消按钮")
    private String cancelText;

    /**
     * 提示图标
     */
    @ExcelProperty(value = "提示图标")
    private String popConfirmIcon;

    /**
     * 图标颜色
     */
    @ExcelProperty(value = "图标颜色")
    private String color;

    /**
     * 提示信息
     */
    @ExcelProperty(value = "提示信息")
    private String msg;

    /**
     * 文字提示
     */
    @ExcelProperty(value = "文字提示")
    private String tipContent;

    /**
     * 排序
     */
    private int sort;


}