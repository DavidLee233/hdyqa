package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
/**
 * @project npic
 * @description IHdlPersonBasicInfoService服务接口，定义员工基本信息主数据能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IHdlPersonBasicInfoService {
/**
 * @description 根据主键查询单条员工基本信息主数据详情信息。
 * @params pkPsndoc 员工基本信息主键编码
 *
  * @return HdlPersonBasicInfoVo 员工基本信息展示对象，用于前端详情或列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
HdlPersonBasicInfoVo queryById(String pkPsndoc);
/**
 * @description 按筛选条件分页查询员工基本信息主数据并封装为表格数据返回。
 * @params bo 员工基本信息主数据分页查询条件业务对象
 * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
 *
  * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
TableDataInfo queryPageList(HdlPersonBasicInfoBo bo, PageQuery pageQuery);
/**
 * @description 将业务对象转换为实体并新增员工基本信息主数据记录。
 * @params bo 员工基本信息主数据新增业务请求对象
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean insertByBo(HdlPersonBasicInfoBo bo);
/**
 * @description 按业务对象更新员工基本信息主数据记录并校验关键字段。
 * @params bo 员工基本信息主数据编辑业务请求对象
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean updateByBo(HdlPersonBasicInfoBo bo);
/**
 * @description 按业务校验规则批量删除员工基本信息主数据记录。
 * @params ids 主键ID集合
 * @params isValid 数据有效标记（0无效、1有效）
 *
  * @return Boolean 业务执行结果标记（true成功，false失败）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
/**
 * @description 执行selectPBIList方法，完成员工基本信息主数据相关业务处理。
 * @params pbi 员工基本信息实体对象
 *
  * @return List<HdlPersonBasicInfo> 员工基本信息列表结果，用于批量处理或前端展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
List<HdlPersonBasicInfo> selectPBIList(HdlPersonBasicInfo pbi);
}