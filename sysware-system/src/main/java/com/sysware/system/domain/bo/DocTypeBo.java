package com.sysware.system.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 文件类型业务对象 hdy_doc_type
 *
 * @author aa
 * @date 2024-06-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DocTypeBo extends BaseEntity {


    /**
     * 类型ID
     */
    @NotBlank(message = "类型ID不能为空", groups = {EditGroup.class })
    private String docTypeId;
    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String docTypeName;

    /**
     * 类型编码
     */
    @NotBlank(message = "类型编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String docTypeCode;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sort;

    /**
     * 描述
     */
    private String description;

    /**
     * 自动归档
     */
    @NotBlank(message = "自动归档不能为空", groups = { AddGroup.class, EditGroup.class })
    private String autoArchive;

    /**
     * 创建后状态
     */
    @NotBlank(message = "创建后状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String createStatus;

    /**
     * 生成总号
     */
    @NotBlank(message = "生成总号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String autoTotal;

    /**
     * 生成档案号
     */
    @NotBlank(message = "生成档案号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String autoRecordNo;

    /**
     * 适用项目类型
     */
    @NotBlank(message = "适用项目类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String projectType;

    /**
     * 独立总号
     */
    @NotBlank(message = "独立总号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String independentTotal;


    private String projectId;


}