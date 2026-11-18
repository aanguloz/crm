package com.prototype.crm.model.records;

import java.util.List;

public record ModulePermissionsDTO(

        String code,
        String path,
        List<String> letPermissions

) {}
