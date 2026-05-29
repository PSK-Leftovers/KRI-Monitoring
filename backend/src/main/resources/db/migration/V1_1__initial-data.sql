INSERT INTO indicator (name, description, green_threshold, yellow_threshold, red_threshold)
VALUES
    ('Liquidity Ratio', 'Monitors the organization''s liquidity risk', 1.5, 1.0, 0.5),
    ('Debt Ratio', 'Monitors the organization''s debt level', 30.0, 50.0, 70.0),
    ('Operational Risk Score', 'Composite score of operational incidents and losses', 20.0, 40.0, 60.0),
    ('Credit Default Rate', 'Share of the loan portfolio in default (%)', 2.0, 4.0, 6.0),
    ('Capital Adequacy Ratio', 'Regulatory capital as a share of risk-weighted assets (%)', 12.0, 10.0, 8.0),
    ('Customer Churn Rate', 'Monthly customer attrition rate (%)', 3.0, 5.0, 10.0),
    ('Fraud Detection Rate', 'Share of fraudulent transactions caught by the system (%)', 98.0, 95.0, 90.0),
    ('Non-Performing Loan Ratio', 'NPL as a share of total loan portfolio (%)', 2.0, 5.0, 8.0),
    ('Net Interest Margin', 'Net interest income over interest-earning assets (%)', 3.5, 2.5, 1.5),
    ('Cybersecurity Incident Count', 'Number of reported security incidents in the period', 1.0, 3.0, 5.0);

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Liquidity Ratio'),
       round((0.3 + random() * 1.8)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Debt Ratio'),
       round((20 + random() * 60)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Operational Risk Score'),
       round((10 + random() * 60)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Credit Default Rate'),
       round((1.0 + random() * 6.0)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Capital Adequacy Ratio'),
       round((7 + random() * 8)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Customer Churn Rate'),
       round((2 + random() * 12)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Fraud Detection Rate'),
       round((88 + random() * 11)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Non-Performing Loan Ratio'),
       round((1 + random() * 9)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Net Interest Margin'),
       round((1.0 + random() * 3.5)::numeric, 2),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

INSERT INTO indicator_value (indicator_id, value, recorded_at)
SELECT (SELECT id FROM indicator WHERE name = 'Cybersecurity Incident Count'),
       round((random() * 7)::numeric, 0),
       (CURRENT_DATE - (n || ' days')::INTERVAL)::TIMESTAMPTZ
FROM generate_series(0, 89) AS n;

UPDATE indicator i
SET status = CASE
    WHEN i.yellow_threshold IS NULL OR i.red_threshold IS NULL THEN 'UNKNOWN'
    WHEN i.yellow_threshold > i.red_threshold THEN
        CASE
            WHEN latest.value >= i.yellow_threshold THEN 'GREEN'
            WHEN latest.value >= i.red_threshold THEN 'YELLOW'
            ELSE 'RED'
        END
    ELSE
        CASE
            WHEN latest.value <= i.yellow_threshold THEN 'GREEN'
            WHEN latest.value <= i.red_threshold THEN 'YELLOW'
            ELSE 'RED'
        END
END
FROM (
    SELECT DISTINCT ON (indicator_id) indicator_id, value
    FROM indicator_value
    ORDER BY indicator_id, recorded_at DESC
) latest
WHERE i.id = latest.indicator_id;
