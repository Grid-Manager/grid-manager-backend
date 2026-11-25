-- ===================================================================
-- 1. Table: CATEGORY
-- ===================================================================
CREATE TABLE tb_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    acronym VARCHAR(10) NOT NULL UNIQUE,
    propulsion_type VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    tire_supplier VARCHAR(100) NOT NULL,
    governing_body VARCHAR(100) NOT NULL,
    horse_power INT NOT NULL CHECK (horse_power > 0),
    founding_year INT NOT NULL CHECK (founding_year > 1800)
);

-- ===================================================================
-- 2. Table: LICENSE
-- ===================================================================
CREATE TABLE tb_license (
    id BIGSERIAL PRIMARY KEY,
    license_number VARCHAR(100) NOT NULL UNIQUE
);

-- ===================================================================
-- 3. Table: PILOT
-- ===================================================================
CREATE TABLE tb_pilot (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    age INT NOT NULL CHECK (age >= 18 AND age <= 60),
    pilot_number BIGINT NOT NULL UNIQUE,
    category_id BIGINT NOT NULL,
    license_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_pilot_category FOREIGN KEY (category_id) REFERENCES tb_category(id),
    CONSTRAINT fk_pilot_license FOREIGN KEY (license_id) REFERENCES tb_license(id)
);

-- ===================================================================
-- 4. Table: SEASON
-- ===================================================================
CREATE TABLE tb_season (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    year INT NOT NULL,
    winner_pilot_id BIGINT,
    CONSTRAINT fk_season_winner_pilot FOREIGN KEY (winner_pilot_id) REFERENCES tb_pilot(id)
);

-- ===================================================================
-- 5. Join Table: PILOT_SEASON (Many-to-Many Relationship)
-- ===================================================================
CREATE TABLE tb_pilot_season (
    pilot_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    PRIMARY KEY (pilot_id, season_id),
    CONSTRAINT fk_pilot_season_pilot FOREIGN KEY (pilot_id) REFERENCES tb_pilot(id) ON DELETE CASCADE,
    CONSTRAINT fk_pilot_season_season FOREIGN KEY (season_id) REFERENCES tb_season(id) ON DELETE CASCADE
);

-- ===================================================================
-- 6. Table: RACE
-- ===================================================================
CREATE TABLE tb_race (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    realization_date DATE NOT NULL,
    start_time TIMESTAMP NOT NULL,
    laps INT NOT NULL CHECK (laps > 0),
    circuit_length DOUBLE PRECISION NOT NULL CHECK (circuit_length > 0),
    fast_lap VARCHAR(20),
    has_safety_car BOOLEAN NOT NULL DEFAULT FALSE,
    safety_car_laps INT NOT NULL DEFAULT 0,
    season_id BIGINT NOT NULL,
    pilot_faster_lap_id BIGINT,
    CONSTRAINT fk_race_season FOREIGN KEY (season_id) REFERENCES tb_season(id),
    CONSTRAINT fk_race_pilot_faster_lap FOREIGN KEY (pilot_faster_lap_id) REFERENCES tb_pilot(id)
);

-- ===================================================================
-- 7. Table: CLIMATE (1:1 Relationship with RACE)
-- ===================================================================
CREATE TABLE tb_climate (
    id BIGSERIAL PRIMARY KEY,
    current_temp DOUBLE PRECISION NOT NULL CHECK (current_temp >= -10 AND current_temp <= 60),
    track_temp DOUBLE PRECISION NOT NULL CHECK (track_temp >= -10 AND track_temp <= 60),
    grip_rate DOUBLE PRECISION NOT NULL,
    wind_speed DOUBLE PRECISION NOT NULL,
    wind_direction VARCHAR(50) NOT NULL,
    rain_chance DOUBLE PRECISION NOT NULL CHECK (rain_chance >= 0 AND rain_chance <= 100),
    track_surface VARCHAR(50) NOT NULL,
    rain_intensity VARCHAR(50) NOT NULL,
    race_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_climate_race FOREIGN KEY (race_id) REFERENCES tb_race(id) ON DELETE CASCADE
);

-- ===================================================================
-- 8. Table: RACE_RESULTS
-- ===================================================================
CREATE TABLE tb_race_results (
    id BIGSERIAL PRIMARY KEY,
    position BIGINT NOT NULL CHECK (position > 0),
    points INT NOT NULL DEFAULT 0,
    race_status_pilot VARCHAR(20) NOT NULL CHECK (race_status_pilot IN ('FINISHED', 'DNF', 'DSQ')),
    penalties INT DEFAULT 0,
    yellow_flags INT DEFAULT 0,
    red_flags INT DEFAULT 0,
    flag_count INT DEFAULT 0,
    pilot_id BIGINT NOT NULL,
    race_id BIGINT NOT NULL,
    CONSTRAINT fk_race_results_pilot FOREIGN KEY (pilot_id) REFERENCES tb_pilot(id),
    CONSTRAINT fk_race_results_race FOREIGN KEY (race_id) REFERENCES tb_race(id),
    CONSTRAINT uk_race_pilot UNIQUE (race_id, pilot_id)
);