package com.sysware.mainData.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.mainData.config.RemoteDataConfig;
import com.sysware.mainData.domain.HdlMainDataBackupRecord;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.mapper.HdlOrganizationDepartmentMapper;
import com.sysware.mainData.mapper.HdlPersonBasicInfoMapper;
import com.sysware.mainData.mapper.HdlPersonJobInfoMapper;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.mainData.service.IHdlMainDataMappingService;
import com.sysware.mainData.service.IHdlMainDataBackupRecordService;
import com.sysware.mainData.service.IHdlMainDataSyncBatchService;
import com.sysware.mainData.service.IRemoteDataService;
import com.sysware.mainData.service.IRemoteTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @project npic
 * @description RemoteDataServiceImpl服务实现类，负责远端主数据业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RequiredArgsConstructor
@Service
public class RemoteDataServiceImpl implements IRemoteDataService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDataServiceImpl.class);
    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";
    private static final String SYNC_OPERATOR = "REMOTE_SYNC";
    private static final String SYNC_OPERATOR_ID = "remote_sync";
    private static final String ENABLE_ACTIVE = "2";
    private static final String ENABLE_INVALID = "3";
    private static final String END_FLAG_ACTIVE = "N";
    private static final String END_FLAG_INVALID = "Y";
    private static final String TRIGGER_MODE_MANUAL = "manual";
    private static final String TRIGGER_MODE_HANDLER = "handler";
    private static final String SYNC_MODE_FULL = "full";
    private static final String SYNC_MODE_INCREMENTAL = "incremental";
    private static final String SYNC_MODE_AUTO = "auto";
    private static final int REMOTE_PAGE_SIZE = 500;
    private static final int MAX_REMOTE_PAGE = 2000;
    private static final List<String> INCREMENTAL_FIELD_CANDIDATES = Arrays.asList(
        "UPDATETIME", "UPDATE_TIME", "MODIFIEDTIME", "MODIFYTIME", "LASTMODIFIEDTIME", "LAST_UPDATE_TIME", "TS", "TIMESTAMP"
    );
    private static final DateTimeFormatter[] TIME_FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    };
    private static final long CACHE_EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    @Autowired
    private RemoteDataConfig remoteDataConfig;

    @Autowired
    private IRemoteTokenService remoteTokenService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IHdlMainDataMappingService mainDataMappingService;

    @Autowired
    private HdlOrganizationDepartmentMapper organizationDepartmentMapper;

    @Autowired
    private HdlPersonBasicInfoMapper personBasicInfoMapper;

    @Autowired
    private HdlPersonJobInfoMapper personJobInfoMapper;

    @Autowired
    private IHdlMainDataSyncBatchService mainDataSyncBatchService;

    @Autowired
    private IHdlMainDataBackupRecordService backupRecordService;

    @Value("${main-data.sync-backup-min-interval-minutes:60}")
    private long syncBackupMinIntervalMinutes;
    @Value("${main-data.sync-audit-log-dir:logs}")
    private String syncAuditLogDir;
    private final Map<String, Map<String, String>> cachedFieldMappingByType = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> cachedReverseMappingByType = new ConcurrentHashMap<>();
    private final Map<String, Long> lastLoadTimeByType = new ConcurrentHashMap<>();
    /**
     * @description 查询远端组织部门数据并转换为统一返回结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> queryRemoteDepartments(Map<String, Object> params) {
        return queryRemoteData(
            params,
            TYPE_ORG_DEPT,
            remoteDataConfig.getOrgnizationDepartmentPath(),
            buildOrgFilterMatchTypes(),
            buildOrgFilterDefaultRemoteFields()
        );
    }
    /**
     * @description 查询远端员工基本信息并转换为统一返回结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> queryRemotePersonBasicInfos(Map<String, Object> params) {
        return queryRemoteData(
            params,
            TYPE_PERSON_BASIC,
            remoteDataConfig.getPersonBasicPath(),
            buildPersonBasicFilterMatchTypes(),
            buildPersonBasicFilterDefaultRemoteFields()
        );
    }
    /**
     * @description 查询远端员工工作信息并转换为统一返回结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> queryRemotePersonJobInfos(Map<String, Object> params) {
        return queryRemoteData(
            params,
            TYPE_PERSON_JOB,
            remoteDataConfig.getPersonJobPath(),
            buildPersonJobFilterMatchTypes(),
            buildPersonJobFilterDefaultRemoteFields()
        );
    }
    /**
     * @description 调用远端主数据接口并解析响应为本系统结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> queryRemoteData(Map<String, Object> params,
                                                 String mainDataType,
                                                 String apiPath,
                                                 Map<String, String> filterMatchTypes,
                                                 Map<String, String> defaultRemoteFields) {
        return queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields, true);
    }

    /**
     * @description 调用远端主数据接口并解析响应为本系统结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     * @params allowRetryOnUnauthorized 鉴权失败后是否允许自动刷新令牌并重试
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> queryRemoteData(Map<String, Object> params,
                                                 String mainDataType,
                                                 String apiPath,
                                                 Map<String, String> filterMatchTypes,
                                                 Map<String, String> defaultRemoteFields,
                                                 boolean allowRetryOnUnauthorized) {
        try {
            String token = remoteTokenService.getValidToken();
            if (StringUtils.isEmpty(token)) {
                throw new RuntimeException("无法获取有效的API Token");
            }

            int pageNum = parseInt(params.get("pageNum"), 0);
            int pageSize = parseInt(params.get("pageSize"), 10);
            String url = remoteDataConfig.getApiUrl() + apiPath + "?pageNum=" + pageNum + "&pageSize=" + pageSize;

            Map<String, Object> requestBody = buildQueryRequestBody(params, mainDataType, filterMatchTypes, defaultRemoteFields);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.info("发送远端主数据查询请求，类型={}，地址={}，请求体={}", mainDataType, url, requestBody);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("查询远端主数据失败，类型={}，状态码={}", mainDataType, response.getStatusCode());
                throw new RuntimeException("查询远端主数据失败，状态码: " + response.getStatusCode());
            }

            return convertResponseFormat(response.getBody(), mainDataType);
        } catch (HttpClientErrorException.Unauthorized e) {
            if (!allowRetryOnUnauthorized) {
                throw new RuntimeException("Token无效且刷新后重试失败", e);
            }
            logger.warn("令牌无效或过期，刷新后重试一次。类型={}", mainDataType);
            remoteTokenService.refreshToken();
            return queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields, false);
        } catch (HttpClientErrorException e) {
            logger.error("查询远端主数据请求错误，类型={}，状态码={}，响应体={}",
                mainDataType, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("查询远端主数据HTTP错误: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("远端接口连接失败，类型={}，异常信息={}", mainDataType, e.getMessage(), e);
            throw new RuntimeException("远端API连接失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("查询远端主数据异常，类型={}", mainDataType, e);
            throw new RuntimeException("查询远端主数据异常: " + e.getMessage(), e);
        }
    }
    /**
     * @description 构建远端查询请求体，包含过滤与字段映射配置。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> buildQueryRequestBody(Map<String, Object> params,
                                                      String mainDataType,
                                                      Map<String, String> filterMatchTypes,
                                                      Map<String, String> defaultRemoteFields) {
        Map<String, Object> requestBody = new HashMap<>();

        List<Map<String, Object>> args = buildQueryArgs(params, mainDataType, filterMatchTypes, defaultRemoteFields);
        if (!args.isEmpty()) {
            requestBody.put("args", args);
            requestBody.put("and", true);
        }

        if (params.containsKey("sortField") && params.containsKey("sortOrder")) {
            List<Map<String, Object>> sorts = new ArrayList<>();
            Map<String, Object> sort = new HashMap<>();
            sort.put("name", params.get("sortField"));
            sort.put("asc", "asc".equals(params.get("sortOrder")));
            sorts.add(sort);
            requestBody.put("sorts", sorts);
        }

        requestBody.put("withSysColumn", false);
        return requestBody;
    }
    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return List<Map<String, Object>> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<Map<String, Object>> buildQueryArgs(Map<String, Object> params,
                                                     String mainDataType,
                                                     Map<String, String> filterMatchTypes,
                                                     Map<String, String> defaultRemoteFields) {
        List<Map<String, Object>> args = new ArrayList<>();
        Map<String, String> fieldMapping = getCachedFieldMapping(mainDataType);
        if (filterMatchTypes == null || filterMatchTypes.isEmpty()) {
            return args;
        }

        for (Map.Entry<String, String> filterRule : filterMatchTypes.entrySet()) {
            String localField = filterRule.getKey();
            String matchType = filterRule.getValue();
            Object value = params.get(localField);
            if (value == null || StringUtils.isEmpty(value.toString())) {
                continue;
            }
            String remoteField = fieldMapping.get(localField);
            if (StrUtil.isBlank(remoteField) && defaultRemoteFields != null) {
                remoteField = defaultRemoteFields.get(localField);
            }
            if (StrUtil.isBlank(remoteField)) {
                logger.warn("字段映射缺失，跳过该查询条件。类型={}，本地字段={}", mainDataType, localField);
                continue;
            }
            args.add(buildQueryArg(remoteField, value, matchType));
        }
        return args;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params field 字段名称
     * @params value 待转换字段值
     * @params matchType 字段匹配方式
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> buildQueryArg(String field, Object value, String matchType) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("name", field);
        arg.put("matchValue", value);
        arg.put("matchType", matchType);
        arg.put("matchOption", "all");
        return arg;
    }
    /**
     * @description 转换远端响应为前端表格所需的数据结构。
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertResponseFormat(Map<String, Object> response, String mainDataType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = extractRows(response);
            long total = extractTotal(response, data);
            Map<String, String> reverseMapping = getCachedReverseMapping(mainDataType);
            List<Map<String, Object>> rows = new ArrayList<>();
            if (data != null) {
                for (Map<String, Object> remoteRow : data) {
                    Map<String, Object> localRow = new HashMap<>();
                    if (remoteRow != null) {
                        localRow.putAll(remoteRow);
                    }
                    if (remoteRow != null && reverseMapping != null && !reverseMapping.isEmpty()) {
                        for (Map.Entry<String, String> entry : reverseMapping.entrySet()) {
                            String remoteField = entry.getKey();
                            String localField = entry.getValue();
                            if (remoteRow.containsKey(remoteField) && remoteRow.get(remoteField) != null) {
                                localRow.put(localField, remoteRow.get(remoteField));
                            }
                        }
                    }
                    rows.add(localRow);
                }
            }
            result.put("rows", rows);
            result.put("total", total);
        } catch (Exception e) {
            logger.error("转换远端响应格式失败，类型={}", mainDataType, e);
            result.put("rows", new ArrayList<>());
            result.put("total", 0L);
        }
        return result;
    }

    /**
     * @description 执行extractRows方法，完成远端主数据相关业务处理。
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return List<Map<String, Object>> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRows(Map<String, Object> response) {
        Object rows = response.get("rows");
        if (rows instanceof List) {
            return (List<Map<String, Object>>) rows;
        }
        Object data = response.get("data");
        if (data instanceof List) {
            return (List<Map<String, Object>>) data;
        }
        return new ArrayList<>();
    }

    /**
     * @description 执行extractTotal方法，完成远端主数据相关业务处理。
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     * @params rows 导出或转换后的数据行集合
     *
      * @return long 数值型业务处理结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private long extractTotal(Map<String, Object> response, List<Map<String, Object>> rows) {
        Object totalObj = response.get("total");
        if (totalObj != null) {
            try {
                return Long.parseLong(totalObj.toString());
            } catch (NumberFormatException e) {
                logger.warn("远端响应总数字段格式异常，使用数据行数兜底。总数={}", totalObj);
            }
        }
        return rows == null ? 0L : rows.size();
    }
    /**
     * @description 获取远端主数据相关信息并返回调用方。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> getCachedFieldMapping(String type) {
        long currentTime = System.currentTimeMillis();
        Long lastLoadTime = lastLoadTimeByType.get(type);
        if (!cachedFieldMappingByType.containsKey(type) || lastLoadTime == null || (currentTime - lastLoadTime) > CACHE_EXPIRE_TIME) {
            reloadFieldMapping(type);
        }
        return cachedFieldMappingByType.getOrDefault(type, new HashMap<>());
    }
    /**
     * @description 获取远端主数据相关信息并返回调用方。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> getCachedReverseMapping(String type) {
        if (!cachedReverseMappingByType.containsKey(type)) {
            reloadFieldMapping(type);
        }
        return cachedReverseMappingByType.getOrDefault(type, new HashMap<>());
    }
    /**
     * @description 执行reloadFieldMapping方法，完成远端主数据相关业务处理。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private synchronized void reloadFieldMapping(String type) {
        // selectMainDataMappingByType 内部兼容旧值：1/2/3 -> 0/1/2
        List<HdlMainDataMappingVo> mappingVos = mainDataMappingService.selectMainDataMappingByType(type);
        if (mappingVos == null || mappingVos.isEmpty()) {
            cachedFieldMappingByType.put(type, new HashMap<>());
            cachedReverseMappingByType.put(type, new HashMap<>());
            lastLoadTimeByType.put(type, System.currentTimeMillis());
            logger.warn("未找到字段映射配置，类型={}", type);
            return;
        }

        Map<String, String> fieldMapping = mappingVos.stream()
            .filter(vo -> vo != null
                && StrUtil.isNotBlank(vo.getSourceField())
                && StrUtil.isNotBlank(vo.getTargetField()))
            .collect(Collectors.toMap(
                HdlMainDataMappingVo::getTargetField,
                HdlMainDataMappingVo::getSourceField,
                (existing, replacement) -> existing
            ));
        Map<String, String> reverseMapping = fieldMapping.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getValue,
                Map.Entry::getKey,
                (existing, replacement) -> existing
            ));

        cachedFieldMappingByType.put(type, fieldMapping);
        cachedReverseMappingByType.put(type, reverseMapping);
        lastLoadTimeByType.put(type, System.currentTimeMillis());
    }
    /**
     * @description 导出远端主数据数据并输出为文件流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public byte[] exportRemoteDepartments(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemoteDepartments, HdlOrganizationDepartmentVo.class, "组织部门数据");
    }

    /**
     * @description 导出远端主数据数据并输出为文件流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public byte[] exportRemotePersonBasicInfos(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemotePersonBasicInfos, HdlPersonBasicInfoVo.class, "员工基本信息数据");
    }

    /**
     * @description 导出远端主数据数据并输出为文件流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public byte[] exportRemotePersonJobInfos(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemotePersonJobInfos, HdlPersonJobInfoVo.class, "员工工作信息数据");
    }

    /**
     * @description 执行组织部门远端到本地同步并记录批次统计。
     * @params 无
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncDepartments() {
        return forceSyncDepartments(SYNC_MODE_FULL, TRIGGER_MODE_MANUAL);
    }

    /**
     * @description 执行组织部门远端到本地同步并记录批次统计。
     * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncDepartments(String syncMode, String triggerMode) {
        SyncStats stats = SyncStats.start(
            TYPE_ORG_DEPT,
            "organizationDepartment",
            normalizeTriggerMode(triggerMode),
            normalizeSyncMode(syncMode),
            resolveOperatorName(),
            resolveOperatorId()
        );
        captureFieldMappingAudit(stats, null);
        String intervalMessage = validateSyncInterval();
        if (StrUtil.isNotBlank(intervalMessage)) {
            stats.fail(intervalMessage);
            return finishAndPersist(stats);
        }
        try {
            List<Map<String, Object>> remoteRows = fetchRemoteRowsForSync(
                stats,
                TYPE_ORG_DEPT,
                remoteDataConfig.getOrgnizationDepartmentPath(),
                buildOrgFilterMatchTypes(),
                buildOrgFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();
            captureFieldMappingAudit(stats, remoteRows);

            Map<String, HdlOrganizationDepartment> localMap = organizationDepartmentMapper.selectList(Wrappers.lambdaQuery(HdlOrganizationDepartment.class))
                .stream()
                .filter(item -> item != null && StrUtil.isNotBlank(item.getPkDept()))
                .collect(Collectors.toMap(HdlOrganizationDepartment::getPkDept, Function.identity(), (a, b) -> a));

            Set<String> remoteIds = new HashSet<>();
            for (Map<String, Object> row : remoteRows) {
                try {
                    HdlOrganizationDepartment incoming = mapRowToEntity(row, HdlOrganizationDepartment.class);
                    String id = incoming.getPkDept();
                    if (StrUtil.isBlank(id)) {
                        stats.failed++;
                        continue;
                    }
                    if (!remoteIds.add(id)) {
                        stats.skipped++;
                        continue;
                    }
                    incoming.setEnableState(normalizeEnableState(incoming.getEnableState()));
                    HdlOrganizationDepartment existing = localMap.get(id);
                    if (existing == null) {
                        applySyncCreateAudit(incoming);
                        organizationDepartmentMapper.insert(incoming);
                        stats.inserted++;
                    } else {
                        mergeDepartment(existing, incoming);
                        applySyncUpdateAudit(existing);
                        organizationDepartmentMapper.updateById(existing);
                        stats.updated++;
                    }
                } catch (Exception ex) {
                    stats.failed++;
                    logger.error("强制同步组织部门单行数据失败，行数据={}", row, ex);
                }
            }

            if (!SYNC_MODE_INCREMENTAL.equals(stats.syncMode)) {
                for (HdlOrganizationDepartment local : localMap.values()) {
                    if (local == null || StrUtil.isBlank(local.getPkDept()) || remoteIds.contains(local.getPkDept())) {
                        continue;
                    }
                    if (!isRemoteManaged(local.getCreateId(), local.getUpdateId())) {
                        stats.skipped++;
                        continue;
                    }
                    if (ENABLE_INVALID.equals(local.getEnableState())) {
                        stats.skipped++;
                        continue;
                    }
                    local.setEnableState(ENABLE_INVALID);
                    applySyncUpdateAudit(local);
                    organizationDepartmentMapper.updateById(local);
                    stats.invalidated++;
                }
            }
            markFailedIfNeeded(stats, "组织部门");
        } catch (Exception e) {
            stats.fail("force sync organization department failed: " + e.getMessage());
            logger.error("强制同步组织部门失败", e);
        }
        return finishAndPersist(stats);
    }

    /**
     * @description 执行员工基本信息远端到本地同步并记录批次统计。
     * @params 无
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncPersonBasicInfos() {
        return forceSyncPersonBasicInfos(SYNC_MODE_FULL, TRIGGER_MODE_MANUAL);
    }

    /**
     * @description 执行员工基本信息远端到本地同步并记录批次统计。
     * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncPersonBasicInfos(String syncMode, String triggerMode) {
        SyncStats stats = SyncStats.start(
            TYPE_PERSON_BASIC,
            "personBasicInfo",
            normalizeTriggerMode(triggerMode),
            normalizeSyncMode(syncMode),
            resolveOperatorName(),
            resolveOperatorId()
        );
        captureFieldMappingAudit(stats, null);
        String intervalMessage = validateSyncInterval();
        if (StrUtil.isNotBlank(intervalMessage)) {
            stats.fail(intervalMessage);
            return finishAndPersist(stats);
        }
        try {
            List<Map<String, Object>> remoteRows = fetchRemoteRowsForSync(
                stats,
                TYPE_PERSON_BASIC,
                remoteDataConfig.getPersonBasicPath(),
                buildPersonBasicFilterMatchTypes(),
                buildPersonBasicFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();
            captureFieldMappingAudit(stats, remoteRows);

            Map<String, HdlPersonBasicInfo> localMap = personBasicInfoMapper.selectList(Wrappers.lambdaQuery(HdlPersonBasicInfo.class))
                .stream()
                .filter(item -> item != null && StrUtil.isNotBlank(item.getPkPsndoc()))
                .collect(Collectors.toMap(HdlPersonBasicInfo::getPkPsndoc, Function.identity(), (a, b) -> a));

            Set<String> remoteIds = new HashSet<>();
            for (Map<String, Object> row : remoteRows) {
                try {
                    HdlPersonBasicInfo incoming = mapRowToEntity(row, HdlPersonBasicInfo.class);
                    String id = incoming.getPkPsndoc();
                    if (StrUtil.isBlank(id)) {
                        stats.failed++;
                        continue;
                    }
                    if (!remoteIds.add(id)) {
                        stats.skipped++;
                        continue;
                    }
                    incoming.setEnableState(normalizeEnableState(incoming.getEnableState()));
                    HdlPersonBasicInfo existing = localMap.get(id);
                    if (existing == null) {
                        applySyncCreateAudit(incoming);
                        personBasicInfoMapper.insert(incoming);
                        stats.inserted++;
                    } else {
                        mergePersonBasic(existing, incoming);
                        applySyncUpdateAudit(existing);
                        personBasicInfoMapper.updateById(existing);
                        stats.updated++;
                    }
                } catch (Exception ex) {
                    stats.failed++;
                    logger.error("强制同步员工基本信息单行数据失败，行数据={}", row, ex);
                }
            }

            if (!SYNC_MODE_INCREMENTAL.equals(stats.syncMode)) {
                for (HdlPersonBasicInfo local : localMap.values()) {
                    if (local == null || StrUtil.isBlank(local.getPkPsndoc()) || remoteIds.contains(local.getPkPsndoc())) {
                        continue;
                    }
                    if (!isRemoteManaged(local.getCreateId(), local.getUpdateId())) {
                        stats.skipped++;
                        continue;
                    }
                    if (ENABLE_INVALID.equals(local.getEnableState())) {
                        stats.skipped++;
                        continue;
                    }
                    local.setEnableState(ENABLE_INVALID);
                    applySyncUpdateAudit(local);
                    personBasicInfoMapper.updateById(local);
                    stats.invalidated++;
                }
            }
            markFailedIfNeeded(stats, "员工基本信息");
        } catch (Exception e) {
            stats.fail("force sync person basic failed: " + e.getMessage());
            logger.error("强制同步员工基本信息失败", e);
        }
        return finishAndPersist(stats);
    }

    /**
     * @description 执行员工工作信息远端到本地同步并记录批次统计。
     * @params 无
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncPersonJobInfos() {
        return forceSyncPersonJobInfos(SYNC_MODE_FULL, TRIGGER_MODE_MANUAL);
    }

    /**
     * @description 执行员工工作信息远端到本地同步并记录批次统计。
     * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> forceSyncPersonJobInfos(String syncMode, String triggerMode) {
        SyncStats stats = SyncStats.start(
            TYPE_PERSON_JOB,
            "personJobInfo",
            normalizeTriggerMode(triggerMode),
            normalizeSyncMode(syncMode),
            resolveOperatorName(),
            resolveOperatorId()
        );
        captureFieldMappingAudit(stats, null);
        String intervalMessage = validateSyncInterval();
        if (StrUtil.isNotBlank(intervalMessage)) {
            stats.fail(intervalMessage);
            return finishAndPersist(stats);
        }
        try {
            List<Map<String, Object>> remoteRows = fetchRemoteRowsForSync(
                stats,
                TYPE_PERSON_JOB,
                remoteDataConfig.getPersonJobPath(),
                buildPersonJobFilterMatchTypes(),
                buildPersonJobFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();
            captureFieldMappingAudit(stats, remoteRows);

            Map<String, HdlPersonJobInfo> localMap = personJobInfoMapper.selectList(Wrappers.lambdaQuery(HdlPersonJobInfo.class))
                .stream()
                .filter(item -> item != null && StrUtil.isNotBlank(item.getPkPsnjob()))
                .collect(Collectors.toMap(HdlPersonJobInfo::getPkPsnjob, Function.identity(), (a, b) -> a));

            Set<String> remoteIds = new HashSet<>();
            for (Map<String, Object> row : remoteRows) {
                try {
                    HdlPersonJobInfo incoming = mapRowToEntity(row, HdlPersonJobInfo.class);
                    String id = incoming.getPkPsnjob();
                    if (StrUtil.isBlank(id)) {
                        stats.failed++;
                        continue;
                    }
                    if (!remoteIds.add(id)) {
                        stats.skipped++;
                        continue;
                    }
                    if (StrUtil.isBlank(incoming.getEndFlag())) {
                        incoming.setEndFlag(END_FLAG_ACTIVE);
                    }
                    HdlPersonJobInfo existing = localMap.get(id);
                    if (existing == null) {
                        applySyncCreateAudit(incoming);
                        personJobInfoMapper.insert(incoming);
                        stats.inserted++;
                    } else {
                        mergePersonJob(existing, incoming);
                        applySyncUpdateAudit(existing);
                        personJobInfoMapper.updateById(existing);
                        stats.updated++;
                    }
                } catch (Exception ex) {
                    stats.failed++;
                    logger.error("强制同步员工工作信息单行数据失败，行数据={}", row, ex);
                }
            }

            if (!SYNC_MODE_INCREMENTAL.equals(stats.syncMode)) {
                for (HdlPersonJobInfo local : localMap.values()) {
                    if (local == null || StrUtil.isBlank(local.getPkPsnjob()) || remoteIds.contains(local.getPkPsnjob())) {
                        continue;
                    }
                    if (!isRemoteManaged(local.getCreateId(), local.getUpdateId())) {
                        stats.skipped++;
                        continue;
                    }
                    if (END_FLAG_INVALID.equalsIgnoreCase(local.getEndFlag())) {
                        stats.skipped++;
                        continue;
                    }
                    local.setEndFlag(END_FLAG_INVALID);
                    applySyncUpdateAudit(local);
                    personJobInfoMapper.updateById(local);
                    stats.invalidated++;
                }
            }
            markFailedIfNeeded(stats, "员工工作信息");
        } catch (Exception e) {
            stats.fail("force sync person job failed: " + e.getMessage());
            logger.error("强制同步员工工作信息失败", e);
        }
        return finishAndPersist(stats);
    }

    /**
     * @description 根据失败条数统一补齐失败判定，避免同步出现失败记录却仍返回成功状态。
     * @params stats 同步统计对象（包含成功标识、统计数据与错误信息）
     * @params dataTypeName 数据类型名称（用于拼装失败提示）
     *
     * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void markFailedIfNeeded(SyncStats stats, String dataTypeName) {
        if (stats == null || stats.failed <= 0 || !stats.success) {
            return;
        }
        stats.fail(String.format("%s同步存在 %d 条失败记录，请检查批次记录与日志明细", dataTypeName, stats.failed));
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params stats 统计结果对象（同步或备份执行统计）
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return List<Map<String, Object>> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<Map<String, Object>> fetchRemoteRowsForSync(SyncStats stats,
                                                             String mainDataType,
                                                             String apiPath,
                                                             Map<String, String> filterMatchTypes,
                                                             Map<String, String> defaultRemoteFields) {
        String requestedMode = normalizeSyncMode(stats.syncMode);
        HdlMainDataSyncBatch latestSuccess = mainDataSyncBatchService.queryLatestSuccessByType(mainDataType);

        if (SYNC_MODE_AUTO.equals(requestedMode)) {
            requestedMode = latestSuccess == null ? SYNC_MODE_FULL : SYNC_MODE_INCREMENTAL;
        }
        if (SYNC_MODE_INCREMENTAL.equals(requestedMode) && latestSuccess == null) {
            requestedMode = SYNC_MODE_FULL;
            stats.note("No successful sync batch found, fallback to full sync.");
        }
        stats.syncMode = requestedMode;

        if (SYNC_MODE_INCREMENTAL.equals(requestedMode)) {
            LocalDateTime watermark = latestSuccess == null ? null : latestSuccess.getEndTime();
            if (watermark == null) {
                stats.syncMode = SYNC_MODE_FULL;
                stats.note("No incremental watermark found, fallback to full sync.");
                return fetchAllRemoteRows(mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
            }
            return fetchIncrementalRemoteRows(mainDataType, apiPath, filterMatchTypes, defaultRemoteFields, watermark, stats);
        }
        return fetchAllRemoteRows(mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
    }

    /**
     * @description 执行fetchAllRemoteRows方法，完成远端主数据相关业务处理。
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return List<Map<String, Object>> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchAllRemoteRows(String mainDataType,
                                                         String apiPath,
                                                         Map<String, String> filterMatchTypes,
                                                         Map<String, String> defaultRemoteFields) {
        List<Map<String, Object>> allRows = new ArrayList<>();
        int pageNum = 0;
        int pageSize = REMOTE_PAGE_SIZE;
        long total = -1L;
        while (true) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNum", pageNum);
            params.put("pageSize", pageSize);
            Map<String, Object> result = queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
            if (rows == null || rows.isEmpty()) {
                break;
            }
            allRows.addAll(rows);
            total = parseLong(result.get("total"), total);
            if (rows.size() < pageSize) {
                break;
            }
            if (total >= 0 && allRows.size() >= total) {
                break;
            }
            pageNum++;
            if (pageNum > MAX_REMOTE_PAGE) {
                logger.warn("拉取远端数据超过最大分页阈值，类型={}", mainDataType);
                break;
            }
        }
        return allRows;
    }

    /**
     * @description 执行fetchIncrementalRemoteRows方法，完成远端主数据相关业务处理。
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     * @params watermark 增量同步时间水位
     * @params stats 统计结果对象（同步或备份执行统计）
     *
      * @return List<Map<String, Object>> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchIncrementalRemoteRows(String mainDataType,
                                                                 String apiPath,
                                                                 Map<String, String> filterMatchTypes,
                                                                 Map<String, String> defaultRemoteFields,
                                                                 LocalDateTime watermark,
                                                                 SyncStats stats) {
        String incrementalField = detectIncrementalRemoteField(mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
        if (StrUtil.isBlank(incrementalField)) {
            stats.syncMode = SYNC_MODE_FULL;
            stats.note("Incremental field not found, fallback to full sync.");
            return fetchAllRemoteRows(mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
        }

        List<Map<String, Object>> incrementalRows = new ArrayList<>();
        int pageNum = 0;
        int pageSize = REMOTE_PAGE_SIZE;
        long total = -1L;
        while (true) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNum", pageNum);
            params.put("pageSize", pageSize);
            params.put("sortField", incrementalField);
            params.put("sortOrder", "desc");
            Map<String, Object> result = queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
            if (rows == null || rows.isEmpty()) {
                break;
            }

            boolean pageHasUnknownTime = false;
            boolean pageHasNewer = false;
            for (Map<String, Object> row : rows) {
                Object fieldValue = getFieldValueIgnoreCase(row, incrementalField);
                LocalDateTime rowTime = parseDateTime(fieldValue);
                if (rowTime == null) {
                    pageHasUnknownTime = true;
                    incrementalRows.add(row);
                    continue;
                }
                if (rowTime.isAfter(watermark)) {
                    pageHasNewer = true;
                    incrementalRows.add(row);
                }
            }

            total = parseLong(result.get("total"), total);
            if (rows.size() < pageSize) {
                break;
            }
            if (!pageHasUnknownTime && !pageHasNewer) {
                break;
            }
            if (total >= 0 && (long) (pageNum + 1) * pageSize >= total) {
                break;
            }
            pageNum++;
            if (pageNum > MAX_REMOTE_PAGE) {
                logger.warn("拉取远端增量数据超过最大分页阈值，类型={}", mainDataType);
                break;
            }
        }
        stats.note("Incremental sync watermark: " + watermark);
        return incrementalRows;
    }

    /**
     * @description 执行detectIncrementalRemoteField方法，完成远端主数据相关业务处理。
     * @params mainDataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params apiPath 远端API相对路径
     * @params filterMatchTypes 筛选字段匹配方式配置
     * @params defaultRemoteFields 远端字段默认映射配置
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private String detectIncrementalRemoteField(String mainDataType,
                                                String apiPath,
                                                Map<String, String> filterMatchTypes,
                                                Map<String, String> defaultRemoteFields) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", 0);
        params.put("pageSize", 1);
        Map<String, Object> result = queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields);
        Object rowsObj = result.get("rows");
        if (!(rowsObj instanceof List) || ((List<?>) rowsObj).isEmpty()) {
            return null;
        }
        Object first = ((List<?>) rowsObj).get(0);
        if (!(first instanceof Map)) {
            return null;
        }
        Map<String, Object> firstRow = (Map<String, Object>) first;
        if (firstRow.isEmpty()) {
            return null;
        }
        for (String candidate : INCREMENTAL_FIELD_CANDIDATES) {
            for (String key : firstRow.keySet()) {
                if (key != null && candidate.equalsIgnoreCase(key)) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * @description 获取远端主数据相关信息并返回调用方。
     * @params row 单条数据行对象
     * @params field 字段名称
     *
      * @return Object 通用对象结果，具体结构由调用上下文决定。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Object getFieldValueIgnoreCase(Map<String, Object> row, String field) {
        if (row == null || row.isEmpty() || StrUtil.isBlank(field)) {
            return null;
        }
        if (row.containsKey(field)) {
            return row.get(field);
        }
        String lower = field.toLowerCase(Locale.ROOT);
        if (row.containsKey(lower)) {
            return row.get(lower);
        }
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.equalsIgnoreCase(field)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params value 待转换字段值
     *
      * @return LocalDateTime 本地日期时间结果，用于同步水位或时间过滤。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.util.Date) {
            return LocalDateTime.ofInstant(((java.util.Date) value).toInstant(), ZoneId.systemDefault());
        }
        if (value instanceof Number) {
            long time = ((Number) value).longValue();
            if (String.valueOf(Math.abs(time)).length() == 10) {
                time = time * 1000;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        if (text.matches("^\\d{10,13}$")) {
            long time = Long.parseLong(text);
            if (text.length() == 10) {
                time = time * 1000;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        }
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // try next parser
        }
        try {
            return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // try next parser
        }
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // try next parser
            }
        }
        return null;
    }

    /**
     * @description 执行mapRowToEntity方法，完成远端主数据相关业务处理。
     * @params row 单条数据行对象
     * @params entityClass 实体类型Class对象
     *
      * @return T 泛型对象结果，类型由调用方指定。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private <T> T mapRowToEntity(Map<String, Object> row, Class<T> entityClass) {
        Map<String, Object> normalized = normalizeRowKeys(row);
        T entity = ReflectUtil.newInstanceIfPossible(entityClass);
        BeanUtil.fillBeanWithMap(normalized, entity, true);
        return entity;
    }

    /**
     * @description 执行mergeDepartment方法，完成远端主数据相关业务处理。
     * @params target 目标实体对象
     * @params source 源实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void mergeDepartment(HdlOrganizationDepartment target, HdlOrganizationDepartment source) {
        String createBy = target.getCreateBy();
        String createId = target.getCreateId();
        LocalDateTime createTime = target.getCreateTime();

        BeanUtil.copyProperties(source, target);
        target.setPkDept(source.getPkDept());
        target.setEnableState(normalizeEnableState(source.getEnableState()));
        target.setCreateBy(createBy);
        target.setCreateId(createId);
        target.setCreateTime(createTime);
    }

    /**
     * @description 执行mergePersonBasic方法，完成远端主数据相关业务处理。
     * @params target 目标实体对象
     * @params source 源实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void mergePersonBasic(HdlPersonBasicInfo target, HdlPersonBasicInfo source) {
        String createBy = target.getCreateBy();
        String createId = target.getCreateId();
        LocalDateTime createTime = target.getCreateTime();

        BeanUtil.copyProperties(source, target);
        target.setPkPsndoc(source.getPkPsndoc());
        target.setEnableState(normalizeEnableState(source.getEnableState()));
        target.setCreateBy(createBy);
        target.setCreateId(createId);
        target.setCreateTime(createTime);
    }

    /**
     * @description 执行mergePersonJob方法，完成远端主数据相关业务处理。
     * @params target 目标实体对象
     * @params source 源实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void mergePersonJob(HdlPersonJobInfo target, HdlPersonJobInfo source) {
        String createBy = target.getCreateBy();
        String createId = target.getCreateId();
        LocalDateTime createTime = target.getCreateTime();

        BeanUtil.copyProperties(source, target);
        target.setPkPsnjob(source.getPkPsnjob());
        if (StrUtil.isBlank(target.getEndFlag())) {
            target.setEndFlag(END_FLAG_ACTIVE);
        }
        target.setCreateBy(createBy);
        target.setCreateId(createId);
        target.setCreateTime(createTime);
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params entity 待持久化的实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void applySyncCreateAudit(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        ReflectUtil.setFieldValue(entity, "createBy", SYNC_OPERATOR);
        ReflectUtil.setFieldValue(entity, "createId", SYNC_OPERATOR_ID);
        ReflectUtil.setFieldValue(entity, "createTime", now);
        ReflectUtil.setFieldValue(entity, "updateBy", SYNC_OPERATOR);
        ReflectUtil.setFieldValue(entity, "updateId", SYNC_OPERATOR_ID);
        ReflectUtil.setFieldValue(entity, "updateTime", now);
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params entity 待持久化的实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void applySyncUpdateAudit(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        ReflectUtil.setFieldValue(entity, "updateBy", SYNC_OPERATOR);
        ReflectUtil.setFieldValue(entity, "updateId", SYNC_OPERATOR_ID);
        ReflectUtil.setFieldValue(entity, "updateTime", now);

        Object createBy = ReflectUtil.getFieldValue(entity, "createBy");
        Object createId = ReflectUtil.getFieldValue(entity, "createId");
        Object createTime = ReflectUtil.getFieldValue(entity, "createTime");
        if (createBy == null || StrUtil.isBlank(createBy.toString())) {
            ReflectUtil.setFieldValue(entity, "createBy", SYNC_OPERATOR);
        }
        if (createId == null || StrUtil.isBlank(createId.toString())) {
            ReflectUtil.setFieldValue(entity, "createId", SYNC_OPERATOR_ID);
        }
        if (createTime == null) {
            ReflectUtil.setFieldValue(entity, "createTime", now);
        }
    }

    /**
     * @description 判断远端主数据业务状态是否满足预设条件。
     * @params createId 创建人ID
     * @params updateId 更新人ID
     *
      * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private boolean isRemoteManaged(String createId, String updateId) {
        return StrUtil.equalsAnyIgnoreCase(createId, SYNC_OPERATOR_ID, SYNC_OPERATOR)
            || StrUtil.equalsAnyIgnoreCase(updateId, SYNC_OPERATOR_ID, SYNC_OPERATOR);
    }

    /**
     * @description 结束备份批次并落库执行结果后返回统计。
     * @params stats 统计结果对象（同步或备份执行统计）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> finishAndPersist(SyncStats stats) {
        Map<String, Object> result = stats.finishAndBuild();
        Path syncAuditFile = writeSyncAuditFile(stats);
        result.put("syncAuditLogFile", syncAuditFile.toString());
        try {
            mainDataSyncBatchService.saveBatch(buildSyncBatchEntity(stats));
            result.put("persisted", true);
        } catch (Exception e) {
            logger.error("同步批次落库失败，批次号={}", stats.batchNo, e);
            result.put("persisted", false);
            result.put("persistError", e.getMessage());
        }
        return result;
    }

    /**
     * @description 采集字段映射审计信息，用于同步完成后的结构化日志输出。
     * @params stats 同步统计对象（包含成功标识、统计数据与错误信息）
     * @params remoteRows 远端拉取结果行集合
     *
     * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/22
     */
    private void captureFieldMappingAudit(SyncStats stats, List<Map<String, Object>> remoteRows) {
        if (stats == null) {
            return;
        }
        Map<String, String> reverseMapping = getCachedReverseMapping(stats.dataType);
        stats.mappingConfigCount = reverseMapping.size();
        stats.mappingConfiguredSample = reverseMapping.entrySet().stream()
            .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getKey(), b.getKey()))
            .limit(20)
            .map(entry -> entry.getKey() + "->" + entry.getValue())
            .collect(Collectors.joining(", "));

        if (remoteRows == null || remoteRows.isEmpty()) {
            stats.observedRemoteFieldCount = 0;
            stats.mappedRemoteFieldCount = 0;
            stats.unmappedRemoteFieldCount = 0;
            stats.mappedObservedSample = "";
            stats.unmappedRemoteFieldSample = "";
            return;
        }

        Set<String> observedRemoteFields = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> mappedRemotePairs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> unmappedRemoteFields = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Map<String, Object> row : remoteRows) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            for (String key : row.keySet()) {
                if (!looksLikeRemoteField(key, reverseMapping)) {
                    continue;
                }
                observedRemoteFields.add(key);
                String mappedLocalField = findMappedLocalField(reverseMapping, key);
                if (StrUtil.isBlank(mappedLocalField)) {
                    unmappedRemoteFields.add(key);
                } else {
                    mappedRemotePairs.add(key + "->" + mappedLocalField);
                }
            }
        }

        stats.observedRemoteFieldCount = observedRemoteFields.size();
        stats.mappedRemoteFieldCount = mappedRemotePairs.size();
        stats.unmappedRemoteFieldCount = unmappedRemoteFields.size();
        stats.mappedObservedSample = mappedRemotePairs.stream().limit(20).collect(Collectors.joining(", "));
        stats.unmappedRemoteFieldSample = unmappedRemoteFields.stream().limit(20).collect(Collectors.joining(", "));
    }

    /**
     * @description 判断字段名是否符合远端字段命名特征，便于过滤本地派生键名。
     * @params key 字段名
     * @params reverseMapping 远端到本地字段映射
     *
     * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
     * @author DavidLee233
     * @date 2026/3/22
     */
    private boolean looksLikeRemoteField(String key, Map<String, String> reverseMapping) {
        if (StrUtil.isBlank(key)) {
            return false;
        }
        if (findMappedLocalField(reverseMapping, key) != null) {
            return true;
        }
        return key.matches("^[A-Z0-9_]+$");
    }

    /**
     * @description 查找远端字段对应的本地字段名称，支持忽略大小写匹配。
     * @params reverseMapping 远端到本地字段映射
     * @params remoteField 远端字段名
     *
     * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/22
     */
    private String findMappedLocalField(Map<String, String> reverseMapping, String remoteField) {
        if (reverseMapping == null || reverseMapping.isEmpty() || StrUtil.isBlank(remoteField)) {
            return null;
        }
        String direct = reverseMapping.get(remoteField);
        if (StrUtil.isNotBlank(direct)) {
            return direct;
        }
        for (Map.Entry<String, String> entry : reverseMapping.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(remoteField)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Path writeSyncAuditFile(SyncStats stats) {
        Path logPath = resolveSyncAuditFilePath(stats);
        String mappingStatus = resolveMappingStatus(stats);
        List<String> lines = new ArrayList<>();
        lines.add("同步时间=" + toLocalDateTime(stats.endAt));
        lines.add("批次号=" + stats.batchNo);
        lines.add("是否同步成功=" + (stats.success ? "是" : "否"));
        lines.add("数据类型=" + stats.dataType + "(" + stats.dataTypeName + ")");
        lines.add("触发方式=" + stats.triggerMode);
        lines.add("同步模式=" + stats.syncMode);
        lines.add("映射状态=" + mappingStatus);
        lines.add("远端字段-本地字段映射样例=" + sanitizeAuditValue(stats.mappedObservedSample));
        lines.add("未映射远端字段样例=" + sanitizeAuditValue(stats.unmappedRemoteFieldSample));
        lines.add("映射配置样例=" + sanitizeAuditValue(stats.mappingConfiguredSample));
        lines.add("映射配置数=" + stats.mappingConfigCount);
        lines.add("观测远端字段数=" + stats.observedRemoteFieldCount);
        lines.add("已映射字段数=" + stats.mappedRemoteFieldCount);
        lines.add("未映射字段数=" + stats.unmappedRemoteFieldCount);
        lines.add("拉取记录数=" + stats.fetched);
        lines.add("新增记录数=" + stats.inserted);
        lines.add("更新记录数=" + stats.updated);
        lines.add("失效记录数=" + stats.invalidated);
        lines.add("失败记录数=" + stats.failed);
        lines.add("失败原因=" + (stats.success ? "-" : sanitizeAuditValue(stats.message)));
        try {
            Path parent = logPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(logPath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            logger.error("写入同步审计文件失败，文件路径={}", logPath, e);
        }
        return logPath;
    }

    private Path resolveSyncAuditFilePath(SyncStats stats) {
        long syncMillis = (stats != null && stats.endAt > 0) ? stats.endAt : System.currentTimeMillis();
        LocalDateTime syncTime = toLocalDateTime(syncMillis);
        Path dirPath = Paths.get(StrUtil.blankToDefault(syncAuditLogDir, "logs"));
        if (!dirPath.isAbsolute()) {
            dirPath = Paths.get(System.getProperty("user.dir")).resolve(dirPath);
        }
        Path minuteFile = dirPath.resolve(syncTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + "-sync.log").normalize();
        if (!Files.exists(minuteFile)) {
            return minuteFile;
        }
        Path secondFile = dirPath.resolve(syncTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-sync.log").normalize();
        if (!Files.exists(secondFile)) {
            return secondFile;
        }
        return dirPath.resolve(syncTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "-sync.log").normalize();
    }

    private String resolveMappingStatus(SyncStats stats) {
        if (stats == null || stats.observedRemoteFieldCount <= 0) {
            return "未采集到远端字段";
        }
        return stats.unmappedRemoteFieldCount > 0 ? "存在未映射字段" : "远端字段已映射";
    }

    private String sanitizeAuditValue(String value) {
        String text = StrUtil.blankToDefault(value, "-");
        return text.replace("\r", " ").replace("\n", " ").replace("\t", " ");
    }

    /**
     * @description 输出主数据同步结构化日志，记录同步时间、执行结果与映射状态。
     * @params stats 同步统计对象（包含成功标识、统计数据与错误信息）
     *
     * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/22
     */
    private void logSyncAudit(SyncStats stats) {
        if (stats == null) {
            return;
        }
        LocalDateTime syncTime = toLocalDateTime(stats.endAt);
        String mappingStatus = stats.observedRemoteFieldCount <= 0
            ? "未采集到远端字段"
            : (stats.unmappedRemoteFieldCount > 0 ? "存在未映射字段" : "远端字段已映射");
        logger.info(
            "[主数据同步映射审计] 同步时间={}，批次号={}，数据类型={}({})，触发方式={}，同步模式={}，是否成功={}，映射状态={}，映射配置数={}，观察远端字段数={}，已映射字段数={}，未映射字段数={}，映射配置样例=[{}]，映射命中样例=[{}]，未映射字段样例=[{}]，失败原因={}",
            syncTime,
            stats.batchNo,
            stats.dataType,
            stats.dataTypeName,
            stats.triggerMode,
            stats.syncMode,
            stats.success ? "是" : "否",
            mappingStatus,
            stats.mappingConfigCount,
            stats.observedRemoteFieldCount,
            stats.mappedRemoteFieldCount,
            stats.unmappedRemoteFieldCount,
            StrUtil.blankToDefault(stats.mappingConfiguredSample, "-"),
            StrUtil.blankToDefault(stats.mappedObservedSample, "-"),
            StrUtil.blankToDefault(stats.unmappedRemoteFieldSample, "-"),
            stats.success ? "-" : StrUtil.blankToDefault(stats.message, "-")
        );
        if (stats.success) {
            logger.info(
                "[主数据同步日志] 同步时间={}，批次号={}，数据类型={}({})，触发方式={}，同步模式={}，是否成功={}，拉取={}，新增={}，更新={}，失效={}，失败={}，映射配置数={}，观测远端字段数={}，已映射字段数={}，未映射字段数={}，映射配置样例=[{}]，观测映射样例=[{}]，未映射字段样例=[{}]",
                syncTime,
                stats.batchNo,
                stats.dataType,
                stats.dataTypeName,
                stats.triggerMode,
                stats.syncMode,
                /*
                "是",
                */
                "是",
                stats.fetched,
                stats.inserted,
                stats.updated,
                stats.invalidated,
                stats.failed,
                stats.mappingConfigCount,
                stats.observedRemoteFieldCount,
                stats.mappedRemoteFieldCount,
                stats.unmappedRemoteFieldCount,
                stats.mappingConfiguredSample,
                stats.mappedObservedSample,
                stats.unmappedRemoteFieldSample
            );
            return;
        }
        logger.warn(
            "[主数据同步日志] 同步时间={}，批次号={}，数据类型={}({})，触发方式={}，同步模式={}，是否成功={}，失败原因={}，拉取={}，新增={}，更新={}，失效={}，失败={}，映射配置数={}，观测远端字段数={}，已映射字段数={}，未映射字段数={}，映射配置样例=[{}]，观测映射样例=[{}]，未映射字段样例=[{}]",
            syncTime,
            stats.batchNo,
            stats.dataType,
            stats.dataTypeName,
            stats.triggerMode,
            stats.syncMode,
            /*
            "否",
            */
            "否",
            stats.message,
            stats.fetched,
            stats.inserted,
            stats.updated,
            stats.invalidated,
            stats.failed,
            stats.mappingConfigCount,
            stats.observedRemoteFieldCount,
            stats.mappedRemoteFieldCount,
            stats.unmappedRemoteFieldCount,
            stats.mappingConfiguredSample,
            stats.mappedObservedSample,
            stats.unmappedRemoteFieldSample
        );
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params stats 统计结果对象（同步或备份执行统计）
     *
      * @return HdlMainDataSyncBatch 主数据同步批次相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private HdlMainDataSyncBatch buildSyncBatchEntity(SyncStats stats) {
        HdlMainDataSyncBatch batch = new HdlMainDataSyncBatch();
        batch.setBatchNo(stats.batchNo);
        batch.setDataType(stats.dataType);
        batch.setTriggerMode(stats.triggerMode);
        batch.setSyncMode(stats.syncMode);
        batch.setSuccess(stats.success ? "1" : "0");
        batch.setMessage(stats.message);
        batch.setStartTime(toLocalDateTime(stats.startAt));
        batch.setEndTime(toLocalDateTime(stats.endAt));
        batch.setDurationMs(stats.endAt - stats.startAt);
        batch.setFetchedCount(stats.fetched);
        batch.setInsertedCount(stats.inserted);
        batch.setUpdatedCount(stats.updated);
        batch.setInvalidatedCount(stats.invalidated);
        batch.setSkippedCount(stats.skipped);
        batch.setConflictCount(stats.conflict);
        batch.setFailedCount(stats.failed);
        batch.setOperatorName(stats.operatorName);
        batch.setOperatorId(stats.operatorId);
        batch.setCreateBy(stats.operatorName);
        batch.setCreateTime(LocalDateTime.now());
        return batch;
    }

    /**
     * @description 执行toLocalDateTime方法，完成远端主数据相关业务处理。
     * @params epochMillis 毫秒级时间戳（用于转换为本地日期时间）
     *
      * @return LocalDateTime 本地日期时间结果，用于同步水位或时间过滤。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

    /**
     * @description 解析当前操作人名称，未登录时回退默认值。
     * @params 无
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveOperatorName() {
        try {
            LoginUser user = LoginHelper.getLoginUser();
            if (user != null && StrUtil.isNotBlank(user.getUsername())) {
                return user.getUsername();
            }
        } catch (Exception ignored) {
            // no login context, use default sync operator
        }
        return SYNC_OPERATOR;
    }

    /**
     * @description 解析当前操作人标识，未登录时回退默认值。
     * @params 无
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveOperatorId() {
        try {
            LoginUser user = LoginHelper.getLoginUser();
            if (user != null && StrUtil.isNotBlank(user.getLoginName())) {
                return user.getLoginName();
            }
        } catch (Exception ignored) {
            // no login context, use default sync operator id
        }
        return SYNC_OPERATOR_ID;
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params enableState 启用状态标记
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String normalizeEnableState(String enableState) {
        return StrUtil.isBlank(enableState) ? ENABLE_ACTIVE : enableState;
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String normalizeSyncMode(String syncMode) {
        if (StrUtil.isBlank(syncMode)) {
            return SYNC_MODE_FULL;
        }
        String value = syncMode.trim().toLowerCase(Locale.ROOT);
        if (SYNC_MODE_INCREMENTAL.equals(value) || SYNC_MODE_AUTO.equals(value)) {
            return value;
        }
        return SYNC_MODE_FULL;
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String normalizeTriggerMode(String triggerMode) {
        if (StrUtil.isBlank(triggerMode)) {
            return TRIGGER_MODE_MANUAL;
        }
        String value = triggerMode.trim().toLowerCase(Locale.ROOT);
        if (TRIGGER_MODE_HANDLER.equals(value) || "job".equals(value)) {
            return TRIGGER_MODE_HANDLER;
        }
        return TRIGGER_MODE_MANUAL;
    }

    /**
     * @description 执行远端主数据同步流程并汇总同步统计结果。
     * @params 无
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String validateSyncInterval() {
        HdlMainDataBackupRecord latestBackup = backupRecordService.queryLatestSuccess();
        if (latestBackup == null || latestBackup.getEndTime() == null) {
            return null;
        }
        long minutes = Duration.between(latestBackup.getEndTime(), LocalDateTime.now()).toMinutes();
        if (minutes >= syncBackupMinIntervalMinutes) {
            return null;
        }
        return String.format(
            "最近一次备份完成时间为 %s，同步任务需至少间隔 %d 分钟后再执行",
            latestBackup.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            syncBackupMinIntervalMinutes
        );
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params value 待转换字段值
     * @params defaultValue 转换失败时使用的默认值
     *
      * @return long 数值型业务处理结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private long parseLong(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    /**
     * @description 导出远端主数据数据并输出为文件流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params queryFunction 远端查询执行函数
     * @params voClass 导出VO类型Class对象
     * @params sheetName Excel工作表名称
     *
      * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @SuppressWarnings("unchecked")
    private <T> byte[] exportRemoteData(Map<String, Object> params,
                                        Function<Map<String, Object>, Map<String, Object>> queryFunction,
                                        Class<T> voClass,
                                        String sheetName) {
        try {
            Map<String, Object> queryResult = queryFunction.apply(params);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) queryResult.get("rows");
            List<T> exportList = convertRowsToVoList(rows, voClass);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ExcelUtil.exportExcel(exportList, sheetName, voClass, os);
            return os.toByteArray();
        } catch (Exception e) {
            logger.error("导出远端{}异常", sheetName, e);
            throw new RuntimeException("导出远端" + sheetName + "异常: " + e.getMessage(), e);
        }
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params rows 导出或转换后的数据行集合
     * @params voClass 导出VO类型Class对象
     *
      * @return List<T> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private <T> List<T> convertRowsToVoList(List<Map<String, Object>> rows, Class<T> voClass) {
        List<T> result = new ArrayList<>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Map<String, Object> normalized = normalizeRowKeys(row);
            T vo = ReflectUtil.newInstanceIfPossible(voClass);
            BeanUtil.fillBeanWithMap(normalized, vo, true);
            result.add(vo);
        }
        return result;
    }

    /**
     * @description 解析并转换远端主数据相关输入，生成可直接使用的数据结构。
     * @params row 单条数据行对象
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> normalizeRowKeys(Map<String, Object> row) {
        Map<String, Object> normalized = new HashMap<>();
        if (row == null || row.isEmpty()) {
            return normalized;
        }
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StrUtil.isBlank(key)) {
                continue;
            }
            normalized.put(key, value);
            normalized.put(StrUtil.lowerFirst(key), value);
            normalized.put(key.toLowerCase(), value);
            String lowerKey = key.toLowerCase();
            normalized.put(StrUtil.toCamelCase(lowerKey), value);
            normalized.put(StrUtil.toCamelCase(key), value);
        }
        return normalized;
    }

    private static class SyncStats {
        private final String batchNo;
        private final String dataType;
        private final String dataTypeName;
        private final String triggerMode;
        private String syncMode;
        private final String operatorName;
        private final String operatorId;
        private final long startAt;
        private long endAt;
        private boolean success = true;
        private String message = "success";
        private int fetched;
        private int inserted;
        private int updated;
        private int invalidated;
        private int skipped;
        private int conflict;
        private int failed;
        private int mappingConfigCount;
        private int observedRemoteFieldCount;
        private int mappedRemoteFieldCount;
        private int unmappedRemoteFieldCount;
        private String mappingConfiguredSample = "";
        private String mappedObservedSample = "";
        private String unmappedRemoteFieldSample = "";

        private SyncStats(String dataType,
                          String dataTypeName,
                          String triggerMode,
                          String syncMode,
                          String operatorName,
                          String operatorId) {
            this.dataType = dataType;
            this.dataTypeName = dataTypeName;
            this.triggerMode = triggerMode;
            this.syncMode = syncMode;
            this.operatorName = operatorName;
            this.operatorId = operatorId;
            this.startAt = System.currentTimeMillis();
            this.batchNo = dataType + "_" + this.startAt;
        }

        static SyncStats start(String dataType,
                               String dataTypeName,
                               String triggerMode,
                               String syncMode,
                               String operatorName,
                               String operatorId) {
            return new SyncStats(dataType, dataTypeName, triggerMode, syncMode, operatorName, operatorId);
        }

        void fail(String msg) {
            this.success = false;
            this.message = msg;
        }

        void note(String msg) {
            if (StrUtil.isBlank(msg)) {
                return;
            }
            if ("success".equals(this.message)) {
                this.message = msg;
                return;
            }
            if (!this.message.contains(msg)) {
                this.message = this.message + " | " + msg;
            }
        }

        Map<String, Object> finishAndBuild() {
            this.endAt = System.currentTimeMillis();
            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("type", dataType);
            result.put("typeName", dataTypeName);
            result.put("triggerMode", triggerMode);
            result.put("syncMode", syncMode);
            result.put("operatorName", operatorName);
            result.put("operatorId", operatorId);
            result.put("success", success);
            result.put("message", message);
            result.put("startAt", startAt);
            result.put("endAt", endAt);
            result.put("durationMs", endAt - startAt);
            result.put("fetched", fetched);
            result.put("inserted", inserted);
            result.put("updated", updated);
            result.put("invalidated", invalidated);
            result.put("skipped", skipped);
            result.put("conflict", conflict);
            result.put("failed", failed);
            result.put("mappingConfigCount", mappingConfigCount);
            result.put("observedRemoteFieldCount", observedRemoteFieldCount);
            result.put("mappedRemoteFieldCount", mappedRemoteFieldCount);
            result.put("unmappedRemoteFieldCount", unmappedRemoteFieldCount);
            result.put("mappingConfiguredSample", mappingConfiguredSample);
            result.put("mappedObservedSample", mappedObservedSample);
            result.put("unmappedRemoteFieldSample", unmappedRemoteFieldSample);
            String mappingStatus = observedRemoteFieldCount <= 0
                ? "未采集到远端字段"
                : (unmappedRemoteFieldCount > 0 ? "存在未映射字段" : "远端字段已映射");
            result.put("mappingStatus", mappingStatus);
            return result;
        }
    }
    /**
     * @description 将对象安全转换为整数，转换失败返回默认值。
     * @params value 待转换字段值
     * @params defaultValue 转换失败时使用的默认值
     *
      * @return int 数值型业务处理结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private int parseInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildOrgFilterMatchTypes() {
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("name", "contains");
        rules.put("code", "contains");
        rules.put("shortName", "contains");
        rules.put("internetName", "contains");
        rules.put("sign", "contains");
        rules.put("enableState", "equals");
        return rules;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildPersonBasicFilterMatchTypes() {
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("name", "contains");
        rules.put("code", "contains");
        rules.put("mobile", "contains");
        return rules;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildPersonJobFilterMatchTypes() {
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("name", "contains");
        rules.put("code", "contains");
        rules.put("keyNumber", "contains");
        return rules;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildOrgFilterDefaultRemoteFields() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("name", "NAME");
        fallback.put("code", "CODE");
        fallback.put("shortName", "SHORTNAME");
        fallback.put("internetName", "INTI");
        fallback.put("sign", "SIGN");
        fallback.put("enableState", "ENAE");
        return fallback;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildPersonBasicFilterDefaultRemoteFields() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("name", "NAME");
        fallback.put("code", "CODE");
        fallback.put("mobile", "MOBILE");
        return fallback;
    }

    /**
     * @description 构建远端主数据处理所需的中间对象或条件。
     * @params 无
     *
      * @return Map<String, String> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, String> buildPersonJobFilterDefaultRemoteFields() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("name", "NAME");
        fallback.put("code", "CODE");
        fallback.put("keyNumber", "KEY");
        return fallback;
    }
}
