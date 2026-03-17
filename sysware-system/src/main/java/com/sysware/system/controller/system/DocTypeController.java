package com.sysware.web.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sysware.common.annotation.Log;
import com.sysware.common.annotation.RepeatSubmit;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.bo.DocTypeBo;
import com.sysware.system.domain.vo.DocTypeVo;
import com.sysware.system.service.IDocTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 文件类型
 *
 * @author aa
 * @date 2023-09-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/docType")
public class DocTypeController extends BaseController {

    private final IDocTypeService iDocTypeService;

    /**
     * 查询文件类型列表
     */
    @GetMapping("/list")
    public TableDataInfo list(DocTypeBo bo, PageQuery pageQuery) {
        return iDocTypeService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出文件类型列表
     */
    @SaCheckPermission("system:docType:export")
    @Log(title = "文件类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DocTypeBo bo, HttpServletResponse response) {
        List<DocTypeVo> list = iDocTypeService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件类型", DocTypeVo.class, response);
    }

    /**
     * 获取文件类型详细信息
     *
     * @param createBy 主键
     */
    @GetMapping("/{createBy}")
    public R<DocTypeVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String createBy) {
        return R.ok(iDocTypeService.queryById(createBy));
    }

    /**
     * 新增文件类型
     */
    @SaCheckPermission("system:docType:add")
    @Log(title = "文件类型", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DocTypeBo bo) {
        return toAjax(iDocTypeService.insertByBo(bo));
    }

    /**
     * 修改文件类型
     */
    @SaCheckPermission("system:docType:edit")
    @Log(title = "文件类型", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DocTypeBo bo) {
        return toAjax(iDocTypeService.updateByBo(bo));
    }

    /**
     * 删除文件类型
     *
     * @param createBys 主键串
     */
    @SaCheckPermission("system:docType:remove")
    @Log(title = "文件类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{createBys}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] createBys) {
        return toAjax(iDocTypeService.deleteWithValidByIds(Arrays.asList(createBys), true));
    }
}
