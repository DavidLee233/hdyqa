package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
/**
 * @project npic
 * @description IHdlMainDataMappingService服务接口，定义主数据映射能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IHdlMainDataMappingService {
    /**
     * @description 根据主键查询单条主数据映射详情信息。
     * @params mapId 主数据映射主键ID
     *
      * @return HdlMainDataMappingVo 主数据映射展示对象，用于前端详情或列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public HdlMainDataMappingVo queryById(Long mapId);
    /**
     * @description 按筛选条件分页查询主数据映射并封装为表格数据返回。
     * @params bo 主数据映射分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public TableDataInfo queryPageList(HdlMainDataMappingBo bo, PageQuery pageQuery);
    /**
     * @description 将业务对象转换为实体并新增主数据映射记录。
     * @params bo 主数据映射新增业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public Boolean insertByBo(HdlMainDataMappingBo bo);
    /**
     * @description 按业务对象更新主数据映射记录并校验关键字段。
     * @params bo 主数据映射编辑业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public Boolean updateByBo(HdlMainDataMappingBo bo);
    /**
     * @description 按业务校验规则批量删除主数据映射记录。
     * @params ids 主键ID集合
     * @params isValid 数据有效标记（0无效、1有效）
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
    /**
     * @description 按类型加载主数据字段映射列表供远端转换使用。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return List<HdlMainDataMappingVo> 主数据映射列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public List<HdlMainDataMappingVo> selectMainDataMappingByType(String type);
}