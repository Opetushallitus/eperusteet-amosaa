CREATE TABLE opetussuunnitelma_osaamisalat (
    opetussuunnitelma_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    osaamisalat CHARACTER VARYING(255)
);

CREATE TABLE opetussuunnitelma_osaamisalat_aud (
    opetussuunnitelma_id BIGINT NOT NULL,
    osaamisalat VARCHAR(255) NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE opetussuunnitelma_tutkintonimikkeet (
    opetussuunnitelma_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    tutkintonimikkeet CHARACTER VARYING(255)
);

CREATE TABLE opetussuunnitelma_tutkintonimikkeet_aud (
    opetussuunnitelma_id BIGINT NOT NULL,
    tutkintonimikkeet VARCHAR(255) NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);
