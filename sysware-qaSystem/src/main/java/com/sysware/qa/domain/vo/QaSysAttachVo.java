package com.sysware.qa.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 问题回答附件视图对象 qa_sys_attach
 *
 * @author aa
 * @date 2025-07-20
 */
@Data
@ExcelIgnoreUnannotated
public class QaSysAttachVo {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @ExcelProperty(value = "唯一ID")
    private Long id;

    /**
     * 关联的问题ID
     */
    @ExcelProperty(value = "关联的问题ID")
    private String recordId;

    /**
     * 文件ID
     */
    @ExcelProperty(value = "文件ID")
    private Long ossId;

    /**
     * 文件名
     */
    @ExcelProperty(value = "文件名")
    private String name;

    /**
     * 文件大小
     */
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 是否删除(0-未删,1-已删)
     */
    @ExcelProperty(value = "是否删除(0-未删,1-已删)")
    private Long isDeleted;

    /**
     * 附件类型(question/answer)
     */
    @ExcelProperty(value = "附件类型(question/answer)")
    private String attachType;

    /**
     * 文件存储url
     */
    @ExcelProperty(value = "文件存储url")
    private String url;
    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
