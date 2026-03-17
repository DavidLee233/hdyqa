package com.sysware.qa.controller;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.service.IQaSysQuestionService;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
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
public class QaSysQuestionController extends BaseController {

    private final IQaSysQuestionService iQaSysService;
    /**
     * 查询问题回答列表（提问者）
     */
    @GetMapping("/listQUser")
    public TableDataInfo listQUser(QaSysBo bo, PageQuery pageQuery) {
        return iQaSysService.queryQPageList(bo, pageQuery);
    }

    /**
     * 查询需提醒的问题回答列表
     */
    @GetMapping("/listQuestionNotify")
    public R<List<QaSys>> listQuestionNotify() {
        return iQaSysService.queryQuestionNotifyList();
    }

    /**
     * 新增问题回答（提问者）
     */
    @Log(title = "新增问题", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody QaSysBo bo) {
        return iQaSysService.insertByBo(bo);
    }

    /**
     * 修改问题回答（提问者）
     */
    @Log(title = "修改问题", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody QaSysBo bo) {
        return iQaSysService.updateByBo(bo);
    }

    /**
     * 删除问题回答（提问者）
     *
     * @param recordIds 主键串
     */
    @Log(title = "删除问答", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] recordIds) {
        return iQaSysService.deleteWithValidByIds(Arrays.asList(recordIds), true);
    }

    /**
     * 采纳问题回答（提问者）
     */
    @Log(title = "采纳回答", businessType = BusinessType.UPDATE)
    @PutMapping("/{recordIds}")
    public R<Void> accept(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] recordIds) {
        return iQaSysService.acceptByIds(Arrays.asList(recordIds));
    }
}
