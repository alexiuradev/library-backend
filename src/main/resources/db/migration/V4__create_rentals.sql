CREATE TABLE rentals (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         book_copy_id BIGINT NOT NULL,

                         rented_at TIMESTAMP NOT NULL DEFAULT NOW(),
                         due_at TIMESTAMP NOT NULL,
                         returned_at TIMESTAMP NULL,

                         CONSTRAINT fk_rental_user
                             FOREIGN KEY (user_id) REFERENCES users(id),

                         CONSTRAINT fk_rental_copy
                             FOREIGN KEY (book_copy_id) REFERENCES book_copies(id)
);

-- Prevent two active rentals for the same physical copy
CREATE UNIQUE INDEX ux_rentals_active_copy
    ON rentals(book_copy_id)
    WHERE returned_at IS NULL;

-- Useful queries
CREATE INDEX ix_rentals_user_active
    ON rentals(user_id)
    WHERE returned_at IS NULL;
