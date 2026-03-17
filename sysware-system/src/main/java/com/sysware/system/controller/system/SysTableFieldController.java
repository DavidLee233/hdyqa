package com.sysware.web.controller.system;

import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.bo.SysTableFieldAddBo;
import com.sysware.system.domain.bo.SysTableFieldEditBo;
import com.sysware.system.domain.bo.SysTableFieldQueryBo;
import com.sysware.system.domain.SysTableField;
import com.sysware.system.domain.vo.SysTableFieldVo;
import com.sysware.system.service.ISysTableFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自定义页面显示Controller
 *
 * @author aa
 * @date 2022-08-03
 */
@Validated
@Api(value = "自定义页面显示控制器", tags = {"自定义页面显示管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/system/field")
public class SysTableFieldController extends BaseController {

    private final ISysTableFieldService iSysTableFieldService;

    /**
     * 查询自定义页面显示列表
     * @SaCheckPermission("@ss.hasPermi('system:field:list')")
     */
    @ApiOperation("查询自定义页面显示列表")
    @GetMapping("/list")
    public TableDataInfo<SysTableFieldVo> list(@Validated SysTableFieldQueryBo bo,PageQuery pageQuery) {
        return iSysTableFieldService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询自定义页面显示列表
     */
    @ApiOperation("根据路径查询自定义页面显示列表")
    @GetMapping("/listFieldByPath")
    public TableDataInfo<SysTableFieldVo> listFieldByPath(@Validated SysTableFieldQueryBo bo, PageQuery pageQuery) {
        return iSysTableFieldService.queryFieldlistByPath(bo, pageQuery);
    }

    /**
     * 导出自定义页面显示列表
     */
    @ApiOperation("导出自定义页面显示列表")
    @SaCheckPermission("system:field:export")
    @Log(title = "自定义页面显示", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(@Validated SysTableFieldQueryBo bo, HttpServletResponse response) {
        List<SysTableFieldVo> list = iSysTableFieldService.queryList(bo);
        ExcelUtil.exportExcel(list, "自定义页面显示",SysTableFieldVo.class,response);
    }

    /**
     * 获取自定义页面显示详细信息
     */
    @ApiOperation("获取自定义页面显示详细信息")
    @GetMapping("/{id}")
    public R getInfo(@NotNull(message = "主键不能为空")
                                                  @PathVariable("id") String id) {
        return R.ok(iSysTableFieldService.queryById(id));
    }

    /**
     * 新增自定义页面显示
     */
    @ApiOperation("新增自定义页面显示")
    @SaCheckPermission("system:field:add")
    @Log(title = "自定义页面显示", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping()
    public R<Void> add(@Validated @RequestBody SysTableFieldAddBo bo) {
        return toAjax(iSysTableFieldService.insertByAddBo(bo) ? 1 : 0);
    }

    /**
     * 修改自定义页面显示
     */
    @ApiOperation("修改自定义页面显示")
    @SaCheckPermission("system:field:edit")
    @Log(title = "自定义页面显示", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody SysTableFieldEditBo bo) {
        return toAjax(iSysTableFieldService.updateByEditBo(bo) ? 1 : 0);
    }

    /**
     * 删除自定义页面显示
     */
    @ApiOperation("删除自定义页面显示")
    @SaCheckPermission("system:field:remove")
    @Log(title = "自定义页面显示" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                                       @PathVariable String[] ids) {
        return toAjax(iSysTableFieldService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
