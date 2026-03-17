package com.sysware.common.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperationObject implements Serializable {


    /**
     * 操作对象ID
     */
    private String operObjectId;
    /**
     * 操作对象名称
     */
    private String operObjectName;
    /**
     * 操作对象密级ID
     */
    private String operObjectSecurityId;
    /**
     * 操作对象密级名称
     */
    private String operObjectSecurityName;
    /**
     * 操作对象密级名称
     */
    private Integer operObjectSecurityValue ;
    /**
     * 操作对象表名称
     */
    private String operObjectTableName ;

}
