package com.sysware.qa.domain.bo;

import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 问答论坛回复业务对象 qa_sys_reply
 *
 * @author aa
 * @date 2025-07-26
 */

@Data
public class QaSysReplyBo{

    /**
     * 回复id
     */
    private Long replyId;

    /**
     * 反馈记录id
     */
    private String recordId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 创建人id
     */
    private String createId;
    /**
     * 创建人姓名
     */
    private String createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 点赞数
     */
    @NotNull(message = "点赞数不能为空")
    private Long thumbsUp;

    /**
     * 提醒状态
     */
    private Long remind;

    /**
     * 回复层id
     */
    private String replyToId;

    /**
     * 回复层姓名
     */
    private String replyToUser;

    /**
     * 搜索值
     */
    private String searchValue;
}
