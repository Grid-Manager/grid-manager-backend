package com.Lino.grid_manager_back.pilot.entity;

import java.util.List;

import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.result.entity.Result;
import com.Lino.grid_manager_back.season.entity.Season;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "db_pilot")
public class Pilot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @Max(150)
    @Min(10)
    private String name;

    @Column(nullable = false)
    @Max(60)
    @Min(18)
    private Integer age;

    @Column(nullable = false)
    @Max(99)
    private Long pilotNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pilot_seasons", 
        joinColumns = @JoinColumn(name = "season_id"), 
        inverseJoinColumns = @JoinColumn(name = "pilot_season_id")
    )
    private List<Season> seasons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private List<Result> results;

    public Pilot(@Max(150) @Min(10) String name, @Max(60) @Min(18) Integer age, @Max(99) Long pilotNumber,
            Category category, List<Season> seasons, List<Result> results) {
        this.name = name;
        this.age = age;
        this.pilotNumber = pilotNumber;
        this.category = category;
        this.seasons = seasons;
        this.results = results;
    }

}
