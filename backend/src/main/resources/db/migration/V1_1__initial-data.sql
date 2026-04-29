INSERT INTO indicator (name, description, green_threshold, yellow_threshold, red_threshold)
VALUES
    ('Liquidity Ratio', 'Monitors the organization''s liquidity risk', 1.5, 1.0, 0.5),
    ('Debt Ratio', 'Monitors the organization''s debt level', 30.0, 50.0, 70.0);

INSERT INTO books (title, author, published_year)
VALUES ('Clean Code', 'Robert C. Martin', 2008),
       ('The Pragmatic Programmer', 'Andrew Hunt', 1999),
       ('Designing Data-Intensive Applications', 'Martin Kleppmann', 2017);
