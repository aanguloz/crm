package com.prototype.crm.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;
    private String path;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "modulo_action",
            joinColumns = @JoinColumn(name = "modulo_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> acciones = new HashSet<>();

}
