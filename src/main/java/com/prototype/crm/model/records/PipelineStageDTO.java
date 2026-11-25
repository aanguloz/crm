package com.prototype.crm.model.records;

import com.prototype.crm.model.dto.emuns.DealStage;

import java.math.BigDecimal;

public record PipelineStageDTO(
        DealStage stage,
        long count,
        BigDecimal totalAmount
) {
}
