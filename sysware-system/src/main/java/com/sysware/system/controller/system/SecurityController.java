package com.sysware.web.controller.system;

import java.util.List;
import java.util.Arrays;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysDictType;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.enums.BusinessType;
import com.sysware.system.domain.vo.SecurityVo;
import com.sysware.system.domain.bo.SecurityQueryBo;
import com.sysware.system.domain.bo.SecurityAddBo;
import com.sysware.system.domain.bo.SecurityEditBo;
import com.sysware.system.service.ISecurityService;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 密级Controller
 *
 * @author zzr
 * @date 2022-01-05
 */
@Validated
@Api(value = "密级控制器", tags = {"密级管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/system/security")
public class SecurityController extends BaseController {

    private final ISecurityService iSecurityService;

    /**
     * 查询密级列表
     */
    @ApiOperation("查询密级列表")
    @GetMapping("/list")
    public TableDataInfo list(@Validated SecurityQueryBo bo, PageQuery pageQuery) {
        return iSecurityService.queryPageList(bo,pageQuery);
    }

    /**
     * 查询密级列表（用户）
     */
    @ApiOperation("查询用户密级列表")
    @GetMapping("/listUser")
    public TableDataInfo<SecurityVo> listUser(@Validated SecurityQueryBo bo, PageQuery pageQuery) {
        bo.setSecurityType("user");
        TableDataInfo result = new TableDataInfo();
        result.setRows(iSecurityService.queryList(bo));
        return result;
    }

    /**
     * 查询密级列表（数据）
     */
    @ApiOperation("查询数据密级列表")
    @GetMapping("/listData")
    public TableDataInfo<SecurityVo> listData(@Validated SecurityQueryBo bo, PageQuery pageQuery) {
        bo.setSecurityType("data");
        TableDataInfo result = new TableDataInfo();
        result.setRows(iSecurityService.queryList(bo));
        return result;
    }

    /**
     * 导出密级列表
     */
    @ApiOperation("导出密级列表")
    @SaCheckPermission("system:security:export")
    @Log(title = "密级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated SecurityQueryBo bo, HttpServletResponse response) {

        List<SecurityVo> list = iSecurityService.queryList(bo);
        ExcelUtil.exportExcel(list, "字典类型", SecurityVo.class, response);
    }

    /**
     * 获取密级详细信息
     */
    @ApiOperation("获取密级详细信息")
    @GetMapping("/{id}")
    public R<SecurityVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") String id) {
        return R.ok(iSecurityService.queryById(id));
    }

    /**
     * 新增密级
     */
    @ApiOperation("新增密级")
    @SaCheckPermission("system:security:add")
    @Log(title = "密级", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping()
    public R<Void> add(@Validated @RequestBody SecurityAddBo bo) {
        return toAjax(iSecurityService.insertByAddBo(bo) ? 1 : 0);
    }

    /**
     * 修改密级
     */
    @ApiOperation("修改密级")
    @SaCheckPermission("system:security:edit")
    @Log(title = "密级", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody SecurityEditBo bo) {
        return toAjax(iSecurityService.updateByEditBo(bo) ? 1 : 0);
    }

    /**
     * 删除密级
     */
    @ApiOperation("删除密级")
    @SaCheckPermission("system:security:remove")
    @Log(title = "密级" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                                       @PathVariable String[] ids) {
        return toAjax(iSecurityService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
