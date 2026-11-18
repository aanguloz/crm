package com.prototype.crm.service.util;

import com.prototype.crm.model.dto.LoginPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_POLICY_KEY = "auth:policy";

    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final int DEFAULT_LOCK_TIME_MINUTES = 15;

    public LoginPolicy getLoginPolicy() {
        Object value = redisTemplate.opsForValue().get(LOGIN_POLICY_KEY);

        if (!(value instanceof LoginPolicy)) {
            LoginPolicy policy = new LoginPolicy();
            policy.setMaxFailedAttempts(DEFAULT_MAX_ATTEMPTS);
            policy.setLockoutDurationMinutes(DEFAULT_LOCK_TIME_MINUTES);
            policy.setAccessTokenExpiryMinutes(15);
            policy.setRefreshTokenExpiryMinutes(60);
            saveLoginPolicy(policy);
            return policy;
        }
        return (LoginPolicy) value;
    }

    public void incrementFailedAttempts(String username) {
        String key = "auth:failed_attempts:" + username;
        LoginPolicy policy = getLoginPolicy();

        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, policy.getLockoutDurationMinutes(), TimeUnit.MINUTES);
        }

        if (attempts != null && attempts >= policy.getMaxFailedAttempts()) {
            String lockKey = "auth:locked:" + username;
            redisTemplate.opsForValue().set(lockKey, "1",
                    policy.getLockoutDurationMinutes(), TimeUnit.MINUTES);
        }
    }

    public boolean isAccountLocked(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("auth:locked:" + username));
    }

    public void storeRefreshToken(String username, String token) {
        LoginPolicy policy = getLoginPolicy();
        String key = "auth:refresh_token:" + username;
        redisTemplate.opsForValue().set(key, token,
                policy.getRefreshTokenExpiryMinutes(), TimeUnit.MINUTES);
    }

    public void saveLoginPolicy(LoginPolicy policy) {
        redisTemplate.opsForValue().set(LOGIN_POLICY_KEY, policy);
    }

    public void invalidateRefreshToken(String username) {
        redisTemplate.delete("auth:refresh_token:" + username);
    }

    public int getFailedAttempts(String username) {
        String key = "auth:failed_attempts:" + username;
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        return attempts != null ? attempts : 0;
    }

    public void resetFailedAttempts(String username) {
        redisTemplate.delete("auth:failed_attempts:" + username);
        redisTemplate.delete("auth:locked:" + username);
    }

    public Long getLockTimeRemaining(String username) {
        String lockKey = "auth:locked:" + username;
        Long expire = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return expire != null && expire > 0 ? expire : 0L;
    }

    public String getRefreshToken(String username) {
        return (String) redisTemplate.opsForValue().get("auth:refresh_token:" + username);
    }

}
