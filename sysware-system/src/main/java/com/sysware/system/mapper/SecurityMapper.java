package com.sysware.system.mapper;

import com.sysware.common.core.domain.entity.SysSecurity;
import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.vo.SecurityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 密级Mapper接口
 *
 * @author zzr
 * @date 2022-01-05
 */
public interface SecurityMapper extends BaseMapperPlus<SecurityMapper,SysSecurity, SecurityVo> {

    List<SysSecurity> selectAllSecurity(@Param("securityType") String securityType);
}
