package com.sysware.common.core.service;

import com.sysware.common.core.domain.entity.SysSecurity;

/**
 * 通用 密级服务类
 *
 * @author
 */
public interface SecurityService {

    /**
     * 通过密级ID查询密级名称
     *
     * @param securityIds 密级ID串逗号分隔
     * @return 密级名称
     */
    String selectSecurityNameById(String securityIds,String type);

    /**
     * 通过密级ID查询密级值
     *
     * @param securityIds 密级ID串逗号分隔
     * @return 密级值
     */
    Long selectSecurityValueById(String securityIds,String type);

    /**
     *
     * @param securityName 密级名称
     * @param type  密级类型
     * @return 密级ID
     */
    String selectSecurityIdByNameAndType(String securityName,String type);

}
