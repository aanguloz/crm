package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.response.RoleRespDTO;
import com.prototype.crm.repository.RoleRepository;
import com.prototype.crm.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleRespDTO> searchRole(String filter) {
        return List.of();
    }
}
