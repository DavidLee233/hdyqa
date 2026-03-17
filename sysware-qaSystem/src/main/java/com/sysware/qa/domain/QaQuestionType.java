package com.sysware.qa.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题类型表对象 qa_question_type
 *
 * @author aa
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qa_question_type")
public class QaQuestionType extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 问题类型ID（主键）
     */
    @TableId(value = "type_id")
    private String typeId;
    /**
     * 问题类型
     */
    private String type;
    /**
     * 是否占用
     */
    private Long occupied;
    /**
     * 顺序排列
     */
    private Long sequence;
}
