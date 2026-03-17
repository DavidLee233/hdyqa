package com.sysware.qa.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.List;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题回答业务对象 qa_sys
 *
 * @author aa
 * @date 2025-07-20
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class QaSysBo extends BaseEntity {

    /**
     * 记录ID
     */
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
    private Date solveTime;

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
     * 搜索人
     */
    private String searchUser;

    /**
     * 页面类型（提问界面、回答界面、管理界面）
     */
    private String pageType;

    /**
     * 访问量
     */
    @NotNull(message = "访问量不能为空")
    private Long visits;
    /**
     * 问题附件列表
     */
    private List<QaSysAttachBo> questionAttachList;
    /**
     * 问题附件列表
     */
    private List<QaSysAttachBo> answerAttachList;
}