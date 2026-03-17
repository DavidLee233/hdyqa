package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 主数据系统Service接口
 *
 * @author aa
 * @date 2026-01-14
 */
public interface IHdlOrganizationDepartmentService {

    /**
     * 查询主数据系统
     */
    HdlOrganizationDepartmentVo queryById(String pkDept);

    /**
     * 查询主数据系统列表
     */
    TableDataInfo queryPageList(HdlOrganizationDepartmentBo bo, PageQuery pageQuery);

    /**
     * 新增主数据系统
     */
    Boolean insertByBo(HdlOrganizationDepartmentBo bo);

    /**
     * 修改主数据系统
     */
    Boolean updateByBo(HdlOrganizationDepartmentBo bo);

    /**
     * 校验并批量删除主数据系统信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 查询导出数据
     */
    List<HdlOrganizationDepartment> selectOrgDeptList(HdlOrganizationDepartment orgDept);
}
