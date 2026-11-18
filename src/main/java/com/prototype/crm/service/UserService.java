package com.prototype.crm.service;

import com.prototype.crm.model.dto.request.UserReqDTO;
import com.prototype.crm.model.dto.response.UserRespDTO;

public interface UserService {

    UserRespDTO addUser(UserReqDTO users);

}
