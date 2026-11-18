package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.emuns.ClientsStatus;
import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.dto.response.ActivityRespDTO;
import com.prototype.crm.model.records.DashboardMetricsDTO;
import com.prototype.crm.model.records.PipelineStageDTO;
import com.prototype.crm.repository.ActivityRepository;
import com.prototype.crm.repository.ClientsRepository;
import com.prototype.crm.repository.DealRepository;
import com.prototype.crm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClientsRepository clientsRepository;
    private final DealRepository dealRepository;
    private final ActivityRepository activityRepository;

    @Override
    public DashboardMetricsDTO getExecutiveMetrics() {

        // 1. Leads Activos (Clientes con estado LEAD o QUALIFIED)
        long activeLeads = clientsRepository.countByStatusIn(List.of(ClientsStatus.LEAD, ClientsStatus.QUALIFY));
        // 2. Oportunidades en Pipeline (Todas las deals activas)
        long pipelineOpportunities = dealRepository.count();

        // 3. Tasa de Conversión (Ejemplo: Cerrados Ganados / Total de Oportunidades * 100)
        long wonDeals = dealRepository.countByStage(DealStage.CLOSED_WON);
        double conversionRate = pipelineOpportunities > 0 ? (double) wonDeals / pipelineOpportunities * 100 : 0.0;
        double roundedConversionRate = Math.round(conversionRate * 10.0) / 10.0; // Redondeo a 1 decimal

        // 4. Ingresos Proyectados (Suma de montos de oportunidades en negociación o propuestas)
        BigDecimal projectedRevenue = dealRepository.sumAmountByStageIn(
                List.of(DealStage.PROPOSAL, DealStage.NEGOTIATION)
        ).orElse(BigDecimal.ZERO);

        // ¡SOLUCIÓN! Instanciamos el record pasando todos los valores en el orden de su definición
        return new DashboardMetricsDTO(
                activeLeads,
                pipelineOpportunities,
                roundedConversionRate,
                projectedRevenue
        );

    }

    @Override
    public List<PipelineStageDTO> getPipelineFunnelData() {
        return dealRepository.getPipelineFunnelData();
    }

    @Override
    public List<ActivityRespDTO> getRecentActivities(int limit) {
        return activityRepository.findTopByOrderByScheduledDateDesc(limit)
                .stream()
                .map(activity -> {
                    ActivityRespDTO dto = new ActivityRespDTO();
                    dto.setId(activity.getId());
                    dto.setSubject(activity.getSubject());
                    dto.setType(activity.getType());
                    dto.setPriority(activity.getPriority());
                    dto.setScheduledDate(activity.getScheduledDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getClientsCountByStatus() {
        Map<String, Long> statusCounts = new HashMap<>();
        for (ClientsStatus status : ClientsStatus.values()) {
            long count = clientsRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }
        return statusCounts;
    }
}
