DELETE FROM indicator_thresholds;

WITH base (name, green, yellow, red) AS (
    VALUES
        ('Liquidity Ratio',              1.5,  1.0,  0.5),
        ('Debt Ratio',                   30.0, 50.0, 70.0),
        ('Operational Risk Score',       20.0, 40.0, 60.0),
        ('Credit Default Rate',          2.0,  4.0,  6.0),
        ('Capital Adequacy Ratio',       12.0, 10.0, 8.0),
        ('Customer Churn Rate',          3.0,  5.0,  10.0),
        ('Fraud Detection Rate',         98.0, 95.0, 90.0),
        ('Non-Performing Loan Ratio',    2.0,  5.0,  8.0),
        ('Net Interest Margin',          3.5,  2.5,  1.5),
        ('Cybersecurity Incident Count', 1.0,  3.0,  5.0)
),
indicator_base AS (
    SELECT i.id, b.green, b.yellow, b.red
    FROM base b
    JOIN indicator i ON i.name = b.name
),
changes AS (
    SELECT id, green, yellow, red, 90 AS days_ago, 1.0 AS factor FROM indicator_base
    UNION ALL
    SELECT id, green, yellow, red, 1, 1.0 FROM indicator_base
    UNION ALL
    SELECT ib.id, ib.green, ib.yellow, ib.red,
           (10 + random() * 70)::int,
           round((0.80 + random() * 0.40)::numeric, 2)
    FROM indicator_base ib
    CROSS JOIN LATERAL generate_series(1, (1 + floor(random() * 4))::int)
)
INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
SELECT id,
       round((green  * factor)::numeric, 2),
       round((yellow * factor)::numeric, 2),
       round((red    * factor)::numeric, 2),
       (CURRENT_DATE - (days_ago || ' days')::INTERVAL)::TIMESTAMPTZ
FROM changes;
