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
 * 主数据字段映射
 *
 * @author aa
 * @date 2026-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mainData/mainDataMapping")
public class HdlMainDataMappingController extends BaseController {

    private final IHdlMainDataMappingService iHdlMainDataMappingService;

    /**
     * 查询主数据字段映射列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlMainDataMappingBo bo, PageQuery pageQuery) {
        return iHdlMainDataMappingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出主数据字段映射列表
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HdlMainDataMapping mdm, HttpServletResponse response) {

    }

    /**
     * 获取主数据字段映射详细信息
     *
     * @param mapId 主键
     */
    @GetMapping("/{mapId}")
    public R<HdlMainDataMappingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long mapId) {
        return R.ok(iHdlMainDataMappingService.queryById(mapId));
    }

    /**
     * 新增主数据字段映射
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HdlMainDataMappingBo bo) {
        return toAjax(iHdlMainDataMappingService.insertByBo(bo));
    }

    /**
     * 修改主数据字段映射
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HdlMainDataMappingBo bo) {
        return toAjax(iHdlMainDataMappingService.updateByBo(bo));
    }

    /**
     * 删除主数据字段映射
     *
     * @param mapIds 主键串
     */
    @Log(title = "主数据字段映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mapIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] mapIds) {
        return toAjax(iHdlMainDataMappingService.deleteWithValidByIds(Arrays.asList(mapIds), true));
    }

    /**
     * 根据类型查询字段映射列表
     */
    @GetMapping("/getByType")
    public R<List<HdlMainDataMappingVo>> getByType(String type) {
        List<HdlMainDataMappingVo> list = iHdlMainDataMappingService.selectMainDataMappingByType(type);
        return R.ok(list);
    }
}
