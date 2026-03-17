package com.sysware.qa.controller;

import java.util.Arrays;

import com.sysware.qa.domain.QaSysReply;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.qa.domain.bo.QaSysReplyBo;
import com.sysware.qa.service.IQaSysReplyService;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 问答论坛回复
 *
 * @author aa
 * @date 2025-07-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qaReply")
public class QaSysReplyController extends BaseController {

    private final IQaSysReplyService iQaSysReplyService;

    /**
     * 查询问答论坛回复列表
     */
    @GetMapping("/list")
    public TableDataInfo list(QaSysReplyBo bo, PageQuery pageQuery) {
        return iQaSysReplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 新增问答论坛回复
     */
    @Log(title = "问答论坛回复", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<QaSysReply> add(@Validated(AddGroup.class) @RequestBody QaSysReplyBo bo) {
        return iQaSysReplyService.insertByBo(bo);
    }

    // 获取回复的全局楼层号
    @GetMapping("/floor/{replyId}")
    public R<Integer> getReplyFloorNumber(@PathVariable String replyId) {
        return iQaSysReplyService.getFloorNum(replyId);
    }

    // 获取回复的全局楼层号页码
    @GetMapping("/floor")
    public R<Integer> getReplyPageNumber(@RequestParam String replyId,
                                         @RequestParam int pageSize) {
        return iQaSysReplyService.getPageNum(replyId, pageSize);
    }

    @Log(title = "点赞问答论坛回复", businessType = BusinessType.UPDATE)
    @PostMapping("/thumbsUp/{replyId}")
    public R<Void> thumbsUp(@PathVariable String replyId) {
        return toAjax(iQaSysReplyService.thumbsUp(replyId));
    }

    /**
     * 删除问答论坛回复
     *
     * @param replyIds 主键串
     */
    @Log(title = "问答论坛回复", businessType = BusinessType.DELETE)
    @DeleteMapping("/{replyIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] replyIds) {
        return iQaSysReplyService.deleteWithValidByIds(Arrays.asList(replyIds));
    }

}
