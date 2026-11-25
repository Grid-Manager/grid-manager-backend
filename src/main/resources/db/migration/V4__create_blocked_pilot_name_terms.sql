CREATE TABLE tb_blocked_pilot_name_term (
    id BIGSERIAL CONSTRAINT pk_blocked_pilot_name_term PRIMARY KEY,
    normalized_term VARCHAR(150) NOT NULL,
    CONSTRAINT uq_blocked_pilot_name_term_normalized_term UNIQUE (normalized_term)
);
