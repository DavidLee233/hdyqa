package com.sysware.mainData.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sysware.mainData.config.RemoteDataConfig;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.service.IHdlMainDataMappingService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RemoteDataServiceImpl implements IRemoteDataService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDataServiceImpl.class);

    @Autowired
    private RemoteDataConfig remoteDataConfig;

    @Autowired
    private IRemoteTokenService remoteTokenService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IHdlMainDataMappingService mainDataMappingService;

    // 缓存字段映射，避免每次查询都访问数据库
    private Map<String, String> cachedFieldMapping;
    private Map<String, String> cachedReverseMapping;
    private long lastLoadTime = 0;
    private static final long CACHE_EXPIRE_TIME = 24 * 60 * 60 * 1000; // 1天缓存

    /**
     * 查询远程组织部门列表（根据新接口规范）
     */
    @Override
    public Map<String, Object> queryRemoteDepartments(Map<String, Object> params) {
        try {
            // 获取有效的Token
            String token = remoteTokenService.getValidToken();
            if (StringUtils.isEmpty(token)) {
                throw new RuntimeException("无法获取有效的API Token");
            }

            // 构建请求URL
            String url = remoteDataConfig.getApiUrl() + remoteDataConfig.getOrgnizationDepartmentPath();

            // 构建查询参数（根据新接口规范）
            Map<String, Object> requestBody = buildQueryRequestBody(params);

            // 添加分页参数到URL
            int pageNum = params.containsKey("pageNum") ? (int) params.get("pageNum") : 0;
            int pageSize = params.containsKey("pageSize") ? (int) params.get("pageSize") : 10;
            url += "?pageNum=" + pageNum + "&pageSize=" + pageSize;

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.info("发送远程部门查询请求，URL: {}, 参数: {}", url, requestBody);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("查询远程组织部门数据成功");

                // 转换响应格式以匹配前端期望的格式
                return convertResponseFormat(response.getBody(), pageNum, pageSize);
            } else {
                logger.error("查询远程组织部门数据失败，响应状态: {}", response.getStatusCode());
                throw new RuntimeException("查询远程组织部门数据失败，状态码: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("Token无效或已过期，尝试刷新Token后重试");
            // Token无效，刷新后重试一次
            remoteTokenService.refreshToken();
            return queryRemoteDepartments(params);

        } catch (HttpClientErrorException e) {
            logger.error("查询远程组织部门数据HTTP错误: {}, 响应内容: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("查询远程组织部门数据HTTP错误: " + e.getStatusCode(), e);

        } catch (ResourceAccessException e) {
            logger.error("远程API连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("远程API连接失败: " + e.getMessage(), e);

        } catch (Exception e) {
            logger.error("查询远程组织部门数据异常", e);
            throw new RuntimeException("查询远程组织部门数据异常: " + e.getMessage(), e);
        }
    }

    /**
     * 构建查询请求体（根据新接口规范）
     */
    private Map<String, Object> buildQueryRequestBody(Map<String, Object> params) {
        Map<String, Object> requestBody = new HashMap<>();

        // 构建查询条件
        List<Map<String, Object>> args = buildQueryDeptArgs(params);
        if (!args.isEmpty()) {
            requestBody.put("args", args);

            // 多条件连接逻辑，默认使用AND
            requestBody.put("and", true);
        }

        // 构建排序条件
        if (params.containsKey("sortField") && params.containsKey("sortOrder")) {
            List<Map<String, Object>> sorts = new ArrayList<>();
            Map<String, Object> sort = new HashMap<>();
            sort.put("name", params.get("sortField"));
            sort.put("asc", "asc".equals(params.get("sortOrder")));
            sorts.add(sort);
            requestBody.put("sorts", sorts);
        }

        // 是否包含系统字段
        requestBody.put("withSysColumn", false);

        return requestBody;
    }

    /**
     * 构建查询条件
     */
    private List<Map<String, Object>> buildQueryDeptArgs(Map<String, Object> params) {
        List<Map<String, Object>> args = new ArrayList<>();

        // 根据前端传递的查询参数构建查询条件
        // 前端传递的是本地字段名，需要映射到远程字段名
        Map<String, String> fieldMapping = getCachedFieldMapping("0");

        // 部门名称查询
        if (params.containsKey("name") && StringUtils.isNotEmpty((String) params.get("name"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("name", "NAME"),
                    params.get("name"), "contains"));
        }

        // 部门编码查询
        if (params.containsKey("code") && StringUtils.isNotEmpty((String) params.get("code"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("code", "CODE"),
                    params.get("code"), "contains"));
        }

        // 部门简称查询
        if (params.containsKey("shortName") && StringUtils.isNotEmpty((String) params.get("shortName"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("shortName", "SHORTNAME"),
                    params.get("shortName"), "contains"));
        }

        // 互联网掩护名查询
        if (params.containsKey("internetName") && StringUtils.isNotEmpty((String) params.get("internetName"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("internetName", "INTI"),
                    params.get("internetName"), "contains"));
        }

        // 组织部门标识查询
        if (params.containsKey("sign") && StringUtils.isNotEmpty((String) params.get("sign"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("sign", "SIGN"),
                    params.get("sign"), "contains"));
        }

        // 启用状态查询
        if (params.containsKey("enableState") && StringUtils.isNotEmpty((String) params.get("enableState"))) {
            args.add(buildQueryArg(fieldMapping.getOrDefault("enableState", "ENAE"),
                    params.get("enableState"), "equals"));
        }

        return args;
    }

    /**
     * 构建单个查询条件
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
     * 转换响应格式以匹配前端期望的格式
     */
    private Map<String, Object> convertResponseFormat(Map<String, Object> response, int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 解析远程API响应
            // 根据实际的响应结构进行调整
            List<Map<String, Object>> data = null;
            long total = 0L;

            // 判断响应结构（不同接口可能返回不同的结构）
            if (response.containsKey("data")) {
                data = (List<Map<String, Object>>) response.get("data");
                total = response.get("total") != null ?
                        Long.parseLong(response.get("total").toString()) :
                        (data != null ? (long) data.size() : 0L);
            } else if (response.containsKey("rows")) {
                data = (List<Map<String, Object>>) response.get("rows");
                total = response.get("total") != null ?
                        Long.parseLong(response.get("total").toString()) :
                        (data != null ? (long) data.size() : 0L);
            } else {
                // 如果响应本身就是数组
                if (response instanceof List) {
                    data = (List<Map<String, Object>>) response;
                    total = (long) data.size();
                } else {
                    data = new ArrayList<>();
                    total = 0L;
                }
            }

            // 将远程字段名映射回本地字段名
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, String> reverseMapping = getCachedReverseMapping("0");

            if (data != null) {
                for (Map<String, Object> remoteRow : data) {
                    Map<String, Object> localRow = new HashMap<>();

                    // 遍历反向映射，将远程字段值复制到本地字段
                    for (Map.Entry<String, String> entry : reverseMapping.entrySet()) {
                        String remoteField = entry.getKey();
                        String localField = entry.getValue();

                        if (remoteRow.containsKey(remoteField) && remoteRow.get(remoteField) != null) {
                            localRow.put(localField, remoteRow.get(remoteField));
                        }
                    }
                    rows.add(localRow);
                }
            }

            result.put("rows", rows);
            result.put("total", total);

        } catch (Exception e) {
            logger.error("转换响应格式失败", e);
            result.put("rows", new ArrayList<>());
            result.put("total", 0L);
        }

        return result;
    }

    /**
     * 获取字段映射关系（带缓存）
     */
    private Map<String, String> getCachedFieldMapping(String type) {
        long currentTime = System.currentTimeMillis();
        // 缓存不存在或已过期，重新加载
        if (cachedFieldMapping == null || (currentTime - lastLoadTime) > CACHE_EXPIRE_TIME) {
            reloadFieldMapping(type);
        }
        return cachedFieldMapping;
    }

    /**
     * 获取反向字段映射
     */
    private Map<String, String> getCachedReverseMapping(String type) {
        if (cachedReverseMapping == null) {
            reloadFieldMapping(type);
        }
        return cachedReverseMapping;
    }

    /**
     * 重新加载字段映射
     */
    private synchronized void reloadFieldMapping(String type) {
        // 从数据库查询字段类型为"0"（组织部门）的映射配置
        List<HdlMainDataMappingVo> mappingVos = mainDataMappingService.selectMainDataMappingByType(type);
        if (mappingVos != null && !mappingVos.isEmpty()) {
            // 使用Stream API转换为Map
            cachedFieldMapping = mappingVos.stream()
                    .filter(vo -> vo != null
                            && StrUtil.isNotBlank(vo.getSourceField())
                            && StrUtil.isNotBlank(vo.getTargetField()))
                    .collect(Collectors.toMap(
                            HdlMainDataMappingVo::getTargetField,  // key: 本地字段名
                            HdlMainDataMappingVo::getSourceField,  // value: 远程字段名
                            (existing, replacement) -> existing    // 冲突时保留第一个
                    ));

            // 构建反向映射
            cachedReverseMapping = cachedFieldMapping.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getValue,  // key: 远程字段名
                            Map.Entry::getKey,    // value: 本地字段名
                            (existing, replacement) -> existing
                    ));

            lastLoadTime = System.currentTimeMillis();
        }
    }

    /**
     * 导出远程组织部门数据
     * 注意：新文档中没有明确导出接口，这里可能需要根据实际情况调整
     */
    @Override
    public byte[] exportRemoteDepartments(Map<String, Object> params) {
        try {
            // 先查询数据
            Map<String, Object> queryResult = queryRemoteDepartments(params);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) queryResult.get("rows");

            // 这里可以根据需要将数据转换为Excel或其他格式
            // 由于新文档没有明确导出接口，这里返回模拟数据或抛异常

            logger.warn("远程导出接口未明确，返回空数据");
            return new byte[0];

        } catch (Exception e) {
            logger.error("导出远程组织部门数据异常", e);
            throw new RuntimeException("导出远程组织部门数据异常: " + e.getMessage());
        }
    }
}
