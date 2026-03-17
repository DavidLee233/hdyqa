package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 员工基本信息数据Mapper接口
 *
 * @author aa
 * @date 2026-01-15
 */
public interface HdlPersonBasicInfoMapper extends BaseMapperPlus<HdlPersonBasicInfoMapper, HdlPersonBasicInfo, HdlPersonBasicInfoVo> {

    /**
     * 根据条件分页查询列表
     *
     * @param pbi 用户信息
     * @return 信息集合信息
     */
    public List<HdlPersonBasicInfo> selectPBIList(HdlPersonBasicInfo pbi);
}
