package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;
/**
 * @project npic
 * @description HdlMainDataMappingMapper数据访问接口，负责主数据映射持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlMainDataMappingMapper extends BaseMapperPlus<HdlMainDataMappingMapper, HdlMainDataMapping, HdlMainDataMappingVo> {
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