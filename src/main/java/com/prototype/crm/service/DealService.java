package com.prototype.crm.service;

import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.dto.request.DealReqDTO;
import com.prototype.crm.model.dto.response.DealRespDTO;

import java.util.List;
import java.util.UUID;

public interface DealService {

    DealRespDTO createDeal(DealReqDTO dealReqDTO);

    DealRespDTO updateDeal(UUID id, DealReqDTO dealReqDTO);

    DealRespDTO moveDealStage(UUID id, DealStage newStage);

    void deleteDeal (UUID id);

    List<DealRespDTO> searchDeal(String filter);
}
