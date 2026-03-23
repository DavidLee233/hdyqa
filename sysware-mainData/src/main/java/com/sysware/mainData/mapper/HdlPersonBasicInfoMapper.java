package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;
/**
 * @project npic
 * @description HdlPersonBasicInfoMapper数据访问接口，负责员工基本信息主数据持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlPersonBasicInfoMapper extends BaseMapperPlus<HdlPersonBasicInfoMapper, HdlPersonBasicInfo, HdlPersonBasicInfoVo> {
    /**
     * @description 执行selectPBIList方法，完成员工基本信息主数据相关业务处理。
     * @params pbi 员工基本信息实体对象
     *
      * @return List<HdlPersonBasicInfo> 员工基本信息列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public List<HdlPersonBasicInfo> selectPBIList(HdlPersonBasicInfo pbi);
}