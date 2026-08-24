package com.Lino.grid_manager_back.season.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.race.entity.Race;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_season")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Season {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String name;
    @Column(nullable = false) private Integer year;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "winner_pilot_id") private Pilot winnerPilot;
    @Builder.Default @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name = "tb_pilot_season", joinColumns = @JoinColumn(name = "season_id"), inverseJoinColumns = @JoinColumn(name = "pilot_id")) private Set<Pilot> pilots = new HashSet<>();
    @Builder.Default @OneToMany(mappedBy = "season", fetch = FetchType.LAZY) private Set<Race> races = new HashSet<>();
}
