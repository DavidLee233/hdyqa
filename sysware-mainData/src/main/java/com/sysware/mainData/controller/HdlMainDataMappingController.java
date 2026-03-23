package com.sysware.mainData.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;
import com.sysware.mainData.service.IHdlMainDataMappingService;
import com.sysware.common.core.page.TableDataInfo;
/**
 * @project npic
 * @description HdlMainDataMappingController控制器，负责主数据映射相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mainData/mainDataMapping")
public class HdlMainDataMappingController extends BaseController {

    private final IHdlMainDataMappingService iHdlMainDataMappingService;
    /**
     * @description 查询主数据映射数据并处理为表格分页结构后返回前端展示。
     * @params bo 主数据映射分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlMainDataMappingBo bo, PageQuery pageQuery) {
        return iHdlMainDataMappingService.queryPageList(bo, pageQuery);
    }
    /**
     * @description 导出主数据映射数据并写入响应流供前端下载。
     * @params mdm 主数据映射实体对象
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlMainDataMapping mdm, HttpServletResponse response) {

    }
    /**
     * @description 根据主键获取主数据映射详情并返回给前端。
     * @params mapId 主数据映射主键ID
     *
      * @return R<HdlMainDataMappingVo> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/{mapId}")
    public R<HdlMainDataMappingVo> getInfo(@NotNull(message = "主键不能为空")
                                           @PathVariable Long mapId) {
        return R.ok(iHdlMainDataMappingService.queryById(mapId));
    }
    /**
     * @description 新增主数据映射记录并完成参数校验后入库。
     * @params bo 主数据映射新增业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlMainDataMappingBo bo) {
        return toAjax(iHdlMainDataMappingService.insertByBo(bo));
    }
    /**
     * @description 编辑主数据映射记录并返回更新结果。
     * @params bo 主数据映射编辑业务请求对象
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlMainDataMappingBo bo) {
        return toAjax(iHdlMainDataMappingService.updateByBo(bo));
    }
    /**
     * @description 按主键集合删除或失效处理主数据映射记录。
     * @params mapIds 主数据映射主键ID数组
     *
      * @return R<Void> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mapIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] mapIds) {
        return toAjax(iHdlMainDataMappingService.deleteWithValidByIds(Arrays.asList(mapIds), true));
    }
    /**
     * @description 按类型查询主数据映射配置并返回字段映射列表。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return R<List<HdlMainDataMappingVo>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/getByType")
    public R<List<HdlMainDataMappingVo>> getByType(String type) {
        List<HdlMainDataMappingVo> list = iHdlMainDataMappingService.selectMainDataMappingByType(type);
        return R.ok(list);
    }
}