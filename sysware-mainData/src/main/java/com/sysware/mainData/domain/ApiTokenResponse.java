package com.sysware.mainData.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @project npic
 * @description ApiTokenResponse领域实体，描述远端令牌响应数据核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class ApiTokenResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("token")
    private String token;

    // 本地记录的时间戳
    private long timestamp;

    /**
     * @description 输出对象关键信息字符串，便于日志追踪与排查。
     * @params 无
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public String toString() {
        return "ApiTokenResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null") + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}