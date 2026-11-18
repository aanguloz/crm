package com.prototype.crm.controller;

import com.prototype.crm.model.dto.response.ActivityRespDTO;
import com.prototype.crm.model.records.DashboardMetricsDTO;
import com.prototype.crm.model.records.PipelineStageDTO;
import com.prototype.crm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 1. Métricas Generales (Tarjetas superiores del Dashboard)
    // Frontend espera: activeLeads, pipelineOpportunities, conversionRate, projectedRevenue
    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getExecutiveMetrics() {
        DashboardMetricsDTO metrics = dashboardService.getExecutiveMetrics();
        return ResponseEntity.status(HttpStatus.OK).body(metrics);
    }

    // 2. Datos del Embudo / Funnel (Para el gráfico de barras del Pipeline)
    // Frontend espera lista de etapas con conteo y valor total
    @GetMapping("/pipeline")
    public ResponseEntity<List<PipelineStageDTO>> getPipelineData() {
        List<PipelineStageDTO> funnelData = dashboardService.getPipelineFunnelData();
        return ResponseEntity.ok(funnelData);
    }

    // 3. Actividades Recientes (Para la sección "Actividad Reciente" del Dashboard)
    @GetMapping("/recent-activities")
    public ResponseEntity<List<ActivityRespDTO>> getRecentActivities(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getRecentActivities(limit));
    }

    // 4. Distribución de Clientes por Estado (Gráfico de pastel opcional)
    @GetMapping("/clients-by-status")
    public ResponseEntity<Map<String, Long>> getClientsByStatus() {
        return ResponseEntity.ok(dashboardService.getClientsCountByStatus());
    }

}
