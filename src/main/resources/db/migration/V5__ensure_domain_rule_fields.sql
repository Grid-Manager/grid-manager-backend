-- Compatibiliza bancos locais que possuíam uma V2 histórica diferente antes
-- da introdução das regras de domínio. Em instalações novas todos os comandos
-- abaixo são idempotentes, pois a estrutura já é criada pela V2 atual.

ALTER TABLE tb_category
    ADD COLUMN IF NOT EXISTS license_pattern VARCHAR(100) NOT NULL DEFAULT 'CATEGORIA-DATA-PILOTO';

ALTER TABLE tb_season
    ADD COLUMN IF NOT EXISTS category_id BIGINT,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_season
    ALTER COLUMN category_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_season_category'
    ) THEN
        ALTER TABLE tb_season
            ADD CONSTRAINT fk_season_category FOREIGN KEY (category_id) REFERENCES tb_category(id);
    END IF;
END $$;

ALTER TABLE tb_race
    ADD COLUMN IF NOT EXISTS race_status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    ADD COLUMN IF NOT EXISTS race_type VARCHAR(20) NOT NULL DEFAULT 'STANDARD';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_race_status'
    ) THEN
        ALTER TABLE tb_race
            ADD CONSTRAINT ck_race_status CHECK (race_status IN ('SCHEDULED', 'IN_PROGRESS', 'FINISHED'));
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_race_type'
    ) THEN
        ALTER TABLE tb_race
            ADD CONSTRAINT ck_race_type CHECK (race_type IN ('STANDARD', 'SPRINT', 'FEATURE', 'ENDURANCE'));
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_race_safety_car_laps'
    ) THEN
        ALTER TABLE tb_race
            ADD CONSTRAINT ck_race_safety_car_laps CHECK (safety_car_laps >= 0);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_season_category_id ON tb_season(category_id);
CREATE INDEX IF NOT EXISTS idx_race_season_id ON tb_race(season_id);
CREATE INDEX IF NOT EXISTS idx_race_results_pilot_id ON tb_race_results(pilot_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_race_results_finished_position
    ON tb_race_results(race_id, position)
    WHERE race_status_pilot = 'FINISHED';
