package com.Lino.grid_manager_back.category.entity;

import java.util.HashSet;
import java.util.Set;

import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.season.entity.Season;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String acronym;

    @Column(name = "propulsion_type", nullable = false, length = 50)
    private String propulsionType;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "tire_supplier", nullable = false, length = 100)
    private String tireSupplier;

    @Column(name = "governing_body", nullable = false, length = 100)
    private String governingBody;

    @Column(name = "horse_power", nullable = false)
    private Integer horsePower;

    @Column(name = "founding_year", nullable = false)
    private Integer foundingYear;

    @Column(name = "license_pattern", nullable = false, length = 100)
    private String licensePattern;

    @Builder.Default
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Pilot> pilots = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Season> seasons = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryScoringProfile> scoringProfiles = new HashSet<>();
}
