package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * @project npic
 * @description HdlMainDataSyncBatch领域实体，描述主数据同步批次记录核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_sync_batch")
public class HdlMainDataSyncBatch extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
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