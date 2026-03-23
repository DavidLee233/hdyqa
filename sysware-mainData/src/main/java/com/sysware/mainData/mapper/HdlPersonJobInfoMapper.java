package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;
/**
 * @project npic
 * @description HdlPersonJobInfoMapper数据访问接口，负责员工工作信息主数据持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlPersonJobInfoMapper extends BaseMapperPlus<HdlPersonJobInfoMapper, HdlPersonJobInfo, HdlPersonJobInfoVo> {
    /**
     * @description 执行selectPJIList方法，完成员工工作信息主数据相关业务处理。
     * @params pji 员工工作信息实体对象
     *
      * @return List<HdlPersonJobInfo> 员工工作信息列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public List<HdlPersonJobInfo> selectPJIList(HdlPersonJobInfo pji);
}