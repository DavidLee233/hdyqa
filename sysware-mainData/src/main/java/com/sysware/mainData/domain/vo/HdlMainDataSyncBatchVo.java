package com.sysware.mainData.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @project npic
 * @description HdlMainDataSyncBatchVo业务出参对象，封装主数据同步批次记录返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class HdlMainDataSyncBatchVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String batchNo;

    private String dataType;

    private String triggerMode;

    private String syncMode;

    private String success;

    private String message;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long durationMs;

    private Integer fetchedCount;

    private Integer insertedCount;

    private Integer updatedCount;

    private Integer invalidatedCount;

    private Integer skippedCount;

    private Integer conflictCount;

    private Integer failedCount;

    private String operatorName;

    private String operatorId;
}