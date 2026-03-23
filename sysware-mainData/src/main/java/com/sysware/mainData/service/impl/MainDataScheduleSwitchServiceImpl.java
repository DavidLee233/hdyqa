package com.sysware.mainData.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.mainData.service.IMainDataScheduleSwitchService;
import com.sysware.system.domain.SysConfig;
import com.sysware.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @project npic
 * @description MainDataScheduleSwitchServiceImpl服务实现类，负责维护三类主数据定时同步与定时备份开关的配置读取、默认补齐和状态更新。
 * @author DavidLee233
 * @date 2026/3/21
 */
@Service
@RequiredArgsConstructor
public class MainDataScheduleSwitchServiceImpl implements IMainDataScheduleSwitchService {

    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";
    private static final String SWITCH_TYPE_SYNC = "sync";
    private static final String SWITCH_TYPE_BACKUP = "backup";
    private static final String DEFAULT_ENABLED_VALUE = "false";
    private static final List<String> SUPPORTED_TYPES = Arrays.asList(TYPE_ORG_DEPT, TYPE_PERSON_BASIC, TYPE_PERSON_JOB);
    private static final List<String> SUPPORTED_SWITCH_TYPES = Arrays.asList(SWITCH_TYPE_SYNC, SWITCH_TYPE_BACKUP);

    private final ISysConfigService sysConfigService;

    /**
     * @description 查询指定主数据类型的定时同步开关与定时备份开关状态，供前端页面初始化开关展示。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return Map<String, Object> 开关状态结果（包含数据类型、定时同步是否开启、定时备份是否开启），用于前端页面渲染当前状态。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public Map<String, Object> querySwitchStatus(String dataType) {
        String normalizedType = normalizeDataType(dataType);
        ensureSwitchConfigExists(normalizedType, SWITCH_TYPE_SYNC);
        ensureSwitchConfigExists(normalizedType, SWITCH_TYPE_BACKUP);

        Map<String, Object> result = new LinkedHashMap<>(4);
        result.put("dataType", normalizedType);
        result.put("syncEnabled", readEnabledValue(normalizedType, SWITCH_TYPE_SYNC));
        result.put("backupEnabled", readEnabledValue(normalizedType, SWITCH_TYPE_BACKUP));
        return result;
    }

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
    @Override
    public synchronized Map<String, Object> updateSwitchStatus(String dataType, String switchType, Boolean enabled) {
        String normalizedType = normalizeDataType(dataType);
        String normalizedSwitchType = normalizeSwitchType(switchType);
        boolean targetEnabled = enabled == null || enabled;
        ensureSwitchConfigExists(normalizedType, normalizedSwitchType);

        String configKey = buildConfigKey(normalizedType, normalizedSwitchType);
        SysConfig existing = findExactConfig(configKey);
        SysConfig config = existing == null ? buildDefaultConfig(normalizedType, normalizedSwitchType) : existing;
        config.setConfigValue(String.valueOf(targetEnabled));
        config.setRemark(buildRemark(normalizedType, normalizedSwitchType));
        config.setUpdateBy(resolveOperatorName());
        config.setUpdateTime(new Date());
        if (config.getConfigId() == null) {
            config.setCreateBy(resolveOperatorName());
            config.setCreateTime(new Date());
            sysConfigService.insertConfig(config);
        } else {
            sysConfigService.updateConfig(config);
        }
        return querySwitchStatus(normalizedType);
    }

    /**
     * @description 判断指定主数据类型的定时同步任务是否允许由本地处理器继续执行。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return boolean 定时同步开关判定结果（true表示允许继续同步，false表示当前类型应跳过同步）。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public boolean isSyncEnabled(String dataType) {
        return readEnabledValue(normalizeDataType(dataType), SWITCH_TYPE_SYNC);
    }

    /**
     * @description 判断指定主数据类型的定时备份任务是否允许由本地处理器继续纳入备份范围。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return boolean 定时备份开关判定结果（true表示允许纳入备份，false表示当前类型应跳过备份）。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public boolean isBackupEnabled(String dataType) {
        return readEnabledValue(normalizeDataType(dataType), SWITCH_TYPE_BACKUP);
    }

    /**
     * @description 汇总当前允许参与定时备份的主数据类型集合，供本地备份处理器筛选备份范围。
     * @params 无
     *
     * @return Set<String> 已开启定时备份的数据类型编码集合，用于定时备份处理器筛选业务表。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public Set<String> listEnabledBackupTypes() {
        Set<String> enabledTypes = new LinkedHashSet<>();
        for (String dataType : SUPPORTED_TYPES) {
            if (isBackupEnabled(dataType)) {
                enabledTypes.add(dataType);
            }
        }
        return enabledTypes;
    }

    /**
     * @description 读取指定主数据类型与开关类型的布尔值配置，不存在时自动补齐默认开启配置。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return boolean 开关状态结果（true表示开启，false表示关闭），用于任务执行前判定。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private boolean readEnabledValue(String dataType, String switchType) {
        ensureSwitchConfigExists(dataType, switchType);
        String value = sysConfigService.selectConfigByKey(buildConfigKey(dataType, switchType));
        if (StrUtil.isBlank(value)) {
            return true;
        }
        String normalized = value.trim().toLowerCase();
        return "1".equals(normalized) || "true".equals(normalized) || "yes".equals(normalized) || "y".equals(normalized);
    }

    /**
     * @description 确保指定主数据类型与开关类型对应的系统参数已存在，避免首次访问时因配置缺失导致状态不可控。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return void 无返回结果，方法执行后会补齐缺失的系统参数配置记录。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private synchronized void ensureSwitchConfigExists(String dataType, String switchType) {
        String configKey = buildConfigKey(dataType, switchType);
        if (findExactConfig(configKey) != null) {
            return;
        }
        sysConfigService.insertConfig(buildDefaultConfig(dataType, switchType));
    }

    /**
     * @description 根据主数据类型和开关类型构造默认系统参数对象，供首次补齐配置或缺省初始化使用。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return SysConfig 系统参数实体（包含参数名、参数键、默认值和备注），用于写入sys_config表。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private SysConfig buildDefaultConfig(String dataType, String switchType) {
        Date now = new Date();
        String operatorName = resolveOperatorName();
        return new SysConfig()
            .setConfigName(buildConfigName(dataType, switchType))
            .setConfigKey(buildConfigKey(dataType, switchType))
            .setConfigValue(DEFAULT_ENABLED_VALUE)
            .setConfigType("N")
            .setCreateBy(operatorName)
            .setCreateTime(now)
            .setUpdateBy(operatorName)
            .setUpdateTime(now)
            .setRemark(buildRemark(dataType, switchType));
    }

    /**
     * @description 按参数键精确查询主数据定时任务开关配置，避免模糊查询误命中其他相似参数。
     * @params configKey 系统参数键名。
     *
     * @return SysConfig 精确匹配到的系统参数实体；若不存在则返回null，用于后续决定新增还是更新。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private SysConfig findExactConfig(String configKey) {
        SysConfig condition = new SysConfig();
        condition.setConfigKey(configKey);
        List<SysConfig> list = sysConfigService.selectConfigList(condition);
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (SysConfig item : list) {
            if (item != null && StrUtil.equals(configKey, item.getConfigKey())) {
                return item;
            }
        }
        return null;
    }

    /**
     * @description 构造主数据定时任务开关对应的系统参数键名，确保前后端和处理器读取同一套配置键。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return String 系统参数键名（如main.data.schedule.sync.1.enabled），用于sys_config表存储与读取。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String buildConfigKey(String dataType, String switchType) {
        return "main.data.schedule." + switchType + "." + dataType + ".enabled";
    }

    /**
     * @description 构造主数据定时任务开关的中文参数名称，便于系统参数表与运维排查时识别配置含义。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return String 中文参数名称，用于sys_config表展示配置用途。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String buildConfigName(String dataType, String switchType) {
        return resolveDataTypeName(dataType) + "定时" + (SWITCH_TYPE_SYNC.equals(switchType) ? "同步" : "备份") + "开关";
    }

    /**
     * @description 构造主数据定时任务开关的备注说明，便于后续查看配置默认行为与业务范围。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return String 参数备注说明，用于sys_config表中的remark字段展示。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String buildRemark(String dataType, String switchType) {
        String actionName = SWITCH_TYPE_SYNC.equals(switchType) ? "定时同步" : "定时备份";
        return "主数据" + resolveDataTypeName(dataType) + actionName + "开关（true开启，false关闭）";
    }

    /**
     * @description 标准化并校验主数据类型编码，防止非法类型进入定时任务开关查询与更新流程。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return String 标准化后的主数据类型编码，用于后续配置键计算与业务判断。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String normalizeDataType(String dataType) {
        String value = StrUtil.blankToDefault(StrUtil.trim(dataType), TYPE_ORG_DEPT);
        if (!SUPPORTED_TYPES.contains(value)) {
            throw new IllegalArgumentException("不支持的主数据类型：" + dataType);
        }
        return value;
    }

    /**
     * @description 标准化并校验开关类型取值，确保仅允许更新定时同步或定时备份两类开关。
     * @params switchType 开关类型（sync定时同步、backup定时备份）。
     *
     * @return String 标准化后的开关类型，用于后续配置键计算与业务判断。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String normalizeSwitchType(String switchType) {
        String value = StrUtil.blankToDefault(StrUtil.trim(switchType), SWITCH_TYPE_SYNC).toLowerCase();
        if (!SUPPORTED_SWITCH_TYPES.contains(value)) {
            throw new IllegalArgumentException("不支持的开关类型：" + switchType);
        }
        return value;
    }

    /**
     * @description 将主数据类型编码转换为中文名称，供系统参数名称、提示文案和日志摘要统一使用。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return String 主数据类型中文名称，用于参数命名与界面提示展示。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String resolveDataTypeName(String dataType) {
        if (TYPE_PERSON_BASIC.equals(dataType)) {
            return "员工基本信息";
        }
        if (TYPE_PERSON_JOB.equals(dataType)) {
            return "员工工作信息";
        }
        return "组织部门";
    }

    /**
     * @description 解析当前操作人名称，在无人登录的处理器场景下回退为主数据定时任务专用操作人。
     * @params 无
     *
     * @return String 操作人名称，用于sys_config参数的创建人和更新人字段记录。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String resolveOperatorName() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (loginUser != null && StrUtil.isNotBlank(loginUser.getUsername())) {
                return loginUser.getUsername();
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "MAIN_DATA_SCHEDULE";
    }
}
