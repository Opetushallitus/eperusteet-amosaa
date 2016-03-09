angular.module("app")
.config($stateProvider => $stateProvider
    .state("root.koulutustoimija.yhteiset.sisalto.tiedot", {
        url: "/tiedot?versio",
        resolve: {
            historia: yhteiset => yhteiset.getList("versiot"),
            versioId: $stateParams => $stateParams.versio,
            versio: (versioId, historia) => versioId && historia.get(versioId),
        },
        controller: TiedotImpl.controller
    }));


namespace TiedotImpl {
    export const controller = ($q, $scope, $state, koulutustoimija, yhteiset, historia, versioId, versio, nimiLataaja) => {
        $scope.versio = versio;
        $scope.koulutustoimija = koulutustoimija;
        [$scope.uusin, $scope.historia] = Revisions.parseAll(historia);
        nimiLataaja($scope.uusin.muokkaaja).then(nimi => $scope.uusin.$$nimi = nimi);

        $scope.edit = EditointikontrollitService.createRestangular($scope, "yhteiset", yhteiset, {
            done: () => historia.get("uusin").then(res => {
                $scope.uusin = Revisions.parseOne(res);
                $scope.historia.unshift($scope.uusin);
            })
        });

        $scope.restoreNew = () => $state.go($state.current.name, { versio: undefined });

        if (versio) {
            $scope.uusin = Revisions.get($scope.historia, versioId);
            _.merge($scope.yhteiset, versio.plain());
            $scope.restoreRevision = () => {
                $scope.yhteiset.put().then(res => {
                    _.merge(yhteiset, versio.plain());
                    $scope.yhteiset = yhteiset;
                    NotifikaatioService.onnistui("versio-palautettu-onnistuneesti");
                    $scope.restoreNew();
                });
            };
        }

        $scope.listRevisions = () => ModalRevisions.viewRevisions($scope.historia)
            .then(res => $state.go($state.current.name, { versio: res }));
    };
}
