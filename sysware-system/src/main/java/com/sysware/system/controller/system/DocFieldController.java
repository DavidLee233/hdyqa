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
import com.sysware.system.domain.bo.DocFieldBo;
import com.sysware.system.domain.vo.DocFieldVo;
import com.sysware.system.service.IDocFieldService;
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
 * 文档著录字段
 *
 * @author aa
 * @date 2023-09-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/docField")
public class DocFieldController extends BaseController {

    private final IDocFieldService iDocFieldService;

    /**
     * 查询文档著录字段列表
     */
    @GetMapping("/list")
    public TableDataInfo list(DocFieldBo bo, PageQuery pageQuery) {
        return iDocFieldService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询需要导入字段字段列表
     */
    @GetMapping("/importFieldList")
    public R<List> importFieldList(DocFieldBo bo) {
        return R.ok(iDocFieldService.queryImportFieldList(bo));
    }

    /**
     * 导出文档著录字段列表
     */
    @SaCheckPermission("system:docField:export")
    @Log(title = "文档著录字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DocFieldBo bo, HttpServletResponse response) {
        
    }

    /**
     * 获取文档著录字段详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<DocFieldVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(iDocFieldService.queryById(id));
    }

    /**
     * 新增文档著录字段
     */
    @SaCheckPermission("system:docField:add")
    @RepeatSubmit()
    @PostMapping()
    public R add(@RequestBody List<DocFieldBo> list) {
        return iDocFieldService.insertByBo(list);
    }

    /**
     * 修改文档著录字段
     */
    @SaCheckPermission("system:docField:edit")
    @RepeatSubmit()
    @PutMapping()
    public R edit(@Validated(EditGroup.class) @RequestBody DocFieldBo bo) {
        return iDocFieldService.updateByBo(bo);
    }

    /**
     * 删除文档著录字段
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:docField:remove")

    @DeleteMapping("/{ids}")
    public R remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return iDocFieldService.deleteWithValidByIds(Arrays.asList(ids), true);
    }
}
