package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 主数据字段映射对象 hdl_main_data_mapping
 *
 * @author aa
 * @date 2026-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_mapping")
public class HdlMainDataMapping extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "map_id")
    private Long mapId;
    /**
     * 源数据字段
     */
    private String sourceField;
    /**
     * 目标数据字段
     */
    private String targetField;
    /**
     * 字段含义
     */
    private String fieldMeaning;
    /**
     * 字段类型（1组织部门、2员工基本信息、3员工工作信息）
     */
    private String type;
    /**
     * 创建者工号
     */
    private String createId;
    /**
     * 更新者工号
     */
    private String updateId;

}
