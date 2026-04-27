CREATE TABLE rodiklis
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pavadinimas   VARCHAR(255) NOT NULL,
    aprasymas     TEXT,
    riba_zalia    DOUBLE PRECISION,
    riba_geltona  DOUBLE PRECISION,
    riba_raudona  DOUBLE PRECISION
);

CREATE TABLE books
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    published_year INT
);