package com.sysware.system.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 页面按钮业务对象 sys_page_btn
 *
 * @author aa
 * @date 2023-05-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysPageBtnBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 按钮ID
     */
    private String btnId;

    /**
     * 按钮类型
     */
    @NotBlank(message = "按钮类型不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "按钮位置不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "按钮权限不能为空", groups = { AddGroup.class, EditGroup.class })
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
