package com.prototype.crm.repository;

import com.prototype.crm.model.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActionRepository extends JpaRepository<Action, UUID> {
}
