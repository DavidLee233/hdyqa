package com.sysware.system.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeptUserTreeVo {
    private String id;   // 部门或员工主键
    private String label;  // 部门或员工名称
    private String type; // dept || user
    private String userName;   // 工号
    private String deptName;
    private String deptId;
    private List<DeptUserTreeVo> children;
}
