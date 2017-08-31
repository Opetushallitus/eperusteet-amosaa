CREATE TABLE opetussuunnitelma_liite(
    opetussuunnitelma_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    liite_id UUID NOT NULL REFERENCES liite(id),
    PRIMARY KEY (opetussuunnitelma_id, liite_id)
);

CREATE TABLE opetussuunnitelma_liite_aud(
    opetussuunnitelma_id BIGINT,
    liite_id UUID,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    PRIMARY KEY (opetussuunnitelma_id, liite_id, rev)
);

-- Kopioi koulutustoimijoihin liitetyt liitelinkit opetussuunnitelmille vastaaviin
with ktliitteet as (
    select distinct ops.id, ktliite.koulutustoimija_id, ktliite.liite_id
    from opetussuunnitelma ops, koulutustoimija_liite ktliite
    where not exists (
        select opsliite.opetussuunnitelma_id from opetussuunnitelma_liite opsliite where opsliite.opetussuunnitelma_id = ops.id
    )
)
insert into opetussuunnitelma_liite (opetussuunnitelma_id, liite_id)
select id, liite_id from ktliitteet;