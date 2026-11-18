package com.prototype.crm.service;

import com.prototype.crm.model.dto.response.RoleRespDTO;

import java.util.List;

public interface RoleService {

    List<RoleRespDTO> searchRole (String filter);

}
