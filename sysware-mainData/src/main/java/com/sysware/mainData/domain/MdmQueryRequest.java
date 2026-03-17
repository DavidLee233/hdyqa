package com.sysware.mainData.domain;

import lombok.Data;

import java.util.List;

/**
 * MDM查询请求实体类
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
