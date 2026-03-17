package com.sysware.system.service;

import com.sysware.system.domain.vo.SysValidateRepetitionVo;

/**
 * 验证业务
 */
public interface ISysValidateService {
    /**
     * 判重数据是否重复
     * @param vo
     * @return
     */
   public Boolean verifyRepetition(SysValidateRepetitionVo vo);

    /**
     * 验证用户是否拥有角色
     * @param userId
     * @param roleKey
     * @return
     */
    Object verifyRoleKey(String userId, String roleKey,String roleType);
}
