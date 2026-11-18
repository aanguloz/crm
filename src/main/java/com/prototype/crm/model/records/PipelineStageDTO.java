package com.prototype.crm.model.records;

import java.math.BigDecimal;

public record PipelineStageDTO(
        String stage,
        long count,
        BigDecimal totalValue
) {
}
