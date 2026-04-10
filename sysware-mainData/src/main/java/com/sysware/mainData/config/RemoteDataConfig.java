package com.sysware.mainData.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "remote.data")
public class RemoteDataConfig {

    /**
     * Base URL of remote main-data platform, e.g. http://139.10.22.52
     */
    private String apiUrl = "http://139.10.22.52";

    /**
     * Relative login path.
     */
    private String loginPath = "/api/users/login";

    private String username = "es_sjpt";
    private String password = "xwzx1107??";

    private long tokenExpireTime = 600000;
    private long tokenRefreshTime = 300000;
    private Map<String, String> fieldMappings = new HashMap<>();

    /**
     * Legacy typo key for compatibility.
     */
    private String orgnizationDepartmentPath = "/api/mdm/NPIC_HR_DEPARTMENT_ZH/query";

    /**
     * Correct key for organization department path.
     */
    private String organizationDepartmentPath;

    private String personBasicPath = "/api/mdm/NPIC_HR_PERSONINFO/query";
    private String personJobPath = "/api/mdm/NPIC_HR_PERSONJOB/query";

    /**
     * Use correct spelling first, fallback to legacy typo key.
     */
    public String getOrganizationDepartmentPath() {
        if (hasText(organizationDepartmentPath)) {
            return organizationDepartmentPath;
        }
        return orgnizationDepartmentPath;
    }

    /**
     * Safely concatenate base URL and relative path.
     */
    public String buildRemoteUrl(String relativePath) {
        String base = trimTrailingSlash(apiUrl);
        String path = normalizePath(relativePath);
        return base + path;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimTrailingSlash(String value) {
        if (!hasText(value)) {
            return "";
        }
        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String normalizePath(String value) {
        if (!hasText(value)) {
            return "";
        }
        String result = value.trim();
        if (!result.startsWith("/")) {
            result = "/" + result;
        }
        return result;
    }
}