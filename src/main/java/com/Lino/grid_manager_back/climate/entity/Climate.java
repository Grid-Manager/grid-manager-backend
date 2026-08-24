package com.Lino.grid_manager_back.climate.entity;

import com.Lino.grid_manager_back.domain.enums.RainIntensity;
import com.Lino.grid_manager_back.domain.enums.TrackSurface;
import com.Lino.grid_manager_back.domain.enums.WindDirection;
import com.Lino.grid_manager_back.race.entity.Race;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_climate")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Climate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "current_temp", nullable = false) private Double currentTemperature;
    @Column(name = "track_temp", nullable = false) private Double trackTemperature;
    @Column(name = "grip_rate", nullable = false) private Double gripRate;
    @Column(name = "wind_speed", nullable = false) private Double windSpeed;
    @Enumerated(EnumType.STRING) @Column(name = "wind_direction", nullable = false, length = 50) private WindDirection windDirection;
    @Column(name = "rain_chance", nullable = false) private Double rainChance;
    @Enumerated(EnumType.STRING) @Column(name = "track_surface", nullable = false, length = 50) private TrackSurface trackSurface;
    @Enumerated(EnumType.STRING) @Column(name = "rain_intensity", nullable = false, length = 50) private RainIntensity rainIntensity;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "race_id", nullable = false, unique = true) private Race race;
}
