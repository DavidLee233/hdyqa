package com.sysware.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.SysOperLog;
import com.sysware.system.domain.bo.SysOperLogBo;
import com.sysware.system.domain.vo.SysOperLogVo;
import com.sysware.system.service.ISysOperLogService;
import com.sysware.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 操作日志记录
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    private final ISysOperLogService operLogService;

    private final ISysUserService sysUserService;

    /**
     * 获取操作日志记录列表
     */
    @GetMapping("/list")
    public TableDataInfo<SysOperLogVo> list(SysOperLogBo operLog, PageQuery pageQuery) {
        return operLogService.selectPageOperLogList(operLog, pageQuery);
    }

    /**
     * 清理操作日志记录
     */
    @GetMapping("/getOperData")
    public R getOperData(SysOperLogBo operLog) {
        return operLogService.getOperData(operLog);
    }

    /**
     * 获取操作日志记录详细信息
     *
     * @param operId 主键
     */
    @GetMapping("/{operId}")
    public R<SysOperLogVo> getInfo(@NotNull(message = "主键不能为空")
                                   @PathVariable String operId) {
        return R.ok(operLogService.queryById(operId));
    }

    /**
     * 新增操作日志记录
     */
    @Log(title = "操作日志记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysOperLogBo bo) {
        return toAjax(operLogService.insertByBo(bo));
    }

    /**
     * 修改操作日志记录
     */
    @Log(title = "操作日志记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOperLogBo bo) {
        return toAjax(operLogService.updateByBo(bo));
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "导出操作日志记录列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysOperLogBo operLog, HttpServletResponse response) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperLog.class, response);
    }

    /**
     * 删除操作日志记录
     *
     * @param operIds 主键串
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] operIds) {
        return toAjax(operLogService.deleteOperLogByIds(Arrays.asList(operIds)));
    }

    /**
     * 清理操作日志记录
     */
    @Log(title = "清理操作日志记录", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }

    /**
     * 查询用户信息（通用方法）
     */
    @GetMapping("/user")
    public R<SysUser> getUser() {
        return sysUserService.getCurrentUser();
    }
}
