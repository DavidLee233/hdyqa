package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 文档著录字段对象 hdy_doc_field
 *
 * @author aa
 * @date 2023-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdy_doc_field")
public class DocField extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private String id;
    /**
     * 文档名称
     */
    private String docName;
    /**
     * 文档ID
     */
    private String docId;
    /**
     * 字段编码
     */
    private String code;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 字段名称
     */
    private String label;
    /**
     * 字段长度
     */
    private String length;
    /**
     * 是否必填
     */
    private String required;
    /**
     * 新建显示
     */
    private String addShow;
    /**
     * 归档显示
     */
    private String archiveShow;
    /**
     * 详情显示
     */
    private String detailsShow;
    /**
     * 序号
     */
    private Long sort;

}