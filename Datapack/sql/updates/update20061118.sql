ALTER TABLE clan_data ADD COLUMN reputation_score INT NOT NULL DEFAULT 0;

ALTER TABLE characters ADD COLUMN subpledge INT NOT NULL DEFAULT 0;