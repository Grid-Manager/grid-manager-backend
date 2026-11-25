package com.Lino.grid_manager_back.license.entity;

import com.Lino.grid_manager_back.pilot.entity.Pilot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_license")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class License {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "license_number", nullable = false, unique = true, length = 100)
  private String licenseNumber;

  @OneToOne(mappedBy = "license", fetch = FetchType.LAZY)
  private Pilot pilot;
}
