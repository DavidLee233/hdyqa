package com.sysware.mainData.domain.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @project npic
 * @description MainDataScheduleSwitchBo业务对象，负责承载主数据定时同步与定时备份开关的前端切换请求参数。
 * @author DavidLee233
 * @date 2026/3/21
 */
@Data
public class MainDataScheduleSwitchBo {

    /**
     * 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     */
    @NotBlank(message = "主数据类型不能为空")
    private String dataType;

    /**
     * 开关类型（sync定时同步、backup定时备份）
     */
    @NotBlank(message = "开关类型不能为空")
    private String switchType;

    /**
     * 开关目标状态（true开启、false关闭）
     */
    @NotNull(message = "开关状态不能为空")
    private Boolean enabled;
}
