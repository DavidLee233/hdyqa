package com.sysware.mainData.domain;

import lombok.Data;

/**
 * @project npic
 * @description ApiTokenRequest领域实体，描述远端令牌请求参数核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class ApiTokenRequest {

    private String username;
    private String password;

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
        return "ApiTokenRequest{" +
                "username='" + username + '\'' +
                ", password='" + (password != null ? password : "") + '\'' +
                '}';
    }
}