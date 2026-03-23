package com.sysware.mainData.domain.bo;

import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * @project npic
 * @description HdlMainDataSyncBatchBo业务入参对象，封装主数据同步批次记录查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlMainDataSyncBatchBo extends BaseEntity {
    private String batchNo;
    private String dataType;
    private String triggerMode;
    private String syncMode;
    private String success;
    private LocalDateTime startTimeBegin;
    private LocalDateTime startTimeEnd;
}