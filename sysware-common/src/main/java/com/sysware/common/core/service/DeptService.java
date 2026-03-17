package com.sysware.common.core.service;

/**
 * 通用 部门服务
 *
 * @author
 */
public interface DeptService {

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    String selectDeptNameByIds(String deptIds);

    /**
     * 通过部门名称查询部门ID
     *
     * @param deptNames 部门名称串逗号分隔
     * @return 部门ID串逗号分隔
     */
    String selectDeptIdByNames(String deptNames);

}
