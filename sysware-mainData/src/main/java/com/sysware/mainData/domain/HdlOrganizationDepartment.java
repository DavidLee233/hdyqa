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
 * @project npic
 * @description HdlOrganizationDepartment领域实体，描述组织部门主数据核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_organization_department")
public class HdlOrganizationDepartment extends BaseEntity {

    private static final long serialVersionUID=1L;
    @TableId(value = "pk_dept")
    private String pkDept;
    private String manualFilter;
    private String virtualDeptFlag;
    private String flag;
    private String deptOrder;
    private String name;
    private String code;
    private String pkFatherOrder;
    private String pkOrg;
    private String enableState;
    private String superiorDept;
    private String shortName;
    private String sign;
    private String internetName;
    private String oldPkDept;
    private String oldCode;
    private String oldPkFatherOrg;
    private String createId;
    private String updateId;

}