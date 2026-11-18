package com.prototype.crm.service;

import com.prototype.crm.model.dto.response.ActivityRespDTO;
import com.prototype.crm.model.records.DashboardMetricsDTO;
import com.prototype.crm.model.records.PipelineStageDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    DashboardMetricsDTO getExecutiveMetrics();

    List<PipelineStageDTO> getPipelineFunnelData();

    List<ActivityRespDTO>getRecentActivities(int limit);

    Map<String, Long> getClientsCountByStatus();

}
