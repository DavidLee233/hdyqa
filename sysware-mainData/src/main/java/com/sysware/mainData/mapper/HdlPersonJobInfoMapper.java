package com.sysware.mainData.mapper;

import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.common.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 员工工作信息数据Mapper接口
 *
 * @author aa
 * @date 2026-01-15
 */
public interface HdlPersonJobInfoMapper extends BaseMapperPlus<HdlPersonJobInfoMapper, HdlPersonJobInfo, HdlPersonJobInfoVo> {

    /**
     * 根据条件分页查询列表
     *
     * @param pji 用户信息
     * @return 信息集合信息
     */
    public List<HdlPersonJobInfo> selectPJIList(HdlPersonJobInfo pji);
}
