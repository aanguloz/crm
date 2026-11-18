package com.prototype.crm.service;

import com.prototype.crm.model.dto.request.ClientsReqDTO;
import com.prototype.crm.model.dto.response.ClientsRespDTO;

import java.util.List;
import java.util.UUID;

public interface ClientsService {

    ClientsRespDTO addClients (ClientsReqDTO clientsReqDTO);

    ClientsRespDTO updateClient(UUID id, ClientsReqDTO clientsReqDTO);

    void deactivateClient(UUID id);

    ClientsRespDTO getClientById(UUID id);

    List<ClientsRespDTO> searchClients (String filter);

}
