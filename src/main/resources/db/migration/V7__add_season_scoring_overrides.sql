ALTER TABLE tb_category_scoring_profile
    ADD COLUMN season_id BIGINT;

ALTER TABLE tb_category_scoring_profile
    ADD CONSTRAINT fk_category_scoring_profile_season
        FOREIGN KEY (season_id) REFERENCES tb_season(id);

ALTER TABLE tb_category_scoring_profile
    DROP CONSTRAINT uq_category_scoring_profile_category_type_date;

CREATE UNIQUE INDEX uq_category_scoring_profile_category_type_date
    ON tb_category_scoring_profile (category_id, race_type, effective_from)
    WHERE season_id IS NULL;

CREATE UNIQUE INDEX uq_category_scoring_profile_season_type_date
    ON tb_category_scoring_profile (season_id, race_type, effective_from)
    WHERE season_id IS NOT NULL;
