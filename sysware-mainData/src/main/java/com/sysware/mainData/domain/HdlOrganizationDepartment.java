package com.sysware.mainData.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 主数据系统对象 hdl_organization_department
 *
 * @author aa
 * @date 2026-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_organization_department")
public class HdlOrganizationDepartment extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 部门主键
     */
    @TableId(value = "pk_dept")
    private String pkDept;
    /**
     * 手动过滤
     */
    private String manualFilter;
    /**
     * 标记是否为手动维护的虚拟部门
     */
    private String virtualDeptFlag;
    /**
     * 标记是否有人
     */
    private String flag;
    /**
     * 部门排序
     */
    private String deptOrder;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门编码
     */
    private String code;
    /**
     * 所属上级
     */
    private String pkFatherOrder;
    /**
     * 所属组织
     */
    private String pkOrg;
    /**
     * 启用状态（1未启用、2已启用、3已停用、默认为2）
     */
    private String enableState;
    /**
     * 一级部门
     */
    private String superiorDept;
    /**
     * 部门简称
     */
    private String shortName;
    /**
     * 组织部门标识
     */
    private String sign;
    /**
     * 互联网掩护名称
     */
    private String internetName;
    /**
     * 旧NC部门主键
     */
    private String oldPkDept;
    /**
     * 旧NC部门编码
     */
    private String oldCode;
    /**
     * 旧NC所属上级
     */
    private String oldPkFatherOrg;
    /**
     * 创建者工号
     */
    private String createId;
    /**
     * 更新者工号
     */
    private String updateId;

}
