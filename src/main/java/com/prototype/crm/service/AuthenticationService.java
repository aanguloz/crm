package com.prototype.crm.service;

import com.prototype.crm.model.dto.AuthRespDTO;

public interface AuthenticationService {

    AuthRespDTO login(String userName, String password);

    AuthRespDTO refresh(String userName, String refreshToken);
}
