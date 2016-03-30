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
            controller: ($scope, $timeout, dokumentti, dokumenttiDto, $http) => {
                $scope.dokumenttiDto = dokumenttiDto;
                $scope.generoi = () => {
                    dokumentti.post("", "", {
                        kieli: KieliService.getSisaltokieli()
                    }).then(() => {
                        poll();
                    });
                };

                $scope.linkki = dokumentti.getRestangularUrl() + "?kieli=" + KieliService.getSisaltokieli();
                $scope.kansi = dokumentti.getRestangularUrl() + "/lataaKuva?tyyppi=kansi&kieli=" + KieliService.getSisaltokieli();
                $scope.ylatunniste = dokumentti.getRestangularUrl() + "/lataaKuva?tyyppi=ylatunniste&kieli=" + KieliService.getSisaltokieli();
                $scope.alatunniste = dokumentti.getRestangularUrl() + "/lataaKuva?tyyppi=alatunniste&kieli=" + KieliService.getSisaltokieli();

                $scope.edistymisetCount = 5;
                $scope.edistymiset = {
                    "tuntematon": 5,
                    "tekstikappaleet": 1,
                    "kuvat": 2,
                    "viitteet": 3,
                    "tyylit": 4,
                };

                $scope.uploadFile = (files) => {
                    var fd = new FormData();
                    //Take the first selected file
                    fd.append("file", files[0]);

                    $http.post(dokumentti.getRestangularUrl()
                        + "/lisaaKuva?tyyppi=kansi&kieli="
                        + KieliService.getSisaltokieli(), fd, {
                        withCredentials: true,
                        headers: {'Content-Type': undefined },
                        transformRequest: angular.identity
                    }).success(function () {
                        console.log("success")
                    }).error(function () {
                        console.log("error");
                    });

                };


                var poll = () => {
                    dokumentti.one("tila").get().then((dokumenttiDto) => {
                        $scope.dokumenttiDto = dokumenttiDto;
                        if (dokumenttiDto.tila === 'luodaan') {
                            $timeout(poll, 1000);
                        }
                    });
                };

                if (dokumenttiDto !== undefined && dokumenttiDto.tila === 'luodaan') {
                    $timeout(poll, 1000);
                }
            }
        }
    }
}));
