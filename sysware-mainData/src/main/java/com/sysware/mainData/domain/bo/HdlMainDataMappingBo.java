package com.sysware.mainData.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 主数据字段映射业务对象 hdl_main_data_mapping
 *
 * @author aa
 * @date 2026-01-14
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class HdlMainDataMappingBo extends BaseEntity {

    /**
     * 主键
     */
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
