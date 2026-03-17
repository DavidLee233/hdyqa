package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;



/**
 * 操作的数据视图对象 sys_oper_params
 *
 * @author aa
 * @date 2024-06-30
 */
@Data
@ExcelIgnoreUnannotated
public class SysOperParamsVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 操作ID
     */
    @ExcelProperty(value = "操作ID")
    private String operId;

    /**
     * 操作前数据
     */
    @ExcelProperty(value = "操作前数据")
    private String operPreParam;

    /**
     * 操作后数据
     */
    @ExcelProperty(value = "操作后数据")
    private String operAfterParam;

    /**
     * 操作对象表
     */
    @ExcelProperty(value = "操作对象表")
    private String operTableName;


}
