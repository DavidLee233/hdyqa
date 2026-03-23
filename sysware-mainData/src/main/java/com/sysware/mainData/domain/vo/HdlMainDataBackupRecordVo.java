package com.sysware.mainData.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @project npic
 * @description HdlMainDataBackupRecordVo业务出参对象，封装主数据备份记录返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class HdlMainDataBackupRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

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