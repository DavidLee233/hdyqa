package com.sysware.mainData.domain;

import lombok.Data;

import java.util.List;
/**
 * @project npic
 * @description MdmQueryRequest领域实体，描述远端主数据查询参数核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
public class MdmQueryRequest {
    private Boolean withSysColumn;
    private Integer pageNum;
    private Integer pageSize;
    private List<MdmParseRefParam> parseRef;
    private List<MdmQueryParam> args;
    private List<MdmSortParam> sorts;
    private Boolean and;
}