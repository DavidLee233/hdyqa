package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 主数据同步批次记录对象 hdl_main_data_sync_batch
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_sync_batch")
public class HdlMainDataSyncBatch extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 结果消息
     */
    private String message;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 拉取数量
     */
    private Integer fetchedCount;

    /**
     * 新增数量
     */
    private Integer insertedCount;

    /**
     * 更新数量
     */
    private Integer updatedCount;

    /**
     * 失效数量
     */
    private Integer invalidatedCount;

    /**
     * 跳过数量
     */
    private Integer skippedCount;

    /**
     * 冲突数量
     */
    private Integer conflictCount;

    /**
     * 失败数量
     */
    private Integer failedCount;

    /**
     * 执行人名称或任务名
     */
    private String operatorName;

    /**
     * 执行人工号或任务标识
     */
    private String operatorId;
}
