package com.prototype.crm.service;

import com.prototype.crm.model.dto.request.ActionReqDTO;
import com.prototype.crm.model.dto.response.ActionRespDTO;

public interface ActionService {

    ActionRespDTO addAction(ActionReqDTO actionReqDTO);

}
