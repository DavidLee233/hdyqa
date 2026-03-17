package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 主数据字段映射Mapper接口
 *
 * @author aa
 * @date 2026-01-14
 */
public interface HdlMainDataMappingMapper extends BaseMapperPlus<HdlMainDataMappingMapper, HdlMainDataMapping, HdlMainDataMappingVo> {

    /**
     * 根据类型查询字段映射列表
     *
     * @param type 字段类型
     * @return 字段映射列表
     */
    public List<HdlMainDataMappingVo> selectMainDataMappingByType(String type);
}
