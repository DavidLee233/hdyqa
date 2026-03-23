package com.sysware.mainData.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;
/**
 * @project npic
 * @description HdlMainDataMappingVo业务出参对象，封装主数据映射返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@ExcelIgnoreUnannotated
public class HdlMainDataMappingVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "主键")
    private Long mapId;
    @ExcelProperty(value = "源数据字段")
    private String sourceField;
    @ExcelProperty(value = "目标数据字段")
    private String targetField;
    @ExcelProperty(value = "字段含义")
    private String fieldMeaning;
    @ExcelProperty(value = "字段类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=组织部门、2员工基本信息、3员工工作信息")
    private String type;
    @ExcelProperty(value = "创建者工号")
    private String createId;
    @ExcelProperty(value = "更新者工号")
    private String updateId;


}