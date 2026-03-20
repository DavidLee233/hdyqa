package com.sysware.mainData.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.service.IRemoteDataService;
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
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.core.validate.QueryGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.mainData.domain.bo.HdlPersonJobInfoBo;
import com.sysware.mainData.service.IHdlPersonJobInfoService;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 员工工作信息数据
 *
 * @author aa
 * @date 2026-01-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mainData/personJobInfo")
public class HdlPersonJobInfoController extends BaseController {

    private final IHdlPersonJobInfoService iHdlPersonJobInfoService;
    @Autowired
    private IRemoteDataService remoteDataService;

    /**
     * 查询员工工作信息数据列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlPersonJobInfoBo bo, PageQuery pageQuery) {
        return iHdlPersonJobInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出员工工作信息数据列表
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlPersonJobInfo pji, HttpServletResponse response) {
        List<HdlPersonJobInfo> list = iHdlPersonJobInfoService.selectPJIList(pji);
        List<HdlPersonJobInfoVo> listVo = BeanUtil.copyToList(list, HdlPersonJobInfoVo.class);
        ExcelUtil.exportExcel(listVo, "员工基本信息数据", HdlPersonJobInfoVo.class, response);
    }

    /**
     * 获取员工工作信息数据详细信息
     *
     * @param pkPsnjob 主键
     */
    @GetMapping("/{pkPsnjob}")
    public R<HdlPersonJobInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String pkPsnjob) {
        return R.ok(iHdlPersonJobInfoService.queryById(pkPsnjob));
    }

    /**
     * 新增员工工作信息数据
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlPersonJobInfoBo bo) {
        return toAjax(iHdlPersonJobInfoService.insertByBo(bo));
    }

    /**
     * 修改员工工作信息数据
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlPersonJobInfoBo bo) {
        return toAjax(iHdlPersonJobInfoService.updateByBo(bo));
    }

    /**
     * 删除员工工作信息数据
     *
     * @param pkPsnjobs 主键串
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pkPsnjobs}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] pkPsnjobs) {
        return toAjax(iHdlPersonJobInfoService.deleteWithValidByIds(Arrays.asList(pkPsnjobs), true));
    }

    /**
     * 查询远端员工工作信息列表
     */
    @PostMapping("/remote/list")
    public RemoteTableDataInfo listRemote(@RequestBody Map<String, Object> params) {
        try {
            PageQuery pageQuery = new PageQuery();
            int pageNum = params.get("pageNum") != null
                ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            int pageSize = params.get("pageSize") != null
                ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);

            Map<String, Object> queryParams = new HashMap<>();
            if (params.get("name") != null) queryParams.put("name", params.get("name"));
            if (params.get("code") != null) queryParams.put("code", params.get("code"));
            if (params.get("keyNumber") != null) queryParams.put("keyNumber", params.get("keyNumber"));
            if (params.get("searchValue") != null && !params.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", params.get("searchValue"));
            }
            queryParams.put("pageNum", pageNum - 1);
            queryParams.put("pageSize", pageSize);

            Map<String, Object> result = remoteDataService.queryRemotePersonJobInfos(queryParams);
            List<?> rows = (List<?>) result.get("rows");
            long total = result.get("total") != null
                ? Long.parseLong(result.get("total").toString()) : 0L;
            return RemoteTableDataInfo.build(rows, total, pageQuery);
        } catch (Exception e) {
            return RemoteTableDataInfo.build(null, 0L, new PageQuery());
        }
    }

    /**
     * 导出远端员工工作信息
     * 兼容两种前端路径：
     * - /mainData/personJobInfo/remote/export
     * - /mainData/personJobInfo/exportRemote
     */
    @PostMapping({"/remote/export", "/exportRemote"})
    public void exportRemote(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            Map<String, Object> queryParams = new HashMap<>(params);
            queryParams.put("pageNum", 0);
            queryParams.put("pageSize", 10000);
            byte[] data = remoteDataService.exportRemotePersonJobInfos(queryParams);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                "attachment;filename=remote_person_job_" + System.currentTimeMillis() + ".xlsx");
            try (OutputStream os = response.getOutputStream()) {
                os.write(data == null ? new byte[0] : data);
                os.flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 强制同步远程员工工作信息到本地
     */
    @PostMapping("/remote/forceSync")
    public R<Map<String, Object>> forceSyncRemote() {
        try {
            Map<String, Object> result = remoteDataService.forceSyncPersonJobInfos();
            if (isSyncSuccess(result)) {
                return R.ok(result);
            }
            String message = resolveSyncFailureMessage(result, "强制同步员工工作信息失败");
            return R.fail(message, result);
        } catch (Exception e) {
            return R.fail("强制同步员工工作信息失败: " + e.getMessage());
        }
    }

    private boolean isSyncSuccess(Map<String, Object> result) {
        if (result == null) {
            return false;
        }
        Object success = result.get("success");
        if (success == null) {
            return false;
        }
        if (success instanceof Boolean) {
            return (Boolean) success;
        }
        String normalized = String.valueOf(success).trim().toLowerCase();
        return "1".equals(normalized) || "true".equals(normalized);
    }

    private String resolveSyncFailureMessage(Map<String, Object> result, String fallback) {
        if (result == null) {
            return fallback;
        }
        Object message = result.get("message");
        if (message == null) {
            return fallback;
        }
        String text = String.valueOf(message).trim();
        return text.isEmpty() ? fallback : text;
    }
}
