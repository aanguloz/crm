package com.prototype.crm.model.entity;

import com.prototype.crm.model.dto.emuns.ActivityPriority;
import com.prototype.crm.model.dto.emuns.ActivityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String subject;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Enumerated(EnumType.STRING)
    private ActivityPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Clients client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users owner;

    private LocalDateTime scheduledDate;

}
