package com.prototype.crm.model.records;

import java.math.BigDecimal;

public record DashboardMetricsDTO(
        long activeLeads,
        long pipelineOpportunities,
        double conversionRate,
        BigDecimal projectedRevenue
) {}


