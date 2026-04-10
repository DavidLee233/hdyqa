package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 下游系统连接配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_main_data_downstream_system")
public class HdlMainDataDownstreamSystem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 下游系统名称。
     */
    private String systemName;

    /**
     * 下游系统 IP。
     */
    private String systemIp;

    /**
     * 下游系统端口，默认 80。
     */
    private Integer systemPort;

    /**
     * 最近一次连通性状态：1-已连接，0-未连接。
     */
    private String lastConnectStatus;

    /**
     * 最近一次检测时间。
     */
    private LocalDateTime lastCheckTime;

    /**
     * 最近一次检测描述。
     */
    private String lastCheckMessage;

    /**
     * IP连接状态：1-已连接，0-未连接（检测结果展示字段，不入库）。
     */
    @TableField(exist = false)
    private String ipConnectStatus;

    /**
     * IP检测信息（检测结果展示字段，不入库）。
     */
    @TableField(exist = false)
    private String ipCheckMessage;

    /**
     * 端口可用状态：1-可用，0-不可用（检测结果展示字段，不入库）。
     */
    @TableField(exist = false)
    private String portConnectStatus;

    /**
     * 端口检测信息（检测结果展示字段，不入库）。
     */
    @TableField(exist = false)
    private String portCheckMessage;
}
