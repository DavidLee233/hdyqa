package com.sysware.mainData.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;
/**
 * @project npic
 * @description HdlMainDataMappingBo业务入参对象，封装主数据映射查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlMainDataMappingBo extends BaseEntity {
    private Long mapId;
    private String sourceField;
    private String targetField;
    private String fieldMeaning;
    private String type;
    private String createId;
    private String updateId;


}