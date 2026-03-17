package com.sysware.qa.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 问答论坛回复视图对象 qa_sys_reply
 *
 * @author aa
 * @date 2025-07-26
 */
@Data
@ExcelIgnoreUnannotated
public class QaSysReplyVo{

    private static final long serialVersionUID = 1L;

    /**
     * 回复id
     */
    @ExcelProperty(value = "回复id")
    private Long replyId;

    /**
     * 反馈记录id
     */
    @ExcelProperty(value = "反馈记录id")
    private String recordId;

    /**
     * 反馈内容
     */
    @ExcelProperty(value = "反馈内容")
    private String content;

    /**
     * 创建人id
     */
    @ExcelProperty(value = "创建人id")
    private String createId;

    /**
     * 创建人姓名
     */
    @ExcelProperty(value = "创建人姓名")
    private String createBy;
    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 点赞数
     */
    @ExcelProperty(value = "点赞数")
    private Long thumbsUp;

    /**
     * 提醒状态
     */
    @ExcelProperty(value = "提醒状态")
    private Long remind;

    /**
     * 回复层id
     */
    @ExcelProperty(value = "回复层id")
    private String replyToId;

    /**
     * 回复层姓名
     */
    @ExcelProperty(value = "回复层姓名")
    private String replyToUser;


}
