package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;
/**
 * @project npic
 * @description HdlMainDataMapping领域实体，描述主数据映射核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_mapping")
public class HdlMainDataMapping extends BaseEntity {

    private static final long serialVersionUID=1L;
    @TableId(value = "map_id")
    private Long mapId;
    private String sourceField;
    private String targetField;
    private String fieldMeaning;
    private String type;
    private String createId;
    private String updateId;

}