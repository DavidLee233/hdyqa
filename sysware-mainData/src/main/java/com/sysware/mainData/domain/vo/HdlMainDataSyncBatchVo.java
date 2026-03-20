package com.sysware.mainData.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 主数据同步批次记录视图对象
 */
@Data
public class HdlMainDataSyncBatchVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String batchNo;

    private String dataType;

    private String triggerMode;

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
