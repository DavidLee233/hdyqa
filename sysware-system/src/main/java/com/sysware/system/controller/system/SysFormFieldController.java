package com.sysware.web.controller.system;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.system.domain.vo.SysFormFieldVo;
import io.swagger.annotations.ApiModelProperty;
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
import com.sysware.common.enums.BusinessType;
import com.sysware.system.domain.bo.SysFormFieldQueryBo;
import com.sysware.system.domain.bo.SysFormFieldAddBo;
import com.sysware.system.domain.bo.SysFormFieldEditBo;
import com.sysware.system.service.ISysFormFieldService;
import com.sysware.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 表单配置Controller
 *
 * @author zzr
 * @date 2023-04-12
 */
@Validated
@Api(value = "表单配置控制器", tags = {"表单配置管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/system/formField")
public class SysFormFieldController extends BaseController {

    private final ISysFormFieldService iSysFormFieldService;

    /**
     * 查询表单配置列表
     */
    @ApiOperation("查询表单配置列表")
    @GetMapping("/list")
    public TableDataInfo list(@Validated SysFormFieldQueryBo bo, PageQuery pageQuery) {
        List sysFormFieldVos = iSysFormFieldService.queryList(bo);
        return TableDataInfo.build(sysFormFieldVos);
    }

    /**
     * 导出表单配置列表
     */
    @ApiOperation("导出表单配置列表")
    @SaCheckPermission("system:formField:export")
    @Log(title = "表单配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated SysFormFieldQueryBo bo, HttpServletResponse response) {
       /* List<SysFormFieldVo> list = iSysFormFieldService.queryList(bo);
        ExcelUtil.exportExcel(list,"表单配置",SysFormFieldVo.class,response);*/

    }

    /**
     * 获取表单配置详细信息
     */
    @ApiOperation("获取表单配置详细信息")
    @GetMapping("/{fieldId}")
    public R<SysFormFieldVo> getInfo(@NotNull(message = "主键不能为空")
                                                  @PathVariable("fieldId") String fieldId) {

        Field[] declaredFields = SysFormFieldVo.class.getDeclaredFields();
        SysFormFieldVo sysFormFieldVo = iSysFormFieldService.queryById(fieldId);
        for (Field field : declaredFields){
            field.getAnnotation(ExcelProperty.class);
        }

        return R.ok(iSysFormFieldService.queryById(fieldId));
    }


    /**
     * 获取表单配置详细信息
     */
    @ApiOperation("获取表单配置详细信息")
    @GetMapping("/getDefaultValue")
    public R<Map<String,Object>> getDefaultValue(@Validated SysFormFieldQueryBo bo) {
        return R.ok(iSysFormFieldService.getDefaultValue(bo));
    }

    /**
     * 获取表单配置详细信息
     */
    @ApiOperation("获取表单配置详细信息")
    @GetMapping("/getDefaultFields")
    public R<Map<String,Object>> getDefaultFields() {

        Field[] declaredFields = SysFormFieldVo.class.getDeclaredFields();
        Map<String,Object> map = new HashMap<String,Object>();
        for (Field field : declaredFields){
            if(field.getAnnotation(ApiModelProperty.class)!=null)
                map.put(field.getName(),field.getAnnotation(ApiModelProperty.class).value());
        }


        return R.ok(map);
    }

    /**
     * 新增表单配置
     */
    @ApiOperation("新增表单配置")
    @SaCheckPermission("system:formField:add")
    @RepeatSubmit
    @PostMapping()
    public R add(@Validated @RequestBody SysFormFieldAddBo bo) {
        return iSysFormFieldService.insertByAddBo(bo);
    }

    /**
     * 修改表单配置
     */
    @ApiOperation("修改表单配置")
    @SaCheckPermission("system:formField:edit")
    @Log(title = "表单配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody SysFormFieldEditBo bo) {
        return toAjax(iSysFormFieldService.updateByEditBo(bo) ? 1 : 0);
    }

    /**
     * 删除表单配置
     */
    @ApiOperation("删除表单配置")
    @SaCheckPermission("system:formField:remove")
    @Log(title = "表单配置" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{fieldIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                                       @PathVariable String[] fieldIds) {
        return toAjax(iSysFormFieldService.deleteWithValidByIds(Arrays.asList(fieldIds), true) ? 1 : 0);
    }


    /**
     * 查询项表单字段列表
     */
    @ApiOperation("查询项表单字段列表")
    @GetMapping("/selectFormFieldList")
    public TableDataInfo<Map> selectFormFieldList(@Validated SysFormFieldQueryBo bo, PageQuery pageQuery) {
        return iSysFormFieldService.queryPageList(bo,pageQuery);
    }

    @GetMapping("/selectFormFieldByFieldId/{fieldId}")
    public TableDataInfo selectFormFieldByFieldId(@PathVariable String fieldId){
        return iSysFormFieldService.selectFormFieldByFieldId(fieldId);
    }


    /**
     * 复制表单字段
     */
    @ApiOperation("复制表单字段")
    @SaCheckPermission("system:formField:add")
    @Log(title = "表单配置", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/copyFormField")
    public R<Void> copyFormField(@RequestBody SysFormFieldAddBo bo) {
        return toAjax(iSysFormFieldService.copyFormField(bo));
    }

}
