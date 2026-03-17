package com.sysware.qa.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题回答类型角色关联业务对象 qa_user_question
 *
 * @author aa
 * @date 2025-08-18
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class QaUserQuestionBo extends BaseEntity {
    /**
     * 组ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 问题类别
     */
    private String typeId;


}
