package com.prototype.crm.model.dto;

import lombok.Data;

@Data
public class AuthRespDTO {

    String accessToken;
    String refreshToken;

    public AuthRespDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
