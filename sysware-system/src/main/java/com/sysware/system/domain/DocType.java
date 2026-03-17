package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 文件类型对象 hdy_doc_type
 *
 * @author aa
 * @date 2024-06-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdy_doc_type")
public class DocType extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "doc_type_id")
    private String docTypeId;
    /**
     * 类型名称
     */
    private String docTypeName;
    /**
     * 类型编码
     */
    private String docTypeCode;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 描述
     */
    private String description;
    /**
     * 自动归档
     */
    private String autoArchive;
    /**
     * 创建后状态
     */
    private String createStatus;
    /**
     * 生成总号
     */
    private String autoTotal;
    /**
     * 生成档案号
     */
    private String autoRecordNo;
    /**
     * 适用项目类型
     */
    private String projectType;
    /**
     * 独立总号
     */
    private String independentTotal;

}