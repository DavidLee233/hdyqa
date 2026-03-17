package com.sysware.qa.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.core.validate.QueryGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.qa.domain.vo.QaQuestionTypeVo;
import com.sysware.qa.domain.bo.QaQuestionTypeBo;
import com.sysware.qa.service.IQaQuestionTypeService;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 问题类型表
 *
 * @author aa
 * @date 2025-08-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qaQuestionType")
public class QaQuestionTypeController extends BaseController {

    private final IQaQuestionTypeService iQaQuestionTypeService;

    /**
     * 查询问题类型表列表
     */
    @GetMapping("/list")
    public TableDataInfo list(QaQuestionTypeBo bo, PageQuery pageQuery) {
        return iQaQuestionTypeService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取问题类型表详细信息
     *
     * @param questionId 主键
     */
    @GetMapping("/{questionId}")
    public R<QaQuestionTypeVo> getInfo(@NotNull(message = "主键不能为空")
                                       @PathVariable String questionId) {
        return R.ok(iQaQuestionTypeService.queryById(questionId));
    }

    /**
     * 获取问题类型顺序
     *
     * @param
     */
    @GetMapping("/sequenceNum")
    public R<Long> sequenceNum() {
        return iQaQuestionTypeService.getSeqenceNum();
    }


    /**
     * 新增问题类型表
     */
    @Log(title = "问题类型表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody QaQuestionTypeBo bo) {
        return iQaQuestionTypeService.insertByBo(bo);
    }

    /**
     * 修改问题类型表
     */
    @Log(title = "问题类型表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody QaQuestionTypeBo bo) {
        return iQaQuestionTypeService.updateByBo(bo);
    }

    /**
     * 删除问题类型表
     *
     * @param questionIds 主键串
     */
    @Log(title = "问题类型表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{questionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] questionIds) {
        return iQaQuestionTypeService.deleteWithValidByIds(Arrays.asList(questionIds));
    }
}
