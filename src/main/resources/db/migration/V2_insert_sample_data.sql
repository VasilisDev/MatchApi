DO $$
DECLARE
m_id INTEGER;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM "match"
    WHERE description = 'OSFP-PAO'
      AND match_date = '2025-08-03'
      AND match_time = '12:00:00'
  ) THEN
    INSERT INTO "match" (description, match_date, match_time, team_a, team_b, sport)
    VALUES ('OSFP-PAO', '2025-08-03', '12:00:00', 'OSFP', 'PAO', 'FOOTBALL')
    RETURNING id INTO m_id;
ELSE
SELECT id INTO m_id FROM "match"
WHERE description = 'OSFP-PAO'
  AND match_date = '2025-08-03'
  AND match_time = '12:00:00';
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM match_odd
    WHERE match_id = m_id AND specifier = '1'
  ) THEN
    INSERT INTO match_odd (specifier, odd, match_id) VALUES ('1', 2.0, m_id);
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM match_odd
    WHERE match_id = m_id AND specifier = 'X'
  ) THEN
    INSERT INTO match_odd (specifier, odd, match_id) VALUES ('X', 1.5, m_id);
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM match_odd
    WHERE match_id = m_id AND specifier = '2'
  ) THEN
    INSERT INTO match_odd (specifier, odd, match_id) VALUES ('2', 3.5, m_id);
END IF;
END$$;
