package com.prototype.crm.model.dto.response;

import com.prototype.crm.model.dto.emuns.ClientsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientsRespDTO {

    String id;
    String identifier;
    String fullName;
    String company;
    String email;
    String phone;
    String sector;
    String status;
    BigDecimal estimatedValue;
    String ownerId;
    LocalDateTime createdAt;

}
