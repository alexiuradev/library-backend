CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         birth_date DATE,
                         created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
