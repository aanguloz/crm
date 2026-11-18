package com.prototype.crm.model.dto.request;

import com.prototype.crm.model.dto.ClientsDTO;
import com.prototype.crm.model.dto.UsersDTO;
import com.prototype.crm.model.dto.emuns.DealStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealReqDTO {

    String title;
    String company;
    BigDecimal amount;
    DealStage stage;
    ClientsDTO client;
    UsersDTO owner;

}
