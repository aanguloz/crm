package com.prototype.crm.model.records;

import java.math.BigDecimal;

public record PipeLinesStageDTO(
        String stage,
        long count,
        BigDecimal totalValue,
        double percentage
) {}
