package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 自定义页面显示对象 sys_table_field
 *
 * @author aa
 * @date 2022-08-03
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_table_field")
public class SysTableField implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属用户的ID
     */
    private String userId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 页面ID
     */
    private String menuId;
    /**
     * 页面组件
     */
    private String path;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 对齐方式
     */
    private String align;

    /**
     * 插槽名称
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String slotName;

    /**
     * 字段名称
     */
    private String label;

    /**
     * 字段属性
     */
    private String prop;

    /**
     * 是否可见
     */
    private String visible;

    /**
     * 能否排序
     */
    private String sortable;

    /**
     * 是否是日期搜索
     */
    private String dateSelect;

    /**
     * 是否可搜索
     */
    private String searchable;
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
     * 关联显示的项目类型
     */
    private String projectType;

}
