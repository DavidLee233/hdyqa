package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 本系统调用远端主数据接口日志（不包含 token 接口）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_remote_api_log")
public class HdlMainDataRemoteApiLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String requestUrl;

    /**
     * 1/2/3
     */
    private String dataType;

    private String dataTypeName;

    /**
     * sync/query/export
     */
    private String callSource;

    private LocalDateTime syncTime;

    /**
     * 1-成功，0-失败
     */
    private String success;

    private Integer recordCount;

    private Long durationMs;

    private String message;
}

