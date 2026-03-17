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

    // Token cache
    private ApiTokenResponse cachedToken;

    // Lock for thread-safe refresh
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Fetch token on startup. If failed, only print warn log and continue startup.
     */
    @PostConstruct
    public void init() {
        ApiTokenResponse initToken = refreshTokenInternal(true);
        if (initToken != null) {
            logger.info("Remote API token initialized successfully.");
        }
    }

    /**
     * Get valid token; refresh when missing/expired.
     */
    @Override
    public String getValidToken() {
        if (!isTokenValid()) {
            logger.info("Token is missing/expired, refreshing token.");
            refreshToken();
        }
        return cachedToken != null ? cachedToken.getToken() : null;
    }

    /**
     * Force refresh token.
     */
    @Override
    public ApiTokenResponse refreshToken() {
        return refreshTokenInternal(false);
    }

    private ApiTokenResponse refreshTokenInternal(boolean startupMode) {
        lock.lock();
        try {
            logger.info("Start fetching remote API token.");

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
                logger.info("Fetch remote API token success: {}", tokenResponse);
                return tokenResponse;
            }

            String message = "Fetch remote API token failed, status: " + response.getStatusCode();
            if (startupMode) {
                logger.warn(message);
                return null;
            }
            logger.error(message);
            throw new RuntimeException(message);

        } catch (HttpClientErrorException e) {
            if (startupMode) {
                logger.warn("Startup fetch token failed (HTTP {}), response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
                return null;
            }
            logger.error("Fetch remote API token HTTP error: {}, response: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Fetch remote API token HTTP error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            if (startupMode) {
                logger.warn("Startup fetch token failed, cannot connect remote API: {}", e.getMessage());
                return null;
            }
            logger.error("Remote API connect failed: {}", e.getMessage(), e);
            throw new RuntimeException("Remote API connect failed: " + e.getMessage(), e);
        } catch (Exception e) {
            if (startupMode) {
                logger.warn("Startup fetch token failed: {}", e.getMessage());
                return null;
            }
            logger.error("Fetch remote API token exception", e);
            throw new RuntimeException("Fetch remote API token exception: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get current cached token info.
     */
    @Override
    public ApiTokenResponse getCurrentTokenInfo() {
        return cachedToken;
    }

    /**
     * Validate token by configured expiry windows.
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
            logger.warn("Token expired, age: {}ms, expire: {}ms", tokenAge, expireTime);
            return false;
        }

        if (tokenAge > refreshTime) {
            logger.info("Token is near expiry, age: {}ms, refresh threshold: {}ms", tokenAge, refreshTime);
            return false;
        }

        return true;
    }
}
