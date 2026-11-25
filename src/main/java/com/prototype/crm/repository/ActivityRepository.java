package com.prototype.crm.repository;

import com.prototype.crm.model.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);

    List<Activity> findAllByOrderByScheduledDateAsc();

    List<Activity> findTopByOrderByScheduledDateDesc(int limit);

    List<Activity> findByClientId(UUID clientId);

}
