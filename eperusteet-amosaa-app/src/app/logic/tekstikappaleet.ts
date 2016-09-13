interface Tekstikappaleeviite {
    id: number,
    $$depth: number,
    $$obj: any,
    $$parent: Tekstikappaleeviite,
    $$closed: boolean,
    $$poistettu: boolean,
    lapset: Array<Tekstikappaleeviite>
}

namespace Tekstikappaleet {
    export const suorituspolut = (tekstikappaleviitteet: Array<any>) =>
        _.find(tekstikappaleviitteet, (tkv) => tkv.tyyppi === "suorituspolut");

    export const tutkinnonosat = (tekstikappaleviitteet: Array<any>) =>
        _.find(tekstikappaleviitteet, (tkv) => tkv.tyyppi === "tutkinnonosat");

    export const root = (tekstikappaleviitteet: Array<any>) =>
        _.find(tekstikappaleviitteet, (tkv) => !tkv._vanhempi);

    export const uniikit = (tekstikappaleviitteet: Array<any>) =>
        _.indexBy(tekstikappaleviitteet, "id");

    export const teeRakenne = (tekstikappaleviitteet, id: number | string, depth = 0, parent = null) => {
        const viite = {};
        _.extend(viite, {
            id: id,
            $$depth: depth,
            $$obj: tekstikappaleviitteet[id],
            $$parent: parent,
            $$closed: false,
            $$poistettu: false,
            lapset: _.map(tekstikappaleviitteet[id].lapset, (lapsiId: number) =>
                teeRakenne(tekstikappaleviitteet, lapsiId, depth + 1, viite))
        });

        return viite;
    };

    export const paivitaRakenne = (viite: Tekstikappaleeviite) => {
        _(viite.lapset).forEach((lapsi: Tekstikappaleeviite) => {
            lapsi.$$parent = viite;
            paivitaRakenne(lapsi);
        }).value();
    };

    export const poista = (viite: Tekstikappaleeviite) => {
        if (viite.$$obj.tyyppi == 'tutkinnonosat' || viite.$$obj.tyyppi == 'suorituspolut' || viite.$$obj.pakollinen) {
            let parent = viite.$$parent;
            while (parent != null) {
                parent.$$poistettu = false;
                parent = parent.$$parent;
            }
            NotifikaatioService.varoitus(viite.$$obj.pakollinen
                ? "pakollinen-ei-saa-poistaa" : viite.$$obj.tyyppi + "-ei-voi-poistaa");
        } else {
            viite.$$poistettu = true;
        }
        _(viite.lapset).forEach(lapsi => poista(lapsi)).value();
    };

    export const palauta = (viite: Tekstikappaleeviite) => {
        viite.$$poistettu = false;
        _(viite.lapset).forEach(lapsi => palauta(lapsi)).value();
    };

    export const palautaYksi = (viite: Tekstikappaleeviite) => {
        viite.$$poistettu = false;
        if (viite.$$parent != null) {
            palautaYksi(viite.$$parent);
        }
    };

    export const poistaPoistetut = (viite: Tekstikappaleeviite) => {
        _.remove(viite.lapset, { $$poistettu: true });
        _(viite.lapset).forEach(viite => poistaPoistetut(viite)).value();
        return viite;
    }
}
