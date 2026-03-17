package com.sysware.qa.domain.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问题回答附件业务对象 qa_sys_attach
 *
 * @author aa
 * @date 2025-07-20
 */

@Data
public class QaSysAttachBo{

    /**
     * 唯一ID
     */
    private Long id;

    /**
     * 关联的问题ID
     */
    private String recordId;

    /**
     * 文件ID（对应桶文件ID）
     */
    private Long ossId;
    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否删除(0-未删,1-已删)
     */
    private Long isDeleted;

    /**
     * 附件类型(question/answer)
     */
    private String attachType;

    /**
     * 文件存储url
     */
    private String url;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
