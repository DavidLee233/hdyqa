package com.sysware.mainData.domain.bo;

import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 主数据同步批次记录业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlMainDataSyncBatchBo extends BaseEntity {

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 数据类型（1组织部门、2员工基本信息、3员工工作信息）
     */
    private String dataType;

    /**
     * 触发方式（manual/job）
     */
    private String triggerMode;

    /**
     * 是否成功（1成功、0失败）
     */
    private String success;

    /**
     * 开始时间范围-起
     */
    private LocalDateTime startTimeBegin;

    /**
     * 开始时间范围-止
     */
    private LocalDateTime startTimeEnd;
}
