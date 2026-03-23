package com.sysware.mainData.domain.bo;

import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * @project npic
 * @description HdlMainDataBackupRecordBo业务入参对象，封装主数据备份记录查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlMainDataBackupRecordBo extends BaseEntity {

    private String batchNo;

    private String triggerMode;

    private String handlerName;

    private String success;

    private String fileName;

    private String operatorName;

    private LocalDateTime startTimeBegin;

    private LocalDateTime startTimeEnd;
}