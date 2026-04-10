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

@RequiredArgsConstructor
@Service
public class RemoteTokenServiceImpl implements IRemoteTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTokenServiceImpl.class);

    @Autowired
    private RemoteDataConfig remoteDataConfig;

    @Autowired
    private RestTemplate restTemplate;

    private ApiTokenResponse cachedToken;

    private final ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        ApiTokenResponse initToken = refreshTokenInternal(true);
        if (initToken != null) {
            logger.info("Remote API token initialized.");
        }
    }

    @Override
    public String getValidToken() {
        if (!isTokenValid()) {
            logger.info("Remote token missing or expired, refreshing token.");
            refreshToken();
        }
        return cachedToken != null ? cachedToken.getToken() : null;
    }

    @Override
    public ApiTokenResponse refreshToken() {
        return refreshTokenInternal(false);
    }

    private ApiTokenResponse refreshTokenInternal(boolean startupMode) {
        lock.lock();
        try {
            logger.info("Start requesting remote API token.");

            String url = remoteDataConfig.buildRemoteUrl(remoteDataConfig.getLoginPath());

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
                logger.info("Remote API token acquired, tenantId={}, token={}",
                    tokenResponse.getTenantId(), maskToken(tokenResponse.getToken()));
                return tokenResponse;
            }

            String message = "Failed to get remote API token, status=" + response.getStatusCode();
            if (startupMode) {
                logger.warn(message);
                return null;
            }
            logger.error(message);
            throw new RuntimeException(message);

        } catch (HttpClientErrorException e) {
            if (startupMode) {
                logger.warn("Startup token fetch failed, status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
                return null;
            }
            logger.error("Token request failed, status={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Token request failed, status=" + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            if (startupMode) {
                logger.warn("Startup token fetch failed, cannot connect remote API: {}", e.getMessage());
                return null;
            }
            logger.error("Remote API connection failed: {}", e.getMessage(), e);
            throw new RuntimeException("Remote API connection failed: " + e.getMessage(), e);
        } catch (Exception e) {
            if (startupMode) {
                logger.warn("Startup token fetch failed: {}", e.getMessage());
                return null;
            }
            logger.error("Unexpected error when requesting remote token", e);
            throw new RuntimeException("Unexpected error when requesting remote token: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ApiTokenResponse getCurrentTokenInfo() {
        return cachedToken;
    }

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
            logger.warn("Token expired, age={}ms, expireThreshold={}ms", tokenAge, expireTime);
            return false;
        }

        if (tokenAge > refreshTime) {
            logger.info("Token near expiry, age={}ms, refreshThreshold={}ms", tokenAge, refreshTime);
            return false;
        }

        return true;
    }

    private String maskToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return "null";
        }
        if (token.length() <= 10) {
            return token.substring(0, 1) + "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}