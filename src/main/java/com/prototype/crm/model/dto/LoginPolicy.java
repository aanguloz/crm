package com.prototype.crm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPolicy {

    int maxFailedAttempts;
    int lockoutDurationMinutes;
    int refreshTokenExpiryMinutes;
    int accessTokenExpiryMinutes;

}
