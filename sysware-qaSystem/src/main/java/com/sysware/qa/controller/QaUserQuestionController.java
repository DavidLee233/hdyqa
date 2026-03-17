package com.sysware.qa.controller;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
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
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.qa.domain.bo.QaUserQuestionBo;
import com.sysware.qa.service.IQaUserQuestionService;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 问题回答类型角色关联
 *
 * @author aa
 * @date 2025-08-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qaUserQuestion")
public class QaUserQuestionController extends BaseController {

    private final IQaUserQuestionService iQaUserQuestionService;

    /**
     * 查询问题回答类型角色关联列表
     */
    @GetMapping("/list")
    public TableDataInfo list(QaUserQuestionBo bo, PageQuery pageQuery) {
        return iQaUserQuestionService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取问题回答类型角色关联详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<QaUserQuestionVo> getInfo(@NotNull(message = "主键不能为空")
                                       @PathVariable String id) {
        return R.ok(iQaUserQuestionService.queryById(id));
    }

    /**
     * 新增问题回答类型角色关联
     */
    @Log(title = "问题回答类型角色关联", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody QaUserQuestionBo bo) {
        return iQaUserQuestionService.insertByBo(bo);
    }

    /**
     * 修改问题回答类型角色关联
     */
    @Log(title = "问题回答类型角色关联", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody QaUserQuestionBo bo) {
        return iQaUserQuestionService.updateByBo(bo);
    }

    /**
     * 删除问题回答类型角色关联
     *
     * @param ids 主键串
     */
    @Log(title = "问题回答类型角色关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return iQaUserQuestionService.deleteWithValidByIds(Arrays.asList(ids));
    }
}
