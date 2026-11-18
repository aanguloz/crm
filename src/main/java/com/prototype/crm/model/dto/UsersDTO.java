package com.prototype.crm.model.dto;

import com.prototype.crm.model.records.RolesDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersDTO {

    UUID id;
    String email;
    String username;
    Set<RolesDTO> roles = new HashSet<>();

}
