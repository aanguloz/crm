package com.prototype.crm.repository;

import com.prototype.crm.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByUsername(String userName);

    Boolean existsByUsername(String userName);

    Boolean existsByEmail(String email);

}
