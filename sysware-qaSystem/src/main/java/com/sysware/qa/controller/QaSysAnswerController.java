package com.sysware.qa.controller;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.service.IQaSysAnswerService;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问题回答
 *
 * @author aa
 * @date 2025-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qa")
public class QaSysAnswerController extends BaseController {

    private final IQaSysAnswerService iQaSysService;
    /**
     * 查询问题回答列表（回答者）
     */
    @GetMapping("/listAUser")
    public TableDataInfo listAUser(QaSysBo bo, PageQuery pageQuery) {
        return iQaSysService.queryAPageList(bo, pageQuery);
    }

    /**
     * 查询需提醒的问题回答列表
     */
    @GetMapping("/listAnswerNotify")
    public R<List<QaSys>> listAnswerNotify() {
        return iQaSysService.queryAnswerNotifyList();
    }

    /**
     * 问题回答（回答者）
     */
    @Log(title = "问题回答", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/answer")
    public R<Void> answer(@Validated(EditGroup.class) @RequestBody QaSysBo bo) {
        return iQaSysService.answerByBo(bo);
    }
}
