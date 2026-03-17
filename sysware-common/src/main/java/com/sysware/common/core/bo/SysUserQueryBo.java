package com.sysware.common.core.bo;

import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserQueryBo extends BaseEntity {




    private String connector;

    private String deptId;
    private String roleId;
    private String securityId;
    private String projectId;

    private int securityValue;

    private String deptName;
    private String roleName;
    private String roleKey;
    private String securityName;




}
