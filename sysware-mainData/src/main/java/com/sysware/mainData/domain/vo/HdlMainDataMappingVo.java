package com.sysware.mainData.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;



/**
 * 主数据字段映射视图对象 hdl_main_data_mapping
 *
 * @author aa
 * @date 2026-01-14
 */
@Data
@ExcelIgnoreUnannotated
public class HdlMainDataMappingVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long mapId;

    /**
     * 源数据字段
     */
    @ExcelProperty(value = "源数据字段")
    private String sourceField;

    /**
     * 目标数据字段
     */
    @ExcelProperty(value = "目标数据字段")
    private String targetField;

    /**
     * 字段含义
     */
    @ExcelProperty(value = "字段含义")
    private String fieldMeaning;

    /**
     * 字段类型（1组织部门、2员工基本信息、3员工工作信息）
     */
    @ExcelProperty(value = "字段类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=组织部门、2员工基本信息、3员工工作信息")
    private String type;

    /**
     * 创建者工号
     */
    @ExcelProperty(value = "创建者工号")
    private String createId;

    /**
     * 更新者工号
     */
    @ExcelProperty(value = "更新者工号")
    private String updateId;


}
