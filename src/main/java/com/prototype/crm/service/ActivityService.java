package com.prototype.crm.service;

import com.prototype.crm.model.dto.request.ActivityReqDTO;
import com.prototype.crm.model.dto.response.ActivityRespDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ActivityService {

    ActivityRespDTO createActivity(ActivityReqDTO activityReqDTO);

    ActivityRespDTO updateActivity(UUID id, ActivityReqDTO activityReqDTO);

    List<ActivityRespDTO> getActivities(LocalDate localDate);

    List<ActivityRespDTO> getActivitiesByClient(UUID clientId);

    void deleteActivity(UUID id);

}
