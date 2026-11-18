package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.request.ModuleReqDTO;
import com.prototype.crm.model.dto.response.ModuleRespDTO;
import com.prototype.crm.repository.ModuleRepository;
import com.prototype.crm.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleServiceImp implements ModuleService {

    private final ModuleRepository moduleRepository;

    @Override
    public ModuleRespDTO addModule(ModuleReqDTO moduleReqDTO) {
        return null;
    }

    @Override
    public List<ModuleRespDTO> getList(String filter) {
        return List.of();
    }
}
