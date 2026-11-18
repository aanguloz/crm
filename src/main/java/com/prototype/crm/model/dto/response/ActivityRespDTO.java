package com.prototype.crm.model.dto.response;

import com.prototype.crm.model.dto.emuns.ActivityPriority;
import com.prototype.crm.model.dto.emuns.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityRespDTO {

    UUID id;
    String subject;
    ActivityType type;
    ActivityPriority priority;
    LocalDateTime scheduledDate;

}
