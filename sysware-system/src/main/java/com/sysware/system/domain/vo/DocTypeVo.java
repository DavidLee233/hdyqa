package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;



/**
 * 文件类型视图对象 hdy_doc_type
 *
 * @author aa
 * @date 2024-06-19
 */
@Data
@ExcelIgnoreUnannotated
public class DocTypeVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String docTypeId;

    /**
     * 类型名称
     */
    @ExcelProperty(value = "类型名称")
    private String docTypeName;

    /**
     * 类型编码
     */
    @ExcelProperty(value = "类型编码")
    private String docTypeCode;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 自动归档
     */
    @ExcelProperty(value = "自动归档")
    private String autoArchive;

    /**
     * 创建后状态
     */
    @ExcelProperty(value = "创建后状态")
    private String createStatus;

    /**
     * 生成总号
     */
    @ExcelProperty(value = "生成总号")
    private String autoTotal;

    /**
     * 生成档案号
     */
    @ExcelProperty(value = "生成档案号")
    private String autoRecordNo;

    /**
     * 适用项目类型
     */
    @ExcelProperty(value = "适用项目类型")
    private String projectType;

    /**
     * 独立总号
     */
    @ExcelProperty(value = "独立总号")
    private String independentTotal;


}