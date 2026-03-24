package com.sysware.mainData.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.mainData.controller.support.MainDataRemoteControllerSupport;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.service.IHdlPersonBasicInfoService;
import com.sysware.mainData.service.IRemoteDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @project npic
 * @description HdlPersonBasicInfoController控制器，负责员工基本信息主数据相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Validated
@RequiredArgsConstructor
@RestController
@SaCheckLogin
@RequestMapping("/mainData/personBasicInfo")
public class HdlPersonBasicInfoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(HdlPersonBasicInfoController.class);

    private final IHdlPersonBasicInfoService iHdlPersonBasicInfoService;

    @Autowired
    private IRemoteDataService remoteDataService;
    /**
     * @description 查询员工基本信息主数据数据并处理为表格分页结构后返回前端展示。
     * @params bo 员工基本信息主数据分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlPersonBasicInfoBo bo, PageQuery pageQuery) {
        return iHdlPersonBasicInfoService.queryPageList(bo, pageQuery);
    }
    /**
     * @description 导出员工基本信息主数据数据并写入响应流供前端下载。
     * @params pbi 员工基本信息实体对象
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlPersonBasicInfo pbi, HttpServletResponse response) {
        List<HdlPersonBasicInfo> list = iHdlPersonBasicInfoService.selectPBIList(pbi);
        List<HdlPersonBasicInfoVo> listVo = BeanUtil.copyToList(list, HdlPersonBasicInfoVo.class);
        ExcelUtil.exportExcel(listVo, "员工基本信息数据", HdlPersonBasicInfoVo.class, response);
    }
    /**
     * @description 根据主键获取员工基本信息主数据详情并返回给前端。
     * @params pkPsndoc 员工基本信息主键编码
     *
      * @return R<HdlPersonBasicInfoVo> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/{pkPsndoc}")
    public R<HdlPersonBasicInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                           @PathVariable String pkPsndoc) {
        return R.ok(iHdlPersonBasicInfoService.queryById(pkPsndoc));
    }
    /**
     * @description 新增员工基本信息主数据记录并完成参数校验后入库。
     * @params bo 员工基本信息主数据新增业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlPersonBasicInfoBo bo) {
        return toAjax(iHdlPersonBasicInfoService.insertByBo(bo));
    }
    /**
     * @description 编辑员工基本信息主数据记录并返回更新结果。
     * @params bo 员工基本信息主数据编辑业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlPersonBasicInfoBo bo) {
        return toAjax(iHdlPersonBasicInfoService.updateByBo(bo));
    }
    /**
     * @description 按主键集合删除或失效处理员工基本信息主数据记录。
     * @params pkPsndocs 员工基本信息主键编码数组
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pkPsndocs}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] pkPsndocs) {
        return toAjax(iHdlPersonBasicInfoService.deleteWithValidByIds(Arrays.asList(pkPsndocs), true));
    }
    /**
     * @description 调用远端接口分页查询员工基本信息主数据并适配前端分页结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return RemoteTableDataInfo 远端表格分页结果（包含远端数据列表与总条数）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/remote/list")
    public RemoteTableDataInfo listRemote(@RequestBody Map<String, Object> params) {
        PageQuery pageQuery = MainDataRemoteControllerSupport.resolvePageQuery(params, 10);
        Map<String, Object> safeParams = params == null ? new HashMap<>() : params;
        try {
            Map<String, Object> queryParams = new HashMap<>();
            if (safeParams.get("name") != null) queryParams.put("name", safeParams.get("name"));
            if (safeParams.get("code") != null) queryParams.put("code", safeParams.get("code"));
            if (safeParams.get("mobile") != null) queryParams.put("mobile", safeParams.get("mobile"));
            if (safeParams.get("searchValue") != null && !safeParams.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", safeParams.get("searchValue"));
            }
            queryParams.put("pageNum", pageQuery.getPageNum() - 1);
            queryParams.put("pageSize", pageQuery.getPageSize());
            Map<String, Object> result = remoteDataService.queryRemotePersonBasicInfos(queryParams);
            return MainDataRemoteControllerSupport.buildRemoteSuccess(result, pageQuery);
        } catch (Exception e) {
            logger.error("查询远端员工基本信息失败，参数={}", safeParams, e);
            return MainDataRemoteControllerSupport.buildRemoteError(pageQuery, "查询远端员工基本信息失败", e);
        }
    }
    /**
     * @description 导出远端员工基本信息主数据查询结果并写入响应流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping({"/remote/export", "/exportRemote"})
    public void exportRemote(@RequestParam(required = false) Map<String, String> params,
                             @RequestBody(required = false) Map<String, Object> body,
                             HttpServletResponse response) {
        try {
            Map<String, Object> queryParams = MainDataRemoteControllerSupport.mergeExportParams(params, body, 0, 10000);
            byte[] data = remoteDataService.exportRemotePersonBasicInfos(queryParams);
            MainDataRemoteControllerSupport.writeExcelResponse(response, data, "remote_person_basic_info");
        } catch (Exception e) {
            logger.error("导出远端员工基本信息失败，参数={}，请求体={}", params, body, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * @description 触发员工基本信息主数据远端到本地的强制同步并返回批次统计。
     * @params body 请求体参数集合（承载过滤条件、映射字段与同步配置）
     *
      * @return R<Map<String, Object>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/remote/forceSync")
    public R<Map<String, Object>> forceSyncRemote(@RequestBody(required = false) Map<String, Object> body) {
        try {
            String syncMode = MainDataRemoteControllerSupport.resolveSyncMode(body);
            Map<String, Object> result = remoteDataService.forceSyncPersonBasicInfos(syncMode, "manual");
            if (MainDataRemoteControllerSupport.isSyncSuccess(result)) {
                return R.ok(result);
            }
            String message = MainDataRemoteControllerSupport.resolveSyncFailureMessage(result, "强制同步员工基本信息失败");
            return R.fail(message, result);
        } catch (Exception e) {
            return R.fail("强制同步员工基本信息失败: " + e.getMessage());
        }
    }
}
