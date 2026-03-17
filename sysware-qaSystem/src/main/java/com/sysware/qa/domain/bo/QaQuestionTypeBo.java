package com.sysware.qa.domain.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题类型表业务对象 qa_question_type
 *
 * @author aa
 * @date 2025-08-18
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class QaQuestionTypeBo extends BaseEntity {

    /**
     * 问题类型ID（主键）
     */
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
