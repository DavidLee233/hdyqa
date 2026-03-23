package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;
/**
 * @project npic
 * @description HdlOrganizationDepartmentMapper数据访问接口，负责组织部门主数据持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlOrganizationDepartmentMapper extends BaseMapperPlus<HdlOrganizationDepartmentMapper, HdlOrganizationDepartment, HdlOrganizationDepartmentVo> {
    /**
     * @description 执行selectOrgDeptList方法，完成组织部门主数据相关业务处理。
     * @params orgDept 组织部门实体对象
     *
      * @return List<HdlOrganizationDepartment> 组织部门列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public List<HdlOrganizationDepartment> selectOrgDeptList(HdlOrganizationDepartment orgDept);
}