CREATE TABLE indicator
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    description     TEXT,
    green_threshold    DOUBLE PRECISION,
    yellow_threshold  DOUBLE PRECISION,
    red_threshold  DOUBLE PRECISION
);

CREATE TABLE books
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    published_year INT
);