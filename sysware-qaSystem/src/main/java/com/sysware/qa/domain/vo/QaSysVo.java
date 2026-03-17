package com.sysware.qa.domain.vo;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * 问题回答视图对象 qa_sys
 *
 * @author aa
 * @date 2025-07-20
 */
@Data
@ExcelIgnoreUnannotated
public class QaSysVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String recordId;

    /**
     * 用户工号
     */
    @ExcelProperty(value = "用户工号")
    private String createId;

    /**
     * 问题标题
     */
    @ExcelProperty(value = "问题标题")
    private String title;

    /**
     * 问题内容
     */
    @ExcelProperty(value = "问题内容")
    private String questionContent;

    /**
     * 回答内容
     */
    @ExcelProperty(value = "回答内容")
    private String answerContent;

    /**
     * 问题状态
     */
    @ExcelProperty(value = "问题状态")
    private String status;

    /**
     * 问题类别
     */
    @ExcelProperty(value = "问题类别")
    private String type;

    /**
     * 严重程度
     */
    @ExcelProperty(value = "严重程度")
    private String severity;

    /**
     * 是否可解决
     */
    @ExcelProperty(value = "是否可解决")
    private String canSolve;

    /**
     * 进度跟踪
     */
    @ExcelProperty(value = "进度跟踪")
    private String track;

    /**
     * 回答问题用户工号
     */
    @ExcelProperty(value = "回答问题用户工号")
    private String updateId;

    /**
     * 完结时间
     */
    @ExcelProperty(value = "完结时间")
    private LocalDateTime solveTime;

    /**
     * 部门
     */
    @ExcelProperty(value = "部门")
    private String department;

    /**
     * 电话
     */
    @ExcelProperty(value = "电话")
    private String telephone;

    /**
     * 房间号
     */
    @ExcelProperty(value = "房间号")
    private String roomNumber;

    /**
     * 密级
     */
    @ExcelProperty(value = "密级")
    private String classification;

    /**
     * 是否提醒
     */
    @ExcelProperty(value = "是否提醒")
    private Long notify;

    /**
     * 提醒内容
     */
    @ExcelProperty(value = "提醒内容")
    private String note;

    /**
     * 访问量
     */
    @ExcelProperty(value = "访问量")
    private Long visits;
    /**
     * 问题附件列表
     */
    private List<QaSysAttachVo> questionAttachList;
    /**
     * 问题附件列表
     */
    private List<QaSysAttachVo> answerAttachList;
    /**
     * 用户唯一ID
     */
    private String userId;

    /**
     * 是否提醒
     */
    private Integer openNotify;

}