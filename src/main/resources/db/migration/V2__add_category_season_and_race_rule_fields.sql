ALTER TABLE tb_category
    ADD COLUMN license_pattern VARCHAR(100) NOT NULL DEFAULT 'CATEGORIA-DATA-PILOTO';

ALTER TABLE tb_season
    ADD COLUMN category_id BIGINT NOT NULL,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD CONSTRAINT fk_season_category FOREIGN KEY (category_id) REFERENCES tb_category(id);

ALTER TABLE tb_race
    ADD COLUMN race_status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    ADD COLUMN race_type VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    ADD CONSTRAINT ck_race_status CHECK (race_status IN ('SCHEDULED', 'IN_PROGRESS', 'FINISHED')),
    ADD CONSTRAINT ck_race_type CHECK (race_type IN ('STANDARD', 'SPRINT', 'FEATURE', 'ENDURANCE')),
    ADD CONSTRAINT ck_race_safety_car_laps CHECK (safety_car_laps >= 0);

CREATE INDEX idx_season_category_id ON tb_season(category_id);
CREATE INDEX idx_race_season_id ON tb_race(season_id);
CREATE INDEX idx_race_results_pilot_id ON tb_race_results(pilot_id);
CREATE UNIQUE INDEX uq_race_results_finished_position
    ON tb_race_results(race_id, position)
    WHERE race_status_pilot = 'FINISHED';
