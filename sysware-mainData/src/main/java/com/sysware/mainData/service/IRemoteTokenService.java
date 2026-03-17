package com.sysware.mainData.service;

import com.sysware.mainData.domain.ApiTokenResponse;

/**
 * 远程API Token管理服务接口
 */
public interface IRemoteTokenService {
    /**
     * 获取有效的API Token
     * @return 有效的Token字符串
     */
    String getValidToken();

    /**
     * 刷新Token（强制获取新的Token）
     * @return 新的Token响应
     */
    ApiTokenResponse refreshToken();

    /**
     * 获取当前Token信息
     * @return Token响应信息
     */
    ApiTokenResponse getCurrentTokenInfo();

    /**
     * 检查Token是否有效
     * @return true表示有效，false表示需要刷新
     */
    boolean isTokenValid();
}
