namespace AmosaaApi {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["Api"]);
    };
};

angular.module("app")
.run(AmosaaApi.init)
.factory("Api", Restangular => Restangular.withConfig(config => {
    config.setBaseUrl("/eperusteet-amosaa-service/api");
    config.addResponseInterceptor((data, operation, what, url, response, deferred) => {
        if (response && response.status >= 400) {
            console.log("Fail");
            if (response.status >= 500) {
                // fataali(KaannaService.kaanna("jarjestelmavirhe-teksti", {
                //     virhekoodi: response.status
                // }), () => {
                //     // TODO Ota käyttöön myöhemmin
                //     // $state.go("root.virhe");
                // });
            }
            else if (response.data && response.data.syy) {
                let syy = response.data.syy;
                NotifikaatioService.varoitus(_.isArray(syy) ? syy[0] : syy);
            }
            else {
                NotifikaatioService.varoitus(KaannaService.kaanna("odottamaton-virhe"));
            }
        }
        return data;
    });
}));
