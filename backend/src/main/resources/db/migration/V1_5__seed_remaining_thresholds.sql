INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 20.0, 40.0, 60.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Operational Risk Score';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 2.0, 4.0, 6.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Credit Default Rate';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 12.0, 10.0, 8.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Capital Adequacy Ratio';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 3.0, 5.0, 10.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Customer Churn Rate';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 98.0, 95.0, 90.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Fraud Detection Rate';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 2.0, 5.0, 8.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Non-Performing Loan Ratio';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 3.5, 2.5, 1.5, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Net Interest Margin';

INSERT INTO indicator_thresholds (indicator_id, green_threshold, yellow_threshold, red_threshold, recorded_at)
    SELECT id, 1.0, 3.0, 5.0, CURRENT_TIMESTAMP FROM indicator WHERE name = 'Cybersecurity Incident Count';
