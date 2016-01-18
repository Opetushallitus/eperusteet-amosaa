var Opetussuunnitelmat;
(function (Opetussuunnitelmat) {
    Opetussuunnitelmat.parsiPerustiedot = function (opetussuunnitelmat, urlGenerator) { return _(opetussuunnitelmat)
        .map(function (p) { return ({
        id: p.id,
        nimi: p.nimi,
        kuvaus: p.kuvaus,
        $$url: urlGenerator(p.id)
    }); })
        .value(); };
})(Opetussuunnitelmat || (Opetussuunnitelmat = {}));
//# sourceMappingURL=opetussuunnitelmat.js.map