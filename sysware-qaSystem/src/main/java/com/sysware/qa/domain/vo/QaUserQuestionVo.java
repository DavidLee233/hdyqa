package com.sysware.qa.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;



/**
 * 问题回答类型角色关联视图对象 qa_user_question
 *
 * @author aa
 * @date 2025-08-18
 */
@Data
@ExcelIgnoreUnannotated
public class QaUserQuestionVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 组ID
     */
    private String id;
    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private String userId;

    /**
     * 工号
     */
    @ExcelProperty(value = "用户工号")
    private String loginName;

    /**
     * 姓名
     */
    @ExcelProperty(value = "用户姓名")
    private String userName;

    /**
     * 问题类别
     */
    @ExcelProperty(value = "问题类别")
    private String typeId;

    /**
     * 问题类型
     */
    @ExcelProperty(value = "问题类型")
    private String type;


}
