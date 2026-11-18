package com.prototype.crm.model.dto.request;

import com.prototype.crm.model.dto.UsersDTO;
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
public class ClientsReqDTO {

    String identifier;
    String fullName;
    String company;
    String email;
    String phone;
    String sector;
    ClientsStatus status;
    BigDecimal estimatedValue;
    UsersDTO owner;
    LocalDateTime createdAt;

}
