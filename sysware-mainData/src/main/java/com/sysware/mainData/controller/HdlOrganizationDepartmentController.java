package com.sysware.mainData.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.service.IRemoteDataService;
import com.sysware.system.domain.vo.SysUserExportVo;
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
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;
import com.sysware.mainData.service.IHdlOrganizationDepartmentService;
import com.sysware.common.core.page.TableDataInfo;

/**
 * 主数据系统
 *
 * @author aa
 * @date 2026-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mainData/organizationDepartment")
public class HdlOrganizationDepartmentController extends BaseController {

    private final IHdlOrganizationDepartmentService iHdlOrganizationDepartmentService;

    @Autowired
    private IRemoteDataService remoteDataService;

    /**
     * 查询主数据系统列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlOrganizationDepartmentBo bo, PageQuery pageQuery) {
        return iHdlOrganizationDepartmentService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出主数据系统列表
     */
    @Log(title = "主数据系统", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlOrganizationDepartment orgDept, HttpServletResponse response) {
        List<HdlOrganizationDepartment> list = iHdlOrganizationDepartmentService.selectOrgDeptList(orgDept);
        List<HdlOrganizationDepartmentVo> listVo = BeanUtil.copyToList(list, HdlOrganizationDepartmentVo.class);
        ExcelUtil.exportExcel(listVo, "组织部门数据", HdlOrganizationDepartmentVo.class, response);
    }

    /**
     * 获取主数据系统详细信息
     *
     * @param pkDept 主键
     */
    @GetMapping("/{pkDept}")
    public R<HdlOrganizationDepartmentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String pkDept) {
        return R.ok(iHdlOrganizationDepartmentService.queryById(pkDept));
    }

    /**
     * 新增主数据系统
     */
    @Log(title = "主数据系统", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlOrganizationDepartmentBo bo) {
        return toAjax(iHdlOrganizationDepartmentService.insertByBo(bo));
    }

    /**
     * 修改主数据系统
     */
    @Log(title = "主数据系统", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlOrganizationDepartmentBo bo) {
        return toAjax(iHdlOrganizationDepartmentService.updateByBo(bo));
    }

    /**
     * 删除主数据系统
     *
     * @param pkDepts 主键串
     */
    @Log(title = "主数据系统", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pkDepts}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] pkDepts) {
        return toAjax(iHdlOrganizationDepartmentService.deleteWithValidByIds(Arrays.asList(pkDepts), true));
    }

    /**
     * 查询远程组织部门列表
     * 适配您当前的分页插件版本
     */
    @PostMapping("/remote/list")
    public RemoteTableDataInfo listRemote(@RequestBody Map<String, Object> params) {
        try {
            // 1. 处理分页参数
            PageQuery pageQuery = new PageQuery();

            // 从请求参数中获取分页信息
            int pageNum = params.get("pageNum") != null ?
                    Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ?
                    Integer.parseInt(params.get("pageSize").toString()) : 10;

            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);

            // 2. 处理搜索条件
            Map<String, Object> queryParams = new HashMap<>();

            // 复制基础查询参数
            if (params.get("name") != null) queryParams.put("name", params.get("name"));
            if (params.get("code") != null) queryParams.put("code", params.get("code"));
            if (params.get("shortName") != null) queryParams.put("shortName", params.get("shortName"));
            if (params.get("internetName") != null) queryParams.put("internetName", params.get("internetName"));
            if (params.get("sign") != null) queryParams.put("sign", params.get("sign"));

            // 处理全局搜索
            if (params.get("searchValue") != null && !params.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", params.get("searchValue"));
            }

            // 3. 添加分页参数（远程接口页码从0开始）
            queryParams.put("pageNum", pageNum - 1); // 转换为远程分页（从0开始）
            queryParams.put("pageSize", pageSize);

            // 4. 调用远程服务查询数据
            Map<String, Object> result = remoteDataService.queryRemoteDepartments(queryParams);

            // 5. 提取数据并构建TableDataInfo
            List<?> rows = (List<?>) result.get("rows");
            long total = result.get("total") != null ?
                    Long.parseLong(result.get("total").toString()) : 0L;

            // 6. 使用您系统标准的RemoteTableDataInfo.build方法
            return RemoteTableDataInfo.build(rows, total, pageQuery);

        } catch (Exception e) {
            // 返回空数据而不是抛出异常
            return RemoteTableDataInfo.build(null, 0L, new PageQuery());
        }
    }
}
