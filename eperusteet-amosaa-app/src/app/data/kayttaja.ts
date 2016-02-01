module Kayttaja {
    let _Api, _Cas, _$q;

    export const init = (Api, Cas, $q) => {
        _Api = Api;
        _Cas = Cas;
        _$q = $q;
    };

    export const kayttaja = () => _Api.one("kayttaja").get();

    export const oikeudet = () => _Api.one("kayttaja").all("oikeudet").get();

    // TODO: Map by koulutustoimija
    export const tyoryhmat = () => _Api.one("kayttaja").all("tyoryhmat").get();

    export const casRoles = () => _$q((resolve) =>
        _Cas.one("myroles").get()
            .then(resolve)
            .catch(() => resolve({})));

    export const casMe = () => _$q((resolve) =>
        _Cas.one("me").get()
            .then(resolve)
            .catch(() => resolve({})));
};


angular.module("app")
.run(($injector) => $injector.invoke(Kayttaja.init))
.factory("Cas", Restangular => Restangular.withConfig(config => {
    config.setBaseUrl("/cas");
}));
