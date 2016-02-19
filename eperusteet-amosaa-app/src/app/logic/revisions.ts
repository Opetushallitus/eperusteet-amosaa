namespace Revisions {
    export const parseOne = (revision) => ({
        id: revision.numero,
        muokkaaja: revision.muokkaajaOid, // TODO: Hae käyttäjän nimi organisaatiopalvelusta promisena
        kommentti: revision.kommentti,
        date: new Date(revision.pvm)
    });

    export const parseAll = (revisions: Array<any>) => ([
        parseOne(_.first(revisions)),
        _.map(revisions, parseOne)
    ]);

    export const get = (revisions: Array<any>, id: number) => _(revisions)
        .find(rev => rev.id === id);

};
