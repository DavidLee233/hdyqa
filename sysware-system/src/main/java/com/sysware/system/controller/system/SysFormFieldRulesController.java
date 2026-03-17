package com.sysware.web.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.bo.SysFormFieldRulesBo;
import com.sysware.system.domain.vo.SysFormFieldRulesVo;
import com.sysware.system.service.ISysFormFieldRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 校验规则
 *
 * @author aa
 * @date 2023-06-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/formFieldRules")
public class SysFormFieldRulesController extends BaseController {

    private final ISysFormFieldRulesService iSysFormFieldRulesService;

    /**
     * 查询校验规则列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysFormFieldRulesBo bo, PageQuery pageQuery) {
        return iSysFormFieldRulesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出校验规则列表
     */
    @SaCheckPermission("system:formFieldRules:export")
    @Log(title = "校验规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysFormFieldRulesBo bo, HttpServletResponse response) {
        List<SysFormFieldRulesVo> list = iSysFormFieldRulesService.queryList(bo);
        ExcelUtil.exportExcel(list, "校验规则", SysFormFieldRulesVo.class, response);
    }

    /**
     * 获取校验规则详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SysFormFieldRulesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(iSysFormFieldRulesService.queryById(id));
    }

    /**
     * 新增校验规则
     */
    @SaCheckPermission("system:formFieldRules:add")
    @Log(title = "校验规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysFormFieldRulesBo bo) {
        return toAjax(iSysFormFieldRulesService.insertByBo(bo));
    }

    /**
     * 修改校验规则
     */
    @SaCheckPermission("system:formFieldRules:edit")
    @Log(title = "校验规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFormFieldRulesBo bo) {
        return toAjax(iSysFormFieldRulesService.updateByBo(bo));
    }

    /**
     * 删除校验规则
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:formFieldRules:remove")
    @Log(title = "校验规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(iSysFormFieldRulesService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
