angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.opetussuunnitelmat.sisalto.tiedot", {
        url: "/tiedot?versio",
        resolve: {
            historia: ops => ops.getList("versiot"),
            versioId: $stateParams => $stateParams.versio,
            versio: (versioId, historia) => versioId && historia.get(versioId)
        },
        controller: TiedotImpl.controller
    })
);

namespace TiedotImpl {
    export const controller = ($q, $scope, $state, koulutustoimija, ops, historia, versioId, versio, nimiLataaja, opsSiirtoModalService, peruste, Api, $stateParams) => {
        $scope.versio = versio;
        $scope.koulutustoimija = koulutustoimija;
        $scope.peruste = peruste;
        [$scope.uusin, $scope.historia] = Revisions.parseAll(historia);
        nimiLataaja($scope.uusin.muokkaaja).then(nimi => {
            $scope.uusin = $scope.uusin || {};
            $scope.uusin.$$nimi = nimi;
        });

        $scope.edit = EditointikontrollitService.createRestangular($scope, "ops", ops, {
            preSave: () => $q((resolve, reject) => {
                const liitteet = Api.one("koulutustoimijat", $stateParams.ktId).one("opetussuunnitelmat", $stateParams.opsId);
                return liitteet.getList("kuvat")
                    .then((res) => {
                        const preSaveObject = {
                            liitteet: res,
                        }
                        return resolve(preSaveObject);
                    })
                    .catch(reject);
            }),
            done: () =>
                historia.get("uusin").then(res => {
                    $scope.uusin = Revisions.parseOne(res);
                    $scope.historia.unshift($scope.uusin);
                })
        });

        $scope.restoreNew = () => $state.go($state.current.name, { versio: undefined });

        if (versio) {
            $scope.uusin = Revisions.get($scope.historia, versioId);
            _.merge($scope.ops, versio.plain());
            $scope.restoreRevision = () => {
                $scope.ops.put().then(res => {
                    _.merge(ops, versio.plain());
                    $scope.ops = ops;
                    NotifikaatioService.onnistui("versio-palautettu-onnistuneesti");
                    $scope.restoreNew();
                });
            };
        }

        $scope.siirraOpetussuunnitelma = () => {
            opsSiirtoModalService.siirra(koulutustoimija, ops);
        };

        $scope.listRevisions = () =>
            ModalRevisions.viewRevisions($scope.historia, nimiLataaja).then(res =>
                $state.go($state.current.name, { versio: res })
            );

        $scope.kieletVaihtuivat = () => {
            $scope.ops.julkaisukielet = _.values($scope.$$julkaisukielet);
        };

        $scope.$$julkaisukielet = _.zipObject($scope.ops.julkaisukielet, $scope.ops.julkaisukielet);
    };
}
