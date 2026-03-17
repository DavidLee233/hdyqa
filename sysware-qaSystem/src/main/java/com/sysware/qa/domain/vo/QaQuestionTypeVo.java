package com.sysware.qa.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;



/**
 * 问题类型表视图对象 qa_question_type
 *
 * @author aa
 * @date 2025-08-18
 */
@Data
@ExcelIgnoreUnannotated
public class QaQuestionTypeVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 问题类型ID
     */
    @ExcelProperty(value = "问题类型ID（主键）")
    private String typeId;

    /**
     * 问题类型
     */
    @ExcelProperty(value = "问题类型")
    private String type;

    /**
     * 是否占用
     */
    @ExcelProperty(value = "是否占用")
    private Long occupied;

    /**
     * 顺序排列
     */
    @ExcelProperty(value = "顺序排列")
    private Long sequence;
}
