package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysUserConfig;
import com.sysware.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户与角色关联表 数据层
 *
 * @author
 */
public interface SysUserConfigMapper extends BaseMapperPlus<SysUserConfigMapper, SysUserConfig, Map> {

    SysUserConfig selectByUserId(@Param("userId") String userId);

    int updateByUserId(@Param("et") SysUserConfig config);
}


