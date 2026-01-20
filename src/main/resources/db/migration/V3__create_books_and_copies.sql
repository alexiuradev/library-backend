CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       publication_year INT,
                       author_id BIGINT NOT NULL,
                       CONSTRAINT fk_book_author
                           FOREIGN KEY (author_id)
                               REFERENCES authors(id)
);

CREATE TABLE book_copies (
                             id BIGSERIAL PRIMARY KEY,
                             book_id BIGINT NOT NULL,
                             status VARCHAR(30) NOT NULL,
                             CONSTRAINT fk_copy_book
                                 FOREIGN KEY (book_id)
                                     REFERENCES books(id)
);
