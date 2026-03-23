package com.sysware.mainData.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @project npic
 * @description RemoteDataConfig配置类，负责注册远端主数据相关运行组件与参数。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "remote.data")
public class RemoteDataConfig {
    // 远程API配置
    private String apiUrl = "http://139.10.22.52";
    private String username = "es_sjpt";
    private String password = "xwzx1107??";

    // Token相关配置
    private long tokenExpireTime = 600000; // 10分钟，单位毫秒
    private long tokenRefreshTime = 300000; // 5分钟，提前刷新

    // 字段映射配置（前端传过来的字段名需要映射到远程字段名）
    private Map<String, String> fieldMappings = new HashMap<>();

    // 接口查询配置
    private String orgnizationDepartmentPath = "/api/mdm/NPIC_HR_DEPARTMENT_ZH/query";
    private String personBasicPath = "/api/mdm/NPIC_HR_PERSONINFO/query";
    private String personJobPath = "/api/mdm/NPIC_HR_PERSONJOB/query";
}