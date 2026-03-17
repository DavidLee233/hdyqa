package com.sysware.system.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 文档著录字段业务对象 hdy_doc_field
 *
 * @author aa
 * @date 2023-09-02
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DocFieldBo extends BaseEntity {

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空", groups = { EditGroup.class })
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