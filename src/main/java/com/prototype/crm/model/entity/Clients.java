package com.prototype.crm.model.entity;

import com.prototype.crm.model.dto.emuns.ClientsStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String identifier;
    private String fullName;
    private String company;
    private String email;
    private String phone;
    private String sector;

    @Enumerated(EnumType.STRING)
    private ClientsStatus status;

    private BigDecimal estimatedValue;

    // Relación con el usuario que lo creó o lo atiende
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Users owner;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
