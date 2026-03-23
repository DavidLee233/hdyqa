package com.sysware.mainData.domain;

import lombok.Data;
/**
 * @project npic
 * @description MdmQueryParam领域实体，描述远端主数据查询参数核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class MdmQueryParam {
    private String name;
    private Object matchValue;
    private String matchType;
    private String matchOption;
}