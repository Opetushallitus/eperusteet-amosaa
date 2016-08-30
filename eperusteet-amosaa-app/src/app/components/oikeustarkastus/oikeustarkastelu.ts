namespace Oikeudet {
    let ktOikeudet, opsOikeudet;
    export const init = _.once((oikeudet, orgoikeudet) => {
        opsOikeudet = _.indexBy(oikeudet, "_opetussuunnitelma");
        ktOikeudet = {};
        const mapClass = (luokka, nimi) => _.each(orgoikeudet[luokka], kt => {
            ktOikeudet[kt] = nimi;
        });
        mapClass("READ",        "luku");
        mapClass("READ_UPDATE", "muokkaus");
        mapClass("CRUD",        "lisays");
        mapClass("ADMIN",       "hallinta");
    });

    export const ktOikeus = (kt) => ktOikeudet[_.isObject(kt) ? kt.id : kt];

    export const opsOikeus = (ops) => {
        const oikeus = opsOikeudet[_.isObject(ops) ? ops.id : ops];
        return oikeus ? oikeus.oikeus : null;
    };

    export const onVahintaan = (vaadittu, annettu): boolean => {
        const korkeus = (oikeus) => ({
            luku: 1,
            muokkaus: 2,
            lisays: 3,
            poisto: 4,
            hallinta: 5,
        }[oikeus] || 0);
        return korkeus(annettu) >= korkeus(vaadittu);
    };
}

namespace OikeustarkasteluImpl {
    const controller = () => { };

    export const directive = ($stateParams) => {
        return {
            restrict: "A",
            controller: controller,
            scope: {
                oikeustarkastelu: "@?"
            },
            link: (scope, element, attrs) => {
                const
                    vaadittu = scope.oikeustarkastelu || "muokkaus",
                    ktId = $stateParams.ktId,
                    opsId = $stateParams.opsId,
                    opsOikeus = Oikeudet.onVahintaan(vaadittu, Oikeudet.opsOikeus(opsId)),
                    ktOikeus = Oikeudet.onVahintaan(vaadittu, Oikeudet.ktOikeus(ktId));

                if (!opsOikeus && !ktOikeus) {
                    if (element.is("button") || element.hasClass("btn")) {
                        element.attr("disabled", true);
                        element.attr("title", KaannaService.kaanna("toiminto-vaatii-oikeuden") + ": " + KaannaService.kaanna("oikeus-" + vaadittu));
                    }
                    else {
                        element.hide();
                    }
                }
            }
        }
    };
}

angular.module("app")
    .directive("oikeustarkastelu", OikeustarkasteluImpl.directive);
