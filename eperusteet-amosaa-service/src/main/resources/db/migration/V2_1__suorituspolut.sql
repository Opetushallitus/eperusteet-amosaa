CREATE TABLE suorituspolku_rivi (
    id BIGINT NOT NULL PRIMARY KEY,
    suorituspolku_id BIGINT NOT NULL REFERENCES suorituspolku(id),
    rakennemoduuli BIGINT NOT NULL,
    jrno BIGINT,
    UNIQUE(suorituspolku_id, rakennemoduuli)
);

CREATE TABLE suorituspolku_rivi_aud (
    id BIGINT NOT NULL,
    suorituspolku_id BIGINT,
    rakennemoduuli BIGINT NOT NULL,
    jrno BIGINT,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY(rev, id)
);