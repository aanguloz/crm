package com.prototype.crm.model.dto.response;

import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.entity.Clients;
import com.prototype.crm.model.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealRespDTO {

    UUID id;
    String title;
    String company;
    BigDecimal amount;
    DealStage stage;
    Clients client;
    Users owner;

}
