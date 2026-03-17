package com.sysware.web.controller.system;

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
import com.sysware.system.domain.vo.SysFormFieldVisibleVo;
import com.sysware.system.domain.bo.SysFormFieldVisibleBo;
import com.sysware.system.service.ISysFormFieldVisibleService;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 单项显示控制
 *
 * @author aa
 * @date 2023-06-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/formFieldVisible")
public class SysFormFieldVisibleController extends BaseController {

    private final ISysFormFieldVisibleService iSysFormFieldVisibleService;

    /**
     * 查询单项显示控制列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysFormFieldVisibleBo bo, PageQuery pageQuery) {
        return iSysFormFieldVisibleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出单项显示控制列表
     */
    @SaCheckPermission("system:formFieldVisible:export")
    @Log(title = "单项显示控制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysFormFieldVisibleBo bo, HttpServletResponse response) {
        List<SysFormFieldVisibleVo> list = iSysFormFieldVisibleService.queryList(bo);
        ExcelUtil.exportExcel(list, "单项显示控制", SysFormFieldVisibleVo.class, response);
    }

    /**
     * 获取单项显示控制详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SysFormFieldVisibleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(iSysFormFieldVisibleService.queryById(id));
    }

    /**
     * 新增单项显示控制
     */
    @SaCheckPermission("system:formFieldVisible:add")
    @Log(title = "单项显示控制", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysFormFieldVisibleBo bo) {
        return toAjax(iSysFormFieldVisibleService.insertByBo(bo));
    }

    /**
     * 修改单项显示控制
     */
    @SaCheckPermission("system:formFieldVisible:edit")
    @Log(title = "单项显示控制", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFormFieldVisibleBo bo) {
        return toAjax(iSysFormFieldVisibleService.updateByBo(bo));
    }

    /**
     * 删除单项显示控制
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:formFieldVisible:remove")
    @Log(title = "单项显示控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(iSysFormFieldVisibleService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
