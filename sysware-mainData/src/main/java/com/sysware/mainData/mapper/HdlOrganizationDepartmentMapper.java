package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 主数据系统Mapper接口
 *
 * @author aa
 * @date 2026-01-14
 */
public interface HdlOrganizationDepartmentMapper extends BaseMapperPlus<HdlOrganizationDepartmentMapper, HdlOrganizationDepartment, HdlOrganizationDepartmentVo> {

    /**
     * 根据条件分页查询列表
     *
     * @param orgDept 用户信息
     * @return 信息集合信息
     */
    public List<HdlOrganizationDepartment> selectOrgDeptList(HdlOrganizationDepartment orgDept);
}
