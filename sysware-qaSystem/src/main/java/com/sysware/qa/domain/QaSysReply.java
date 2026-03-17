package com.sysware.qa.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问答论坛回复对象 qa_sys_reply
 *
 * @author aa
 * @date 2025-07-26
 */
@Data
@TableName("qa_sys_reply")
public class QaSysReply{

    private static final long serialVersionUID=1L;

    /**
     * 回复id
     */
    @TableId(value = "reply_id")
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

}
