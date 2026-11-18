package com.prototype.crm.model.dto.request;

import com.prototype.crm.model.dto.UsersDTO;
import com.prototype.crm.model.dto.emuns.ActivityPriority;
import com.prototype.crm.model.dto.emuns.ActivityType;
import com.prototype.crm.model.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityReqDTO {

    String subject;
    ActivityType type;
    ActivityPriority priority;
    UsersDTO owner;
    LocalDateTime scheduledDate;

}
