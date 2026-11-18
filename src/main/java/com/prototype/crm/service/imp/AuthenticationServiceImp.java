package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.AuthRespDTO;
import com.prototype.crm.model.dto.LoginPolicy;
import com.prototype.crm.model.entity.Users;
import com.prototype.crm.repository.UserRepository;
import com.prototype.crm.service.AuthenticationService;
import com.prototype.crm.service.util.JwtService;
import com.prototype.crm.service.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthRespDTO login(String userName, String password) {
        if (redisService.isAccountLocked(userName)) {
            throw new RuntimeException("Cuenta bloqueada temporalmente");
        }

        Users user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            redisService.incrementFailedAttempts(userName);
            throw new RuntimeException("Credenciales inválidas");
        }

        redisService.resetFailedAttempts(userName);

        LoginPolicy policy = redisService.getLoginPolicy();
        String accessToken = jwtService.generateAccessToken(user, policy.getAccessTokenExpiryMinutes());
        String refreshToken = jwtService.generateRefreshToken(user);

        redisService.storeRefreshToken(userName, refreshToken);

        return new AuthRespDTO(accessToken, refreshToken);
    }

    @Override
    public AuthRespDTO refresh(String userName, String refreshToken) {

        if (redisService.isAccountLocked(userName)) {
            throw new RuntimeException("Cuenta bloqueada temporalmente");
        }

        String storeRefreshToken = redisService.getRefreshToken(userName);
        if (storeRefreshToken == null || !storeRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        Users user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado...!"));

        LoginPolicy policy = redisService.getLoginPolicy();
        String newAccessToken = jwtService.generateAccessToken(user, policy.getAccessTokenExpiryMinutes());
        String newRefreshToken = jwtService.generateRefreshToken(user);

        redisService.storeRefreshToken(userName, newRefreshToken);

        return new AuthRespDTO(newAccessToken, newRefreshToken);
    }
}
