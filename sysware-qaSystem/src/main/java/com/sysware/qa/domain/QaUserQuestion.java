package com.sysware.qa.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 问题回答类型角色关联对象 qa_user_question
 *
 * @author aa
 * @date 2025-08-18
 */
@Data
@TableName("qa_user_question")
public class QaUserQuestion{

    private static final long serialVersionUID=1L;
    /**
     * 主键ID
     */
    @TableId(value = "id")
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
