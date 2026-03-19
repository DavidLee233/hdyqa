package com.sysware.mainData.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
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
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.mainData.service.IHdlPersonBasicInfoService;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 员工基本信息数据
 *
 * @author aa
 * @date 2026-01-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mainData/personBasicInfo")
public class HdlPersonBasicInfoController extends BaseController {

    private final IHdlPersonBasicInfoService iHdlPersonBasicInfoService;
    @Autowired
    private IRemoteDataService remoteDataService;

    /**
     * 查询员工基本信息数据列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlPersonBasicInfoBo bo, PageQuery pageQuery) {
        return iHdlPersonBasicInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出员工基本信息数据列表
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlPersonBasicInfo pbi, HttpServletResponse response) {
        List<HdlPersonBasicInfo> list = iHdlPersonBasicInfoService.selectPBIList(pbi);
        List<HdlPersonBasicInfoVo> listVo = BeanUtil.copyToList(list, HdlPersonBasicInfoVo.class);
        ExcelUtil.exportExcel(listVo, "员工基本信息数据", HdlPersonBasicInfoVo.class, response);
    }

    /**
     * 获取员工基本信息数据详细信息
     *
     * @param pkPsndoc 主键
     */
    @GetMapping("/{pkPsndoc}")
    public R<HdlPersonBasicInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String pkPsndoc) {
        return R.ok(iHdlPersonBasicInfoService.queryById(pkPsndoc));
    }

    /**
     * 新增员工基本信息数据
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlPersonBasicInfoBo bo) {
        return toAjax(iHdlPersonBasicInfoService.insertByBo(bo));
    }

    /**
     * 修改员工基本信息数据
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlPersonBasicInfoBo bo) {
        return toAjax(iHdlPersonBasicInfoService.updateByBo(bo));
    }

    /**
     * 删除员工基本信息数据
     *
     * @param pkPsndocs 主键串
     */
    @Log(title = "员工基本信息数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pkPsndocs}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] pkPsndocs) {
        return toAjax(iHdlPersonBasicInfoService.deleteWithValidByIds(Arrays.asList(pkPsndocs), true));
    }

    /**
     * 查询远端员工基本信息列表
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
            if (params.get("mobile") != null) queryParams.put("mobile", params.get("mobile"));
            if (params.get("searchValue") != null && !params.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", params.get("searchValue"));
            }
            queryParams.put("pageNum", pageNum - 1);
            queryParams.put("pageSize", pageSize);

            Map<String, Object> result = remoteDataService.queryRemotePersonBasicInfos(queryParams);
            List<?> rows = (List<?>) result.get("rows");
            long total = result.get("total") != null
                ? Long.parseLong(result.get("total").toString()) : 0L;
            return RemoteTableDataInfo.build(rows, total, pageQuery);
        } catch (Exception e) {
            return RemoteTableDataInfo.build(null, 0L, new PageQuery());
        }
    }

    /**
     * 导出远端员工基本信息
     * 兼容两种前端路径：
     * - /mainData/personBasicInfo/remote/export
     * - /mainData/personBasicInfo/exportRemote
     */
    @PostMapping({"/remote/export", "/exportRemote"})
    public void exportRemote(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            Map<String, Object> queryParams = new HashMap<>(params);
            queryParams.put("pageNum", 0);
            queryParams.put("pageSize", 10000);
            byte[] data = remoteDataService.exportRemotePersonBasicInfos(queryParams);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                "attachment;filename=remote_person_basic_" + System.currentTimeMillis() + ".xlsx");
            try (OutputStream os = response.getOutputStream()) {
                os.write(data == null ? new byte[0] : data);
                os.flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
