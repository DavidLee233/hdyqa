package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 下游系统调用主数据拉取接口日志。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_downstream_log")
public class HdlMainDataDownstreamLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long downstreamSystemId;

    private String downstreamSystemName;

    private String downstreamSystemIp;

    private String requestUrl;

    /**
     * 1/2/3
     */
    private String dataType;

    private String dataTypeName;

    private LocalDateTime syncTime;

    /**
     * 1-成功，0-失败
     */
    private String success;

    private Integer recordCount;

    private Long durationMs;

    private String message;
}

