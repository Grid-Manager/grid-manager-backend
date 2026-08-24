package com.Lino.grid_manager_back.pilot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_blocked_pilot_name_term")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedPilotNameTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "normalized_term", nullable = false, unique = true, length = 150)
    private String normalizedTerm;
}
