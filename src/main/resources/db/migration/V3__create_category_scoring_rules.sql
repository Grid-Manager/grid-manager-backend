CREATE TABLE tb_category_scoring_profile (
    id BIGSERIAL CONSTRAINT pk_category_scoring_profile PRIMARY KEY,
    category_id BIGINT NOT NULL,
    race_type VARCHAR(20) NOT NULL,
    effective_from DATE NOT NULL,
    fastest_lap_bonus INT NOT NULL DEFAULT 0,
    fastest_lap_requires_top_ten BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_category_scoring_profile_category FOREIGN KEY (category_id) REFERENCES tb_category(id),
    CONSTRAINT uq_category_scoring_profile_category_type_date UNIQUE (category_id, race_type, effective_from),
    CONSTRAINT ck_category_scoring_profile_fastest_lap_bonus CHECK (fastest_lap_bonus >= 0)
);

CREATE TABLE tb_category_scoring_rule (
    id BIGSERIAL CONSTRAINT pk_category_scoring_rule PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    finishing_position INT NOT NULL,
    points INT NOT NULL,
    CONSTRAINT fk_category_scoring_rule_profile FOREIGN KEY (profile_id) REFERENCES tb_category_scoring_profile(id) ON DELETE CASCADE,
    CONSTRAINT uq_category_scoring_rule_profile_position UNIQUE (profile_id, finishing_position),
    CONSTRAINT ck_category_scoring_rule_position CHECK (finishing_position > 0),
    CONSTRAINT ck_category_scoring_rule_points CHECK (points >= 0)
);

CREATE INDEX idx_category_scoring_profile_category_id ON tb_category_scoring_profile(category_id);
CREATE INDEX idx_category_scoring_rule_profile_id ON tb_category_scoring_rule(profile_id);
