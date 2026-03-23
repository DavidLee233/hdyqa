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
 * @description HdlMainDataBackupRecord领域实体，描述主数据备份记录核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_backup_record")
public class HdlMainDataBackupRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String triggerMode;
    private String handlerName;
    private String success;
    private String message;
    private String fileName;
    private String filePath;
    private Long fileSizeBytes;
    private Integer tableCount;
    private Integer rowCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private String operatorName;
    private String operatorId;
}