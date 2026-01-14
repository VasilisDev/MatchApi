DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'sport') THEN
CREATE TYPE sport AS ENUM ('FOOTBALL', 'BASKETBALL');
END IF;
END$$;

CREATE TABLE IF NOT EXISTS match (
                                       id SERIAL PRIMARY KEY,
                                       description TEXT,
                                       match_date DATE NOT NULL,
                                       match_time TIME NOT NULL,
                                       team_a VARCHAR(255),
                                       team_b VARCHAR(255),
                                       sport sport NOT NULL
);

CREATE TABLE IF NOT EXISTS match_odd (
                                         id SERIAL PRIMARY KEY,
                                         specifier VARCHAR(50),
                                         odd DOUBLE PRECISION,
                                         match_id INTEGER NOT NULL REFERENCES "match"(id) ON DELETE CASCADE,
                                         CONSTRAINT uq_match_specifier UNIQUE (match_id, specifier)

    );
