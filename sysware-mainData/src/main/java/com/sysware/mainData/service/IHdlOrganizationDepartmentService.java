package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
/**
 * @project npic
 * @description IHdlOrganizationDepartmentService服务接口，定义组织部门主数据能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IHdlOrganizationDepartmentService {
/**
 * @description 根据主键查询单条组织部门主数据详情信息。
 * @params pkDept 组织部门主键编码
 *
  * @return HdlOrganizationDepartmentVo 组织部门展示对象，用于前端详情或列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
HdlOrganizationDepartmentVo queryById(String pkDept);
/**
 * @description 按筛选条件分页查询组织部门主数据并封装为表格数据返回。
 * @params bo 组织部门主数据分页查询条件业务对象
 * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
 *
  * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
TableDataInfo queryPageList(HdlOrganizationDepartmentBo bo, PageQuery pageQuery);
/**
 * @description 将业务对象转换为实体并新增组织部门主数据记录。
 * @params bo 组织部门主数据新增业务请求对象
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean insertByBo(HdlOrganizationDepartmentBo bo);
/**
 * @description 按业务对象更新组织部门主数据记录并校验关键字段。
 * @params bo 组织部门主数据编辑业务请求对象
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean updateByBo(HdlOrganizationDepartmentBo bo);
/**
 * @description 按业务校验规则批量删除组织部门主数据记录。
 * @params ids 主键ID集合
 * @params isValid 数据有效标记（0无效、1有效）
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
/**
 * @description 执行selectOrgDeptList方法，完成组织部门主数据相关业务处理。
 * @params orgDept 组织部门实体对象
 *
  * @return List<HdlOrganizationDepartment> 组织部门列表结果，用于批量处理或前端展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
List<HdlOrganizationDepartment> selectOrgDeptList(HdlOrganizationDepartment orgDept);
}