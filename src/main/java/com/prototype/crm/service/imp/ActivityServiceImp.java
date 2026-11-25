package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.request.ActivityReqDTO;
import com.prototype.crm.model.dto.response.ActivityRespDTO;
import com.prototype.crm.model.entity.Activity;
import com.prototype.crm.repository.ActivityRepository;
import com.prototype.crm.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImp implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
    public ActivityRespDTO createActivity(ActivityReqDTO activityReqDTO) {

        Activity activity = new Activity();
        activity.setSubject(activityReqDTO.getSubject());
        activity.setType(activityReqDTO.getType());
        activity.setPriority(activityReqDTO.getPriority());
        activity.setScheduledDate(activityReqDTO.getScheduledDate() != null
                ? activityReqDTO.getScheduledDate()
                : LocalDateTime.now());
        // activity.setOwner(userRepository.findById(dto.getOwner().getId()).orElseThrow()); // Descomentar si necesitas validar el usuario

        Activity saved = activityRepository.save(activity);
        return mapToRespDTO(saved);

    }

    @Override
    public ActivityRespDTO updateActivity(UUID id, ActivityReqDTO activityReqDTO) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada con ID: " + id));

        if (activityReqDTO.getSubject() != null) {
            activity.setSubject(activityReqDTO.getSubject());
        }
        if (activityReqDTO.getType() != null) {
            activity.setType(activityReqDTO.getType());
        }
        if (activityReqDTO.getPriority() != null) {
            activity.setPriority(activityReqDTO.getPriority());
        }
        if (activityReqDTO.getScheduledDate() != null) {
            activity.setScheduledDate(activityReqDTO.getScheduledDate());
        }

        activity.setSubject(activityReqDTO.getSubject());
        activity.setType(activityReqDTO.getType());
        activity.setPriority(activityReqDTO.getPriority());
        activity.setScheduledDate(activityReqDTO.getScheduledDate());

        return mapToRespDTO(activityRepository.save(activity));

    }

    @Override
    public List<ActivityRespDTO> getActivities(LocalDate localDate) {

        List<Activity> activities;
        if (localDate != null) {
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();
            activities = activityRepository.findByScheduledDateBetween(start, end);
        } else {
            // Trae actividades futuras o pendientes ordenadas por fecha
            activities = activityRepository.findAllByOrderByScheduledDateAsc();
        }
        return activities.stream().map(this::mapToRespDTO).collect(Collectors.toList());

    }

    @Override
    public List<ActivityRespDTO> getActivitiesByClient(UUID clientId) {

        return activityRepository.findByClientId(clientId).stream()
                .map(this::mapToRespDTO)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteActivity(UUID id) {

        if (!activityRepository.existsById(id)) {
            throw new RuntimeException("Actividad no encontrada con ID: " + id);
        }
        activityRepository.deleteById(id);

    }

    private ActivityRespDTO mapToRespDTO(Activity activity) {

        ActivityRespDTO dto = new ActivityRespDTO();
        dto.setId(activity.getId());
        dto.setSubject(activity.getSubject());
        dto.setType(activity.getType());
        dto.setPriority(activity.getPriority());
        dto.setScheduledDate(activity.getScheduledDate());
        return dto;

    }
}
