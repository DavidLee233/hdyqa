package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 文档著录字段视图对象 hdy_doc_field
 *
 * @author aa
 * @date 2023-09-02
 */
@Data
@ExcelIgnoreUnannotated
public class DocFieldVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 文档名称
     */
    @ExcelProperty(value = "文档名称")
    private String docName;

    /**
     * 文档ID
     */
    @ExcelProperty(value = "文档ID")
    private String docId;

    /**
     * 字段编码
     */
    @ExcelProperty(value = "字段编码")
    private String code;

    /**
     * 字段类型
     */
    @ExcelProperty(value = "字段类型")
    private String type;

    /**
     * 字段名称
     */
    @ExcelProperty(value = "字段名称")
    private String label;

    /**
     * 字段长度
     */
    @ExcelProperty(value = "字段长度")
    private String length;

    /**
     * 是否必填
     */
    @ExcelProperty(value = "是否必填")
    private String required;

    /**
     * 新建显示
     */
    @ExcelProperty(value = "新建显示")
    private String addShow;

    /**
     * 归档显示
     */
    @ExcelProperty(value = "归档显示")
    private String archiveShow;

    /**
     * 详情显示
     */
    @ExcelProperty(value = "详情显示")
    private String detailsShow;

    /**
     * 序号
     */
    @ExcelProperty(value = "序号")
    private Long sort;


}