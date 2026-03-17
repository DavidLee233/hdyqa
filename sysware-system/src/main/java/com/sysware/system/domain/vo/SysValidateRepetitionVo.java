package com.sysware.system.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 验证重复VO
 */
@Data
@NoArgsConstructor
public class SysValidateRepetitionVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 验证表的 ID列名
     */
    private String validateIdName;

    /**
     * 验证表的ID列值
     */
    private String validateIdValue;

    /**
     * 字段名称
     */
    private String field;

    /**
     * 值
     */
    private String value;

    /**
     * 其他验证字段
     */
    private Map<String,Object> otherFieldValidate;



}
