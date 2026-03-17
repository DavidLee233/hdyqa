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
 * 主数据系统视图对象 hdl_organization_department
 *
 * @author aa
 * @date 2026-01-14
 */
@Data
@ExcelIgnoreUnannotated
public class HdlOrganizationDepartmentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门主键
     */
    @ExcelProperty(value = "部门主键")
    private String pkDept;

    /**
     * 手动过滤
     */
    @ExcelProperty(value = "手动过滤")
    private String manualFilter;

    /**
     * 标记是否为手动维护的虚拟部门
     */
    @ExcelProperty(value = "标记是否为手动维护的虚拟部门")
    private String virtualDeptFlag;

    /**
     * 标记是否有人
     */
    @ExcelProperty(value = "标记是否有人")
    private String flag;

    /**
     * 部门排序
     */
    @ExcelProperty(value = "部门排序")
    private String deptOrder;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    private String name;

    /**
     * 部门编码
     */
    @ExcelProperty(value = "部门编码")
    private String code;

    /**
     * 所属上级
     */
    @ExcelProperty(value = "所属上级")
    private String pkFatherOrder;

    /**
     * 所属组织
     */
    @ExcelProperty(value = "所属组织")
    private String pkOrg;

    /**
     * 启用状态（1未启用、2已启用、3已停用、默认为2）
     */
    @ExcelProperty(value = "启用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=未启用、2已启用、3已停用、默认为2")
    private String enableState;

    /**
     * 一级部门
     */
    @ExcelProperty(value = "一级部门")
    private String superiorDept;

    /**
     * 部门简称
     */
    @ExcelProperty(value = "部门简称")
    private String shortName;

    /**
     * 组织部门标识
     */
    @ExcelProperty(value = "组织部门标识")
    private String sign;

    /**
     * 互联网掩护名称
     */
    @ExcelProperty(value = "互联网掩护名称")
    private String internetName;

    /**
     * 旧NC部门主键
     */
    private String oldPkDept;

    /**
     * 旧NC部门编码
     */
    @ExcelProperty(value = "旧NC部门编码")
    private String oldCode;

    /**
     * 旧NC所属上级
     */
    @ExcelProperty(value = "旧NC所属上级")
    private String oldPkFatherOrg;

    /**
     * 创建者姓名
     */
    private String createBy;

    /**
     * 创建者工号
     */
    private String createId;

    /**
     * 创建者姓名
     */
    private String createTime;

    /**
     * 更新者工号
     */
    private String updateId;


}
