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
import com.sysware.mainData.service.IHdlMainDataSyncBatchService;
import com.sysware.mainData.service.IRemoteDataService;
import com.sysware.mainData.service.IRemoteTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 按主数据类型缓存字段映射，避免多类型场景下重复查询数据库。
     */
    private final Map<String, Map<String, String>> cachedFieldMappingByType = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> cachedReverseMappingByType = new ConcurrentHashMap<>();
    private final Map<String, Long> lastLoadTimeByType = new ConcurrentHashMap<>();

    /**
     * 查询远程组织部门列表
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
     * 查询远程员工基本信息列表
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
     * 查询远程员工工作信息列表
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
     * 统一远端查询执行逻辑。
     */
    private Map<String, Object> queryRemoteData(Map<String, Object> params,
                                                 String mainDataType,
                                                 String apiPath,
                                                 Map<String, String> filterMatchTypes,
                                                 Map<String, String> defaultRemoteFields) {
        return queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields, true);
    }

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

            logger.info("发送远端主数据查询请求，type={}, url={}, body={}", mainDataType, url, requestBody);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("查询远端主数据失败，type={}, status={}", mainDataType, response.getStatusCode());
                throw new RuntimeException("查询远端主数据失败，状态码: " + response.getStatusCode());
            }

            return convertResponseFormat(response.getBody(), mainDataType);
        } catch (HttpClientErrorException.Unauthorized e) {
            if (!allowRetryOnUnauthorized) {
                throw new RuntimeException("Token无效且刷新后重试失败", e);
            }
            logger.warn("Token无效或过期，刷新后重试一次。type={}", mainDataType);
            remoteTokenService.refreshToken();
            return queryRemoteData(params, mainDataType, apiPath, filterMatchTypes, defaultRemoteFields, false);
        } catch (HttpClientErrorException e) {
            logger.error("查询远端主数据HTTP错误，type={}, status={}, body={}",
                mainDataType, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("查询远端主数据HTTP错误: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("远端API连接失败，type={}, msg={}", mainDataType, e.getMessage(), e);
            throw new RuntimeException("远端API连接失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("查询远端主数据异常，type={}", mainDataType, e);
            throw new RuntimeException("查询远端主数据异常: " + e.getMessage(), e);
        }
    }

    /**
     * 构建查询请求体。
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
     * 按类型和字段映射生成查询条件。
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
                logger.warn("字段映射缺失，跳过该查询条件。type={}, localField={}", mainDataType, localField);
                continue;
            }
            args.add(buildQueryArg(remoteField, value, matchType));
        }
        return args;
    }

    private Map<String, Object> buildQueryArg(String field, Object value, String matchType) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("name", field);
        arg.put("matchValue", value);
        arg.put("matchType", matchType);
        arg.put("matchOption", "all");
        return arg;
    }

    /**
     * 转换响应格式以匹配前端期望的 rows/total。
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
            logger.error("转换远端响应格式失败，type={}", mainDataType, e);
            result.put("rows", new ArrayList<>());
            result.put("total", 0L);
        }
        return result;
    }

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

    private long extractTotal(Map<String, Object> response, List<Map<String, Object>> rows) {
        Object totalObj = response.get("total");
        if (totalObj != null) {
            try {
                return Long.parseLong(totalObj.toString());
            } catch (NumberFormatException e) {
                logger.warn("远端响应total格式异常，使用rows.size兜底。total={}", totalObj);
            }
        }
        return rows == null ? 0L : rows.size();
    }

    /**
     * 获取字段映射关系（带缓存）
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
     * 获取反向字段映射
     */
    private Map<String, String> getCachedReverseMapping(String type) {
        if (!cachedReverseMappingByType.containsKey(type)) {
            reloadFieldMapping(type);
        }
        return cachedReverseMappingByType.getOrDefault(type, new HashMap<>());
    }

    /**
     * 重新加载字段映射
     */
    private synchronized void reloadFieldMapping(String type) {
        // selectMainDataMappingByType 内部兼容旧值：1/2/3 -> 0/1/2
        List<HdlMainDataMappingVo> mappingVos = mainDataMappingService.selectMainDataMappingByType(type);
        if (mappingVos == null || mappingVos.isEmpty()) {
            cachedFieldMappingByType.put(type, new HashMap<>());
            cachedReverseMappingByType.put(type, new HashMap<>());
            lastLoadTimeByType.put(type, System.currentTimeMillis());
            logger.warn("未找到字段映射配置，type={}", type);
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
     * 导出远端组织部门数据
     */
    @Override
    public byte[] exportRemoteDepartments(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemoteDepartments, HdlOrganizationDepartmentVo.class, "组织部门数据");
    }

    @Override
    public byte[] exportRemotePersonBasicInfos(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemotePersonBasicInfos, HdlPersonBasicInfoVo.class, "员工基本信息数据");
    }

    @Override
    public byte[] exportRemotePersonJobInfos(Map<String, Object> params) {
        return exportRemoteData(params, this::queryRemotePersonJobInfos, HdlPersonJobInfoVo.class, "员工工作信息数据");
    }

    @Override
    public Map<String, Object> forceSyncDepartments() {
        SyncStats stats = SyncStats.start(TYPE_ORG_DEPT, "organizationDepartment", "manual", resolveOperatorName(), resolveOperatorId());
        try {
            List<Map<String, Object>> remoteRows = fetchAllRemoteRows(
                TYPE_ORG_DEPT,
                remoteDataConfig.getOrgnizationDepartmentPath(),
                buildOrgFilterMatchTypes(),
                buildOrgFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();

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
                    logger.error("Force sync organization department row failed, row={}", row, ex);
                }
            }

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
        } catch (Exception e) {
            stats.fail("force sync organization department failed: " + e.getMessage());
            logger.error("Force sync organization department failed", e);
        }
        return finishAndPersist(stats);
    }

    @Override
    public Map<String, Object> forceSyncPersonBasicInfos() {
        SyncStats stats = SyncStats.start(TYPE_PERSON_BASIC, "personBasicInfo", "manual", resolveOperatorName(), resolveOperatorId());
        try {
            List<Map<String, Object>> remoteRows = fetchAllRemoteRows(
                TYPE_PERSON_BASIC,
                remoteDataConfig.getPersonBasicPath(),
                buildPersonBasicFilterMatchTypes(),
                buildPersonBasicFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();

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
                    logger.error("Force sync person basic row failed, row={}", row, ex);
                }
            }

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
        } catch (Exception e) {
            stats.fail("force sync person basic failed: " + e.getMessage());
            logger.error("Force sync person basic failed", e);
        }
        return finishAndPersist(stats);
    }

    @Override
    public Map<String, Object> forceSyncPersonJobInfos() {
        SyncStats stats = SyncStats.start(TYPE_PERSON_JOB, "personJobInfo", "manual", resolveOperatorName(), resolveOperatorId());
        try {
            List<Map<String, Object>> remoteRows = fetchAllRemoteRows(
                TYPE_PERSON_JOB,
                remoteDataConfig.getPersonJobPath(),
                buildPersonJobFilterMatchTypes(),
                buildPersonJobFilterDefaultRemoteFields()
            );
            stats.fetched = remoteRows.size();

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
                    logger.error("Force sync person job row failed, row={}", row, ex);
                }
            }

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
        } catch (Exception e) {
            stats.fail("force sync person job failed: " + e.getMessage());
            logger.error("Force sync person job failed", e);
        }
        return finishAndPersist(stats);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchAllRemoteRows(String mainDataType,
                                                         String apiPath,
                                                         Map<String, String> filterMatchTypes,
                                                         Map<String, String> defaultRemoteFields) {
        List<Map<String, Object>> allRows = new ArrayList<>();
        int pageNum = 0;
        int pageSize = 500;
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
            if (pageNum > 2000) {
                logger.warn("Fetch remote rows exceeded max page threshold, type={}", mainDataType);
                break;
            }
        }
        return allRows;
    }

    private <T> T mapRowToEntity(Map<String, Object> row, Class<T> entityClass) {
        Map<String, Object> normalized = normalizeRowKeys(row);
        T entity = ReflectUtil.newInstanceIfPossible(entityClass);
        BeanUtil.fillBeanWithMap(normalized, entity, true);
        return entity;
    }

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

    private void applySyncCreateAudit(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        ReflectUtil.setFieldValue(entity, "createBy", SYNC_OPERATOR);
        ReflectUtil.setFieldValue(entity, "createId", SYNC_OPERATOR_ID);
        ReflectUtil.setFieldValue(entity, "createTime", now);
        ReflectUtil.setFieldValue(entity, "updateBy", SYNC_OPERATOR);
        ReflectUtil.setFieldValue(entity, "updateId", SYNC_OPERATOR_ID);
        ReflectUtil.setFieldValue(entity, "updateTime", now);
    }

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

    private boolean isRemoteManaged(String createId, String updateId) {
        return StrUtil.equalsAnyIgnoreCase(createId, SYNC_OPERATOR_ID, SYNC_OPERATOR)
            || StrUtil.equalsAnyIgnoreCase(updateId, SYNC_OPERATOR_ID, SYNC_OPERATOR);
    }

    private Map<String, Object> finishAndPersist(SyncStats stats) {
        Map<String, Object> result = stats.finishAndBuild();
        try {
            mainDataSyncBatchService.saveBatch(buildSyncBatchEntity(stats));
            result.put("persisted", true);
        } catch (Exception e) {
            logger.error("Persist sync batch failed, batchNo={}", stats.batchNo, e);
            result.put("persisted", false);
            result.put("persistError", e.getMessage());
        }
        return result;
    }

    private HdlMainDataSyncBatch buildSyncBatchEntity(SyncStats stats) {
        HdlMainDataSyncBatch batch = new HdlMainDataSyncBatch();
        batch.setBatchNo(stats.batchNo);
        batch.setDataType(stats.dataType);
        batch.setTriggerMode(stats.triggerMode);
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

    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

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

    private String normalizeEnableState(String enableState) {
        return StrUtil.isBlank(enableState) ? ENABLE_ACTIVE : enableState;
    }

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

        private SyncStats(String dataType,
                          String dataTypeName,
                          String triggerMode,
                          String operatorName,
                          String operatorId) {
            this.dataType = dataType;
            this.dataTypeName = dataTypeName;
            this.triggerMode = triggerMode;
            this.operatorName = operatorName;
            this.operatorId = operatorId;
            this.startAt = System.currentTimeMillis();
            this.batchNo = dataType + "_" + this.startAt;
        }

        static SyncStats start(String dataType,
                               String dataTypeName,
                               String triggerMode,
                               String operatorName,
                               String operatorId) {
            return new SyncStats(dataType, dataTypeName, triggerMode, operatorName, operatorId);
        }

        void fail(String msg) {
            this.success = false;
            this.message = msg;
        }

        Map<String, Object> finishAndBuild() {
            this.endAt = System.currentTimeMillis();
            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("type", dataType);
            result.put("typeName", dataTypeName);
            result.put("triggerMode", triggerMode);
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
            return result;
        }
    }
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

    private Map<String, String> buildPersonBasicFilterMatchTypes() {
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("name", "contains");
        rules.put("code", "contains");
        rules.put("mobile", "contains");
        return rules;
    }

    private Map<String, String> buildPersonJobFilterMatchTypes() {
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("name", "contains");
        rules.put("code", "contains");
        rules.put("keyNumber", "contains");
        return rules;
    }

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

    private Map<String, String> buildPersonBasicFilterDefaultRemoteFields() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("name", "NAME");
        fallback.put("code", "CODE");
        fallback.put("mobile", "MOBILE");
        return fallback;
    }

    private Map<String, String> buildPersonJobFilterDefaultRemoteFields() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("name", "NAME");
        fallback.put("code", "CODE");
        fallback.put("keyNumber", "KEY");
        return fallback;
    }
}


