package com.sysware.mainData.domain;

import lombok.Data;

/**
 * MDM查询参数实体类
 */
@Data
public class MdmQueryParam {
    private String name;
    private Object matchValue;
    private String matchType;
    private String matchOption;
}
