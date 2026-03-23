package com.sysware.mainData.domain;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * @project npic
 * @description DownstreamPullRequest领域实体，描述下游主数据接口核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class DownstreamPullRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 200;
    private Boolean includeInvalid = false;
    private LocalDateTime updatedAfter;

    private String name;

    private String code;

    private String shortName;

    private String sign;

    private String mobile;

    private String keyNumber;

    private String idNumber;
}