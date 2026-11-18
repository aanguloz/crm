package com.prototype.crm.service;

import com.prototype.crm.model.dto.request.ModuleReqDTO;
import com.prototype.crm.model.dto.response.ModuleRespDTO;

import java.util.List;

public interface ModuleService {

    ModuleRespDTO addModule (ModuleReqDTO moduleReqDTO);

    List<ModuleRespDTO> getList(String filter);

}
