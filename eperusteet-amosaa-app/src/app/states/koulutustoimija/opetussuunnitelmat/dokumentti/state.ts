angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.dokumentti", {
    url: "/dokumentti",
    resolve: {
        dokumentti: (ops) => ops.one("dokumentti"),
        dokumenttiDto: (dokumentti) => dokumentti.one("tila").get()
    },
    views: {
        "": {
            controller: ($scope, $timeout, dokumentti, dokumenttiDto, $http, FileUploader) => {
                const dokumenttiUrl = dokumentti.getRestangularUrl();
                $scope.kuva = {};

                // Kuvien päivitys
                const paivitaKuva = (tyyppi) => {
                    $scope.kuva[tyyppi] = dokumenttiUrl
                        + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli()
                        + "&" + new Date().getTime()
                };

                paivitaKuva("kansi");
                paivitaKuva("ylatunniste");
                paivitaKuva("alatunniste");

                // Kuvien lataus
                const createUploader = (tyyppi) => {
                    var uploader = new FileUploader({
                        url: dokumenttiUrl + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli(),
                        queueLimit: '1',
                        removeAfterUpload: true
                    });
                    uploader.onSuccessItem = () => {
                        paivitaKuva(tyyppi);
                    };
                    return uploader;
                };

                $scope.poistaKuva = (tyyppi) => {
                    $http({
                        method: 'DELETE',
                        url: dokumenttiUrl + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli()
                    }).then(() => {
                        paivitaKuva(tyyppi);
                    });
                };

                $scope.kansiUploader = createUploader("kansi");
                $scope.ylatunnisteUploader = createUploader("ylatunniste");
                $scope.alatunnisteUploader = createUploader("alatunniste");

                // Generointi
                $scope.dokumenttiDto = dokumenttiDto;
                $scope.generoi = () => {
                    dokumentti.post("", "", {
                        kieli: KieliService.getSisaltokieli()
                    }).then(() => {
                        poll();
                    });
                };

                // Linkit
                $scope.linkki = dokumenttiUrl + "?kieli=" + KieliService.getSisaltokieli();

                // Edistyminen
                $scope.edistymisetCount = 5;
                $scope.edistymiset = {
                    tuntematon: 5,
                    tekstikappaleet: 1,
                    kuvat: 2,
                    viitteet: 3,
                    tyylit: 4,
                };

                //
                const poll = () => {
                    dokumentti.one("tila").get().then((dokumenttiDto) => {
                        $scope.dokumenttiDto = dokumenttiDto;
                        if (dokumenttiDto.tila === 'luodaan') {
                            // $timeout(poll, 1000);
                        }
                    });
                };

                if (dokumenttiDto && dokumenttiDto.tila === 'luodaan') {
                    // $timeout(poll, 1000);
                }
            }
        }
    }
}));
