angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.dokumentti", {
    url: "/dokumentti",
    resolve: {
        dokumentti: (ops) => ops.one("dokumentti"),
        dokumenttiDto: (dokumentti) => dokumentti.customGET("tila", {
            kieli: KieliService.getSisaltokieli()
        })
    },
    views: {
        "": {
            controller: ($scope, $timeout, dokumentti, dokumenttiDto, $http, FileUploader, $rootScope, $cookies) => {
                const dokumenttiUrl = dokumentti.getRestangularUrl();
                $scope.dokumenttiDto = dokumenttiDto;
                $scope.kuva = {};
                $scope.edistymisetCount = 5;
                $scope.edistymiset = {
                    tuntematon: 5,
                    tekstikappaleet: 1,
                    kuvat: 2,
                    viitteet: 3,
                    tyylit: 4,
                };

                $scope.paivitaAsetukset = () => {
                    $scope.dokumenttiDto.put({
                        kieli: KieliService.getSisaltokieli()
                    }).then(dto => $scope.dokumenttiDto = dto);
                };

                $scope.poistaKuva = (tyyppi) => {
                    $http({
                        method: 'DELETE',
                        url: dokumenttiUrl + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli()
                    }).then(() => {
                        $scope.dokumenttiDto[tyyppi] = null;
                        paivitaKuva(tyyppi);
                    });
                };

                // Generointi
                $scope.generoi = () => {
                    dokumentti.post("", "", {
                        kieli: KieliService.getSisaltokieli()
                    }).then(() => {
                        poll();
                    });
                };

                // Kuvien päivitys
                const paivitaKuva = (tyyppi) => {
                    $scope.kuva[tyyppi] = dokumenttiUrl
                        + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli()
                        + "&" + new Date().getTime()
                };

                // Kuvien lataus
                const createUploader = (tyyppi) => {
                    var uploader = new FileUploader({
                        url: dokumenttiUrl + "/kuva?tyyppi=" + tyyppi + "&kieli=" + KieliService.getSisaltokieli(),
                        headers: {
                            CSRF: $cookies.get("CSRF")
                        },
                        queueLimit: '1',
                        removeAfterUpload: true
                    });
                    uploader.onSuccessItem = (item, res) => {
                        $scope.dokumenttiDto = res;
                        paivitaKuva(tyyppi);
                    };
                    return uploader;
                };

                // Linkit
                const createLink = () => {
                    $scope.linkki = dokumenttiUrl + "?kieli=" + KieliService.getSisaltokieli();
                };

                const poll = () => {
                    dokumentti.customGET("tila", {
                        kieli: KieliService.getSisaltokieli()
                    }).then((dokumenttiDto) => {
                        $scope.dokumenttiDto = dokumenttiDto;
                        if (dokumenttiDto.tila === 'luodaan' || dokumenttiDto.tila === 'jonossa') {
                            $timeout(poll, 1000);
                        }
                    });
                };

                const init = () => {
                    $scope.kansikuvaUploader = createUploader("kansikuva");
                    $scope.ylatunnisteUploader = createUploader("ylatunniste");
                    $scope.alatunnisteUploader = createUploader("alatunniste");

                    dokumentti.customGET("tila", {
                        kieli: KieliService.getSisaltokieli()
                    }).then(dto => {
                        $scope.dokumenttiDto = dto;
                        createLink();
                        paivitaKuva("kansikuva");
                        paivitaKuva("ylatunniste");
                        paivitaKuva("alatunniste");

                        if (dokumenttiDto && (dokumenttiDto.tila === 'luodaan' || dokumenttiDto.tila === 'jonossa')) {
                            $timeout(poll, 1000);
                        }
                    });
                };

                init();

                $rootScope.$on("changed:sisaltokieli", () => {
                    init();
                });
            }
        }
    }
}));
