package com.sysware.mainData.domain.bo;

import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;
/**
 * @project npic
 * @description HdlOrganizationDepartmentBo业务入参对象，封装组织部门主数据查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlOrganizationDepartmentBo extends BaseEntity {
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