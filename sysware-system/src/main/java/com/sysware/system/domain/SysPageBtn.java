package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 页面按钮对象 sys_page_btn
 *
 * @author aa
 * @date 2023-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_page_btn")
public class SysPageBtn extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;
    /**
     * 按钮ID
     */
    private String btnId;
    /**
     * 按钮类型
     */
    private String type;
    /**
     * 按钮图标
     */
    private String icon;
    /**
     * 按钮大小
     */
    private String size;
    /**
     * 按钮位置
     */
    private String location;
    /**
     * 按钮文字
     */
    private String text;
    /**
     * 隐藏文字
     */
    private String showText;
    /**
     * 按钮权限
     */
    private String permission;
    /**
     * 禁用条件
     */
    private String disabledType;
    /**
     * 页面路径
     */
    private String pagePath;
    /**
     * 方法名称
     */
    private String functionName;
    /**
     * 弹窗标题
     */
    private String title;
    /**
     * 弹窗类型
     */
    private String openType;
    /**
     * 弹窗方式
     */
    private String direction;
    /**
     * 表单ID
     */
    private String formId;
    /**
     * 提交事件
     */
    private String submitEventName;
    /**
     * 确认按钮
     */
    private String confirmText;
    /**
     * 取消按钮
     */
    private String cancelText;
    /**
     * 提示图标
     */
    private String popConfirmIcon;
    /**
     * 图标颜色
     */
    private String color;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 文字提示
     */
    private String tipContent;
    /**
     * 排序
     */
    private int sort;
}