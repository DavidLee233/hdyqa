package com.sysware.mainData.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;

/**
 * @project npic
 * @description organizationDepartmentAction查询构建工具类，负责组装组织部门主数据过滤条件表达式。
 * @author DavidLee233
 * @date 2026/3/20
 */
public class organizationDepartmentAction {
    /**
     * @description 构建组织部门主数据处理所需的中间对象或条件。
     * @params bo 组织部门主数据业务请求对象（包含查询与变更字段）
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return LambdaQueryWrapper<HdlOrganizationDepartment> 组织部门主数据查询条件构造器，用于后续数据库过滤与排序。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public static LambdaQueryWrapper<HdlOrganizationDepartment> buildOrgQueryWrapper(HdlOrganizationDepartmentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlOrganizationDepartment> lqw = Wrappers.lambdaQuery();
        // 首先 进行局部精确匹配查询
        if (bo != null) {
            // 模糊匹配所有搜索字段
            if (StrUtil.isNotBlank(bo.getName())) lqw.like(HdlOrganizationDepartment::getName, bo.getName());
            if (StrUtil.isNotBlank(bo.getCode())) lqw.like(HdlOrganizationDepartment::getCode, bo.getCode());
            if (StrUtil.isNotBlank(bo.getShortName())) lqw.like(HdlOrganizationDepartment::getShortName, bo.getShortName());
            if (StrUtil.isNotBlank(bo.getInternetName())) lqw.like(HdlOrganizationDepartment::getInternetName, bo.getInternetName());
            if (StrUtil.isNotBlank(bo.getSign())) lqw.like(HdlOrganizationDepartment::getSign, bo.getSign());
        }
        // 其次 进行全局模糊查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(HdlOrganizationDepartment::getManualFilter, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getVirtualDeptFlag, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getFlag, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getDeptOrder, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getName, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getCode, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getPkFatherOrder, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getPkOrg, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getEnableState, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getSuperiorDept, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getShortName, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getSign, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getInternetName, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getOldPkDept, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getOldCode, bo.getSearchValue())
                        .or()
                        .like(HdlOrganizationDepartment::getOldPkFatherOrg, bo.getSearchValue());
            });
        }
        return lqw;
    }
}