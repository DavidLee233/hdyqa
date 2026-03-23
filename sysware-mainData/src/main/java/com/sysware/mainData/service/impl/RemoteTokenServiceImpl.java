package com.sysware.mainData.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sysware.mainData.config.RemoteDataConfig;
import com.sysware.mainData.domain.ApiTokenRequest;
import com.sysware.mainData.domain.ApiTokenResponse;
import com.sysware.mainData.service.IRemoteTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @project npic
 * @description RemoteTokenServiceImpl服务实现类，负责远端访问令牌业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RequiredArgsConstructor
@Service
public class RemoteTokenServiceImpl implements IRemoteTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTokenServiceImpl.class);

    @Autowired
    private RemoteDataConfig remoteDataConfig;

    @Autowired
    private RestTemplate restTemplate;

    // Token cache
    private ApiTokenResponse cachedToken;

    // Lock for thread-safe refresh
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * @description 执行init方法，完成远端访问令牌相关业务处理。
     * @params 无
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostConstruct
    public void init() {
        ApiTokenResponse initToken = refreshTokenInternal(true);
        if (initToken != null) {
            logger.info("远端接口令牌初始化成功。");
        }
    }
    /**
     * @description 获取远端访问令牌相关信息并返回调用方。
     * @params 无
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public String getValidToken() {
        if (!isTokenValid()) {
            logger.info("令牌缺失或已过期，开始刷新令牌。");
            refreshToken();
        }
        return cachedToken != null ? cachedToken.getToken() : null;
    }
    /**
     * @description 主动刷新远端访问令牌并返回最新令牌信息。
     * @params 无
     *
      * @return ApiTokenResponse 远端令牌响应相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public ApiTokenResponse refreshToken() {
        return refreshTokenInternal(false);
    }

    /**
     * @description 执行refreshTokenInternal方法，完成远端访问令牌相关业务处理。
     * @params startupMode 启动模式标记（用于决定是否启用本地调度）
     *
      * @return ApiTokenResponse 远端令牌响应相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private ApiTokenResponse refreshTokenInternal(boolean startupMode) {
        lock.lock();
        try {
            logger.info("开始获取远端接口令牌。");

            String url = remoteDataConfig.getApiUrl() + "/api/users/login";

            ApiTokenRequest request = new ApiTokenRequest();
            request.setUsername(remoteDataConfig.getUsername());
            request.setPassword(remoteDataConfig.getPassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ApiTokenRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ApiTokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ApiTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ApiTokenResponse tokenResponse = response.getBody();
                tokenResponse.setTimestamp(System.currentTimeMillis());
                cachedToken = tokenResponse;
                logger.info("获取远端接口令牌成功：{}", tokenResponse);
                return tokenResponse;
            }

            String message = "获取远端接口令牌失败，状态码：" + response.getStatusCode();
            if (startupMode) {
                logger.warn(message);
                return null;
            }
            logger.error(message);
            throw new RuntimeException(message);

        } catch (HttpClientErrorException e) {
            if (startupMode) {
                logger.warn("启动阶段获取令牌失败（状态码{}），响应：{}",
                    e.getStatusCode(), e.getResponseBodyAsString());
                return null;
            }
            logger.error("获取远端接口令牌请求错误，状态码{}，响应：{}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("获取远端接口令牌请求错误：" + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            if (startupMode) {
                logger.warn("启动阶段获取令牌失败，无法连接远端接口：{}", e.getMessage());
                return null;
            }
            logger.error("远端接口连接失败：{}", e.getMessage(), e);
            throw new RuntimeException("远端接口连接失败：" + e.getMessage(), e);
        } catch (Exception e) {
            if (startupMode) {
                logger.warn("启动阶段获取令牌失败：{}", e.getMessage());
                return null;
            }
            logger.error("获取远端接口令牌异常", e);
            throw new RuntimeException("获取远端接口令牌异常: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
    /**
     * @description 获取远端访问令牌相关信息并返回调用方。
     * @params 无
     *
      * @return ApiTokenResponse 远端令牌响应相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public ApiTokenResponse getCurrentTokenInfo() {
        return cachedToken;
    }
    /**
     * @description 判断远端访问令牌业务状态是否满足预设条件。
     * @params 无
     *
      * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public boolean isTokenValid() {
        if (cachedToken == null || StringUtils.isEmpty(cachedToken.getToken())) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long tokenAge = currentTime - cachedToken.getTimestamp();

        long expireTime = remoteDataConfig.getTokenExpireTime();
        long refreshTime = remoteDataConfig.getTokenRefreshTime();

        if (tokenAge > expireTime) {
            logger.warn("令牌已过期，存活时长={}毫秒，过期阈值={}毫秒", tokenAge, expireTime);
            return false;
        }

        if (tokenAge > refreshTime) {
            logger.info("令牌接近过期，存活时长={}毫秒，刷新阈值={}毫秒", tokenAge, refreshTime);
            return false;
        }

        return true;
    }
}