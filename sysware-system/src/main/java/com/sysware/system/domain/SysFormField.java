package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * 表单配置对象 sys_form_field
 *
 * @author zzr
 * @date 2023-04-14
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_form_field")
public class SysFormField extends BaseEntity implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String fieldId;

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
     * 是否显示
     */
    private String disabled;
    /**
     * 能否多选
     */
    private String multiple;
    /**
     * 是否显示
     */
    private String showabled;
    /**
     * 动态关联字段
     */
    private String dynamicLabel;
    /**
     * 文件格式
     */
    private String fileType;
    /**
     * 备注
     */
    private String remark;


    @TableField(exist = false)
    private List<SysFormFieldRules> ruleList;

    @TableField(exist = false)
    private List<SysFormFieldVisible> visible;

    @TableField(exist = false)
    private Long visibleCount;

    @TableField(exist = false)
    private Long ruleCount;

}
