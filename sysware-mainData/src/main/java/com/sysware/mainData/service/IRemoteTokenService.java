package com.sysware.mainData.service;

import com.sysware.mainData.domain.ApiTokenResponse;
/**
 * @project npic
 * @description IRemoteTokenService服务接口，定义远端访问令牌能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IRemoteTokenService {
/**
 * @description 获取远端访问令牌相关信息并返回调用方。
 * @params 无
 *
  * @return String 字符串类型业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
String getValidToken();
/**
 * @description 主动刷新远端访问令牌并返回最新令牌信息。
 * @params 无
 *
  * @return ApiTokenResponse 远端令牌响应相关业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
ApiTokenResponse refreshToken();
/**
 * @description 获取远端访问令牌相关信息并返回调用方。
 * @params 无
 *
  * @return ApiTokenResponse 远端令牌响应相关业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
ApiTokenResponse getCurrentTokenInfo();
/**
 * @description 判断远端访问令牌业务状态是否满足预设条件。
 * @params 无
 *
  * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
 * @author DavidLee233
 * @date 2026/3/20
 */
boolean isTokenValid();
}