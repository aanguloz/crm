package com.prototype.crm.model.records;

import java.util.List;

public record UserPermissionDTO(

        Long userId,
        String userName,
        List<RolesDTO> roles,
        List<ModulePermissionsDTO> permissions

) {}
