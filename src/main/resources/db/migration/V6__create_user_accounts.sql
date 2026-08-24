CREATE TABLE tb_user_account (
    id BIGSERIAL CONSTRAINT pk_user_account PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_account_email UNIQUE (email),
    CONSTRAINT ck_user_account_role CHECK (role IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_user_account_email ON tb_user_account(email);
