DROP TABLE ohje;
DROP TABLE ohje_aud;

CREATE TABLE ohje (
    id bigint NOT NULL PRIMARY KEY,
    kysymys TEXT,
    vastaus TEXT
);