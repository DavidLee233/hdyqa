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
import com.sysware.system.domain.bo.SysPageBtnBo;
import com.sysware.system.domain.vo.SysPageBtnVo;
import com.sysware.system.service.ISysPageBtnService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 页面按钮
 *
 * @author aa
 * @date 2023-05-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/pageBtn")
public class SysPageBtnController extends BaseController {

    private final ISysPageBtnService iSysPageBtnService;

    /**
     * 查询页面按钮列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysPageBtnBo bo, PageQuery pageQuery) {
        return iSysPageBtnService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出页面按钮列表
     */
    @SaCheckPermission("system:pageBtn:export")
    @Log(title = "页面按钮", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysPageBtnBo bo, HttpServletResponse response) {
        List<SysPageBtnVo> list = iSysPageBtnService.queryList(bo);
        ExcelUtil.exportExcel(list, "页面按钮", SysPageBtnVo.class, response);
    }

    /**
     * 获取页面按钮详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SysPageBtnVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(iSysPageBtnService.queryById(id));
    }

    /**
     * 新增页面按钮
     */
    @SaCheckPermission("system:pageBtn:add")
    @Log(title = "页面按钮", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysPageBtnBo bo) {
        return toAjax(iSysPageBtnService.insertByBo(bo));
    }

    /**
     * 修改页面按钮
     */
    @SaCheckPermission("system:pageBtn:edit")
    @Log(title = "页面按钮", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysPageBtnBo bo) {
        return toAjax(iSysPageBtnService.updateByBo(bo));
    }

    /**
     * 删除页面按钮
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:pageBtn:remove")
    @Log(title = "页面按钮", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(iSysPageBtnService.deleteWithValidByIds(Arrays.asList(ids), true));
    }


    /**
     * 初始化页面按钮
     *
     */
    @SaCheckPermission("system:pageBtn:init")
    @Log(title = "页面按钮", businessType = BusinessType.INSERT)
    @GetMapping("/initBtn")
    public R<Void> initBtn() {
        return toAjax(iSysPageBtnService.initBtn());
    }
}
