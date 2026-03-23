package com.sysware.mainData.service;

import java.util.Map;
import java.util.Set;

/**
 * @project npic
 * @description 主数据定时任务开关服务接口，负责提供三类主数据的定时同步与定时备份开关查询、更新及执行判断能力。
 * @author DavidLee233
 * @date 2026/3/21
 */
public interface IMainDataScheduleSwitchService {

    /**
     * @description 查询指定主数据类型的定时同步开关与定时备份开关状态，供前端页面初始化开关展示。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return Map<String, Object> 开关状态结果（包含数据类型、定时同步是否开启、定时备份是否开启），用于前端页面渲染当前状态。
     * @author DavidLee233
     * @date 2026/3/21
     */
    Map<String, Object> querySwitchStatus(String dataType);

    /**
     * @description 更新指定主数据类型的定时任务开关状态，并返回更新后的当前状态供前端回显。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     * @params enabled 开关目标状态（true开启、false关闭）。
     *
     * @return Map<String, Object> 更新后的开关状态结果（包含数据类型、定时同步是否开启、定时备份是否开启），用于前端提示与开关回显。
     * @author DavidLee233
     * @date 2026/3/21
     */
    Map<String, Object> updateSwitchStatus(String dataType, String switchType, Boolean enabled);

    /**
     * @description 判断指定主数据类型的定时同步任务是否允许由本地处理器继续执行。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return boolean 定时同步开关判定结果（true表示允许继续同步，false表示当前类型应跳过同步）。
     * @author DavidLee233
     * @date 2026/3/21
     */
    boolean isSyncEnabled(String dataType);

    /**
     * @description 判断指定主数据类型的定时备份任务是否允许由本地处理器继续纳入备份范围。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return boolean 定时备份开关判定结果（true表示允许纳入备份，false表示当前类型应跳过备份）。
     * @author DavidLee233
     * @date 2026/3/21
     */
    boolean isBackupEnabled(String dataType);

    /**
     * @description 汇总当前允许参与定时备份的主数据类型集合，供本地备份处理器筛选备份表范围。
     * @params 无
     *
     * @return Set<String> 已开启定时备份的数据类型编码集合，用于定时备份时筛选对应业务表。
     * @author DavidLee233
     * @date 2026/3/21
     */
    Set<String> listEnabledBackupTypes();
}
