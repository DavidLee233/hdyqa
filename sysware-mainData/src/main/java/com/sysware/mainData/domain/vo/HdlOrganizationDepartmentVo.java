package com.sysware.mainData.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @project npic
 * @description HdlOrganizationDepartmentVo业务出参对象，封装组织部门主数据返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@ExcelIgnoreUnannotated
public class HdlOrganizationDepartmentVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "部门主键")
    private String pkDept;
    @ExcelProperty(value = "手动过滤")
    private String manualFilter;
    @ExcelProperty(value = "标记是否为手动维护的虚拟部门")
    private String virtualDeptFlag;
    @ExcelProperty(value = "标记是否有人")
    private String flag;
    @ExcelProperty(value = "部门排序")
    private String deptOrder;
    @ExcelProperty(value = "部门名称")
    private String name;
    @ExcelProperty(value = "部门编码")
    private String code;
    @ExcelProperty(value = "所属上级")
    private String pkFatherOrder;
    @ExcelProperty(value = "所属组织")
    private String pkOrg;
    @ExcelProperty(value = "启用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=未启用、2已启用、3已停用、默认为2")
    private String enableState;
    @ExcelProperty(value = "一级部门")
    private String superiorDept;
    @ExcelProperty(value = "部门简称")
    private String shortName;
    @ExcelProperty(value = "组织部门标识")
    private String sign;
    @ExcelProperty(value = "互联网掩护名称")
    private String internetName;
    private String oldPkDept;
    @ExcelProperty(value = "旧NC部门编码")
    private String oldCode;
    @ExcelProperty(value = "旧NC所属上级")
    private String oldPkFatherOrg;
    private String createBy;
    private String createId;
    private String createTime;
    private String updateId;


}