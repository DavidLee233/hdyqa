package com.sysware.web.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.constant.CacheConstants;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.common.utils.redis.RedisUtils;
import com.sysware.system.domain.SysLogininfor;
import com.sysware.system.domain.bo.SysLogininforBo;
import com.sysware.system.domain.vo.SysLogininforVo;
import com.sysware.system.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 系统访问记录
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    /**
     * 获取系统访问记录列表
     */
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforBo logininfor, PageQuery pageQuery) {
        return logininforService.selectPageLogininforList(logininfor, pageQuery);
    }

    /**
     * 获取系统访问记录详细信息
     *
     * @param infoId 主键
     */
    @GetMapping("/{infoId}")
    public R<SysLogininforVo> getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable String infoId) {
        return R.ok(logininforService.queryById(infoId));
    }

    /**
     * 新增系统访问记录
     */
    @Log(title = "系统访问记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysLogininforBo bo) {
        return toAjax(logininforService.insertByBo(bo));
    }

    /**
     * 修改系统访问记录
     */
    @Log(title = "系统访问记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysLogininforBo bo) {
        return toAjax(logininforService.updateByBo(bo));
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLogininforBo logininfor, HttpServletResponse response) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininfor.class, response);
    }

    /**
     * 批量删除登录日志
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable String[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@PathVariable("userName") String userName) {
        String loginName = CacheConstants.PWD_ERR_CNT_KEY + userName;
        if (RedisUtils.hasKey(loginName)) {
            RedisUtils.deleteObject(loginName);
        }
        return R.ok();
    }

}
