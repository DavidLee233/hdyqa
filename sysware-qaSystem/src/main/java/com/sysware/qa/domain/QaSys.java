package com.sysware.qa.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题回答对象 qa_sys
 *
 * @author aa
 * @date 2025-07-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qa_sys")
public class QaSys extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 记录ID
     */
    @TableId(value = "record_id")
    private String recordId;
    /**
     * 用户工号
     */
    private String createId;
    /**
     * 问题标题
     */
    private String title;
    /**
     * 问题内容
     */
    private String questionContent;
    /**
     * 回答内容
     */
    private String answerContent;
    /**
     * 问题状态
     */
    private String status;
    /**
     * 问题类别
     */
    private String type;
    /**
     * 严重程度
     */
    private String severity;
    /**
     * 是否可解决
     */
    private String canSolve;
    /**
     * 进度跟踪
     */
    private String track;
    /**
     * 回答问题用户工号
     */
    private String updateId;
    /**
     * 完结时间
     */
    private LocalDateTime solveTime;
    /**
     * 部门
     */
    private String department;
    /**
     * 电话
     */
    private String telephone;
    /**
     * 房间号
     */
    private String roomNumber;
    /**
     * 密级
     */
    private String classification;
    /**
     * 是否提醒
     */
    private Long notify;
    /**
     * 提醒内容
     */
    private String note;
    /**
     * 访问量
     */
    private Long visits;

}