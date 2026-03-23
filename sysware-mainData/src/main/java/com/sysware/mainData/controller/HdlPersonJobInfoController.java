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
 * @project npic
 * @description HdlPersonJobInfoController控制器，负责员工工作信息主数据相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
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
     * @description 查询员工工作信息主数据数据并处理为表格分页结构后返回前端展示。
     * @params bo 员工工作信息主数据分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlPersonJobInfoBo bo, PageQuery pageQuery) {
        return iHdlPersonJobInfoService.queryPageList(bo, pageQuery);
    }
    /**
     * @description 导出员工工作信息主数据数据并写入响应流供前端下载。
     * @params pji 员工工作信息实体对象
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlPersonJobInfo pji, HttpServletResponse response) {
        List<HdlPersonJobInfo> list = iHdlPersonJobInfoService.selectPJIList(pji);
        List<HdlPersonJobInfoVo> listVo = BeanUtil.copyToList(list, HdlPersonJobInfoVo.class);
        ExcelUtil.exportExcel(listVo, "员工工作信息数据", HdlPersonJobInfoVo.class, response);
    }
    /**
     * @description 根据主键获取员工工作信息主数据详情并返回给前端。
     * @params pkPsnjob 员工工作信息主键编码
     *
      * @return R<HdlPersonJobInfoVo> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/{pkPsnjob}")
    public R<HdlPersonJobInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable String pkPsnjob) {
        return R.ok(iHdlPersonJobInfoService.queryById(pkPsnjob));
    }
    /**
     * @description 新增员工工作信息主数据记录并完成参数校验后入库。
     * @params bo 员工工作信息主数据新增业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlPersonJobInfoBo bo) {
        return toAjax(iHdlPersonJobInfoService.insertByBo(bo));
    }
    /**
     * @description 编辑员工工作信息主数据记录并返回更新结果。
     * @params bo 员工工作信息主数据编辑业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlPersonJobInfoBo bo) {
        return toAjax(iHdlPersonJobInfoService.updateByBo(bo));
    }
    /**
     * @description 按主键集合删除或失效处理员工工作信息主数据记录。
     * @params pkPsnjobs 员工工作信息主键编码数组
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工工作信息数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pkPsnjobs}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] pkPsnjobs) {
        return toAjax(iHdlPersonJobInfoService.deleteWithValidByIds(Arrays.asList(pkPsnjobs), true));
    }
    /**
     * @description 调用远端接口分页查询员工工作信息主数据并适配前端分页结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return RemoteTableDataInfo 远端表格分页结果（包含远端数据列表与总条数）。
     * @author DavidLee233
     * @date 2026/3/20
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
     * @description 导出远端员工工作信息主数据查询结果并写入响应流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
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
     * @description 触发员工工作信息主数据远端到本地的强制同步并返回批次统计。
     * @params body 请求体参数集合（承载过滤条件、映射字段与同步配置）
     *
      * @return R<Map<String, Object>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/remote/forceSync")
    public R<Map<String, Object>> forceSyncRemote(@RequestBody(required = false) Map<String, Object> body) {
        try {
            String syncMode = resolveSyncMode(body);
            Map<String, Object> result = remoteDataService.forceSyncPersonJobInfos(syncMode, "manual");
            if (isSyncSuccess(result)) {
                return R.ok(result);
            }
            String message = resolveSyncFailureMessage(result, "强制同步员工工作信息失败");
            return R.fail(message, result);
        } catch (Exception e) {
            return R.fail("强制同步员工工作信息失败: " + e.getMessage());
        }
    }

    /**
     * @description 判定同步结果是否成功，用于统一接口返回状态。
     * @params result 执行结果对象（包含成功标记、统计数据与错误信息）
     *
      * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
     * @author DavidLee233
     * @date 2026/3/20
     */
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

    /**
     * @description 解析同步失败原因并转换为前端可展示错误信息。
     * @params result 执行结果对象（包含成功标记、统计数据与错误信息）
     * @params fallback 失败兜底提示文案
     *
      * @return String 面向调用方的业务提示信息。
     * @author DavidLee233
     * @date 2026/3/20
     */
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

    /**
     * @description 解析请求中的同步模式参数并标准化为全量或增量。
     * @params body 请求体参数集合（承载过滤条件、映射字段与同步配置）
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveSyncMode(Map<String, Object> body) {
        if (body == null || body.get("syncMode") == null) {
            return "full";
        }
        String value = String.valueOf(body.get("syncMode")).trim().toLowerCase();
        if ("incremental".equals(value) || "auto".equals(value)) {
            return value;
        }
        return "full";
    }
}