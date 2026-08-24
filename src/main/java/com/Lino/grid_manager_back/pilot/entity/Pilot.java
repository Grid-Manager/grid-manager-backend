package com.Lino.grid_manager_back.pilot.entity;

import java.util.HashSet;
import java.util.Set;

import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.license.entity.License;
import com.Lino.grid_manager_back.result.entity.Result;
import com.Lino.grid_manager_back.season.entity.Season;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tb_pilot")
public class Pilot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "pilot_number", nullable = false, unique = true)
    private Long pilotNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "license_id", nullable = false, unique = true)
    private License license;

    @Builder.Default
    @ManyToMany(mappedBy = "pilots", fetch = FetchType.LAZY)
    private Set<Season> seasons = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "pilot", fetch = FetchType.LAZY)
    private Set<Result> results = new HashSet<>();

}
