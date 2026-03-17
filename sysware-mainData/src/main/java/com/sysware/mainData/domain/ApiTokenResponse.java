package com.sysware.mainData.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
