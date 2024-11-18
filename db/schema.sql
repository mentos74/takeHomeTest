CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    balance NUMERIC(19, 4) DEFAULT 0 NOT NULL
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    invoice VARCHAR(255)  UNIQUE,
    service_id BIGINT ,  
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );


CREATE TABLE service_layanan (
    service_code VARCHAR(50) PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    service_icon TEXT NOT NULL,
    service_tariff NUMERIC(19, 4) NOT NULL   
);
